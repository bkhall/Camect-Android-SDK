package com.camect.android.library.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class LoggingInterceptor implements Interceptor {
    private static final String TAG = LoggingInterceptor.class.getSimpleName();

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Timber.i("Sending request %s \n%s", request.url(), request.headers());

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Timber.i("Received response for %s in %.1fms\n%s", response.request().url(),
                (t2 - t1) / 1e6d, response.headers());
        Timber.d(String.valueOf(response.code()));
        Timber.d(response.message());

        return response;
    }
}