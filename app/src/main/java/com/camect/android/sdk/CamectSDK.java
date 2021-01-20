package com.camect.android.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.camect.android.sdk.model.HomeInfo;
import com.camect.android.sdk.network.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class CamectSDK {

    private static CamectSDK sInstance;

    public static CamectSDK getInstance() {
        return sInstance;
    }

    public static void init(Context context, String id, String password) {
        synchronized (CamectSDK.class) {
            if (sInstance == null) {
                sInstance = new CamectSDK(context, id, "admin", password);
            }
        }
    }

    public static void init(Context context, String id, String username, String password) {
        synchronized (CamectSDK.class) {
            if (sInstance == null) {
                sInstance = new CamectSDK(context, id, username, password);
            }
        }
    }

    public static boolean isInitialized() {
        return sInstance != null;
    }

    private final Context mContext;
    private final String  mPassword;
    private final String  mUserAgent;
    private final String  mUsername;

    private String       mHost;
    private OkHttpClient mHttpClient;

    private CamectSDK(Context context, @NonNull String host, @NonNull String username,
                      @NonNull String password) {

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("An invalid parameter was passed.");
        }


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        mContext = context.getApplicationContext();

        mUserAgent = WebSettings.getDefaultUserAgent(mContext);
        mUsername = username;
        mPassword = password;

        updateHost(host);
    }

    @WorkerThread
    public HomeInfo getHomeInfo() {
        Request request = getStandardRequest()
                .url(mHost + "GetHomeInfo")
                .get()
                .build();

        try (Response response = mHttpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                String json = response.body().string();

                return HomeInfo.inflate(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Request.Builder getStandardRequest() {
        Request.Builder builder = new Request.Builder()
                .header("Accept", "application/json")
                .header("User-Agent", mUserAgent);

        return builder;
    }

    public void updateHost(String host) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(new Cache(mContext.getCacheDir(), 1024 * 1024 * 2))
                .pingInterval(30, TimeUnit.SECONDS) // for the websocket
                .authenticator((route, response) -> {
                    String credential = Credentials.basic(mUsername, mPassword);
                    return response.request().newBuilder().header("Authorization",
                            credential).build();
                });

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new LoggingInterceptor());
        }

        // are we using a CamectID or an IPv4 Address
        String[] elements = host.split("\\.");
        if (elements.length == 4) {
            // IPv4 Address
            for (String element : elements) {
                if (TextUtils.isEmpty(element)) {
                    throw new IllegalArgumentException("IPv4 Address is invalid");
                }

                int octect = Integer.parseInt(element);
                if (octect < 0 || octect > 255) {
                    throw new IllegalArgumentException("IPv4 Address is invalid");
                }
            }

            builder.hostnameVerifier((hostname, session) -> true);
        } else {
            // Camect ID
            if (host.length() < 9) {
                throw new IllegalArgumentException("Camect ID is invalid");
            }

            host = host.substring(0, 9);
            host += ".l.home.camect.com";
        }

        mHost = "https://" + host + ":443/api/";

        mHttpClient = builder.build();
    }
}
