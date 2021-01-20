package com.camect.android.sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.camect.android.sdk.model.Camera;
import com.camect.android.sdk.model.HomeInfo;
import com.camect.android.sdk.network.LoggingInterceptor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
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

    public static void setLoggingEnabled(boolean enable) {
        if (enable) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.uprootAll();
        }
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

        mContext = context.getApplicationContext();
        mUserAgent = WebSettings.getDefaultUserAgent(mContext);
        mUsername = username;
        mPassword = password;

        updateHost(host);
    }

    @WorkerThread
    public Bitmap getCameraSnapshot(@NonNull String cameraId, int width, int height) {
        HttpUrl url = HttpUrl.parse(mHost + "SnapshotCamera").newBuilder()
                .addQueryParameter("CamId", cameraId)
                .addQueryParameter("Width", String.valueOf(width))
                .addQueryParameter("Height", String.valueOf(height))
                .build();

        Request request = getStandardRequest()
                .url(url)
                .get()
                .build();

        try (Response response = mHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();

                JSONObject jsonObject = new JSONObject(json);
                String base64ImageString = jsonObject.getString("jpeg_data");

                byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);

                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @NonNull
    @WorkerThread
    public ArrayList<Camera> getCameras() {
        Request request = getStandardRequest()
                .url(mHost + "ListCameras")
                .get()
                .build();

        ArrayList<Camera> cameras = new ArrayList<>();

        try (Response response = mHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();

                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("camera");

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);

                    cameras.add(Camera.inflate(jsonObject));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cameras;
    }

    @Nullable
    @WorkerThread
    public HomeInfo getHomeInfo() {
        Request request = getStandardRequest()
                .url(mHost + "GetHomeInfo")
                .get()
                .build();

        try (Response response = mHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();

                JSONObject jsonObject = new JSONObject(json);

                return HomeInfo.inflate(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Request.Builder getStandardRequest() {
        return new Request.Builder()
                .header("Accept", "application/json")
                .header("User-Agent", mUserAgent);
    }

    @WorkerThread
    public boolean setMode(@NonNull Mode mode) {
        HttpUrl url = HttpUrl.parse(mHost + "SetOperationMode").newBuilder()
                .addQueryParameter("Mode", mode.getValue())
                .build();

        Request request = getStandardRequest()
                .url(url)
                .get()
                .build();

        try (Response response = mHttpClient.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updateHost(@NonNull String host) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(new Cache(mContext.getCacheDir(), 1024 * 1024 * 2))
                .pingInterval(30, TimeUnit.SECONDS) // for the websocket
                .authenticator((route, response) -> {
                    String credential = Credentials.basic(mUsername, mPassword);
                    return response.request().newBuilder().header("Authorization",
                            credential).build();
                })
                .addInterceptor(new LoggingInterceptor());

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

    public enum Mode {
        HOME("HOME"), AWAY("NORMAL");

        private final String mValue;

        Mode(String value) {
            mValue = value;
        }

        public String getValue() {
            return mValue;
        }
    }
}
