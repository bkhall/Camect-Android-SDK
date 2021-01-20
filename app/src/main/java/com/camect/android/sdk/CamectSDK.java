package com.camect.android.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.camect.android.sdk.model.HomeInfo;
import com.camect.android.sdk.network.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
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

    private final Context      mContext;
    private final OkHttpClient mHttpClient;
    private final String       mId;
    private final String       mPassword;
    private final String       mUserAgent;
    private final String       mUsername;

    private CamectSDK(Context context, @NonNull String id, @NonNull String username,
                      @NonNull String password) {

        if (TextUtils.isEmpty(id) || id.length() < 9 || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("An invalid parameter was passed.");
        }

        mContext = context.getApplicationContext();

        mUserAgent = WebSettings.getDefaultUserAgent(mContext);

        mId = id.substring(0, 9);
        mUsername = username;
        mPassword = password;

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(new Cache(context.getCacheDir(), 1024 * 1024 * 2))
                .pingInterval(30, TimeUnit.SECONDS) // for the websocket
                .authenticator((route, response) -> {
                    String credential = Credentials.basic(mUsername, mPassword);
                    return response.request().newBuilder().header("Authorization", credential).build();
                });

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            builder.addInterceptor(new LoggingInterceptor());
        }

        mHttpClient = builder.build();
    }

    @WorkerThread
    public HomeInfo getHomeInfo() {
        Request request = getStandardRequest()
                .url(getHost() + "GetHomeInfo")
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

    private String getHost() {
        return "https://" + mId + ".l.home.camect.com:443/api/";
    }

    private Request.Builder getStandardRequest() {
        Request.Builder builder = new Request.Builder()
                .header("Accept", "application/json")
                .header("User-Agent", mUserAgent);

        return builder;
    }
}
