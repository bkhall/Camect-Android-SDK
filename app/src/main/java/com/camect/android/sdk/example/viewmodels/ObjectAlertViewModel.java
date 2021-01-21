package com.camect.android.sdk.example.viewmodels;

import java.util.ArrayList;

import androidx.lifecycle.ViewModel;

public class ObjectAlertViewModel extends ViewModel {
    private String    mCameraId;
    private boolean[] mChecked;
    private String[] mLabels;

    public String getCameraId() {
        return mCameraId;
    }

    public boolean[] getChecked() {
        return mChecked;
    }

    public String[] getLabels() {
        return mLabels;
    }

    public void prepare(ArrayList<String> objects) {
        mChecked = new boolean[objects.size()];

        mLabels = new String[objects.size()];

        objects.toArray(mLabels);

        mCameraId = null;
    }

    public void setCameraId(String cameraId) {
        mCameraId = cameraId;
    }
}