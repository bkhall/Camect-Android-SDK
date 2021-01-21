package com.camect.android.example.viewmodels;

import androidx.lifecycle.ViewModel;

public class LiveViewViewModel extends ViewModel {
    private String mStreamUrl;

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        mStreamUrl = streamUrl;
    }
}
