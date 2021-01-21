package com.camect.android.sdk.example.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.camect.android.sdk.model.Camera;
import com.camect.android.sdk.model.HomeInfo;

import java.util.ArrayList;

public class CamectViewModel extends ViewModel {
    private final ArrayList<Camera> mCameras = new ArrayList<>();

    private HomeInfo mHomeInfo;
    private Camera   mSelectedCamera;

    @NonNull
    public ArrayList<Camera> getCameras() {
        return mCameras;
    }

    public HomeInfo getHomeInfo() {
        return mHomeInfo;
    }

    public Camera getSelectedCamera() {
        return mSelectedCamera;
    }

    public void setCameras(@NonNull ArrayList<Camera> cameras) {
        mCameras.clear();

        for (Camera camera : cameras) {
            if (camera.isDisabled()) {
                continue;
            }

            mCameras.add(camera);
        }
    }

    public void setHomeInfo(HomeInfo homeInfo) {
        mHomeInfo = homeInfo;
    }

    public void setSelectedCamera(@NonNull Camera selectedCamera) {
        mSelectedCamera = selectedCamera;
    }
}