package com.camect.android.example.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class ObjectAlertViewModel extends ViewModel {
    private String    mCameraId;
    private boolean[] mChecked;
    private String[]  mLabels;

    public boolean[] getChecked() {
        return mChecked;
    }

    public String[] getLabels() {
        return mLabels;
    }

    public void prepare(ArrayList<String> objects) {
        mChecked = new boolean[objects.size()];

        Arrays.fill(mChecked, false);

        mLabels = new String[objects.size()];

        objects.toArray(mLabels);

        mCameraId = null;
    }

    public void reset() {
        Arrays.fill(mChecked, false);
    }
}