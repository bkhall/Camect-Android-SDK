package com.camect.android.example.viewmodels;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.ViewModel;

public class ObjectAlertViewModel extends ViewModel {
    private String    mCameraId;
    private boolean[] mChecked;
    private String[]  mLabels;

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
        if (mChecked != null) {
            // is this new id different from before
            // if so, clear the boolean array
            if ((!TextUtils.isEmpty(cameraId) && TextUtils.isEmpty(mCameraId)) ||
                    (TextUtils.isEmpty(cameraId) && !TextUtils.isEmpty(mCameraId))) {
                Arrays.fill(mChecked, false);
            } else if (!TextUtils.isEmpty(cameraId) && !TextUtils.isEmpty(mCameraId)) {
                if (!cameraId.equals(mCameraId)) {
                    Arrays.fill(mChecked, false);
                }
            }
        }

        mCameraId = cameraId;
    }
}