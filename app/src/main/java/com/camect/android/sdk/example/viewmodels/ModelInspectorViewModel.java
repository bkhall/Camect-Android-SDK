package com.camect.android.sdk.example.viewmodels;

import androidx.lifecycle.ViewModel;

public class ModelInspectorViewModel extends ViewModel {
    private String mText;
    private String mTitle;

    public String getText() {
        return mText;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}