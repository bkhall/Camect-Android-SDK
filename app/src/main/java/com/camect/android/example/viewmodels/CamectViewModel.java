package com.camect.android.example.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.camect.android.library.model.Camera;
import com.camect.android.library.model.HomeInfo;

import java.util.ArrayList;
import java.util.Collections;

public class CamectViewModel extends ViewModel {
    private final ArrayList<Camera> mAllCameras      = new ArrayList<>();
    private final ArrayList<Camera> mDisabledCameras = new ArrayList<>();
    private final ArrayList<Camera> mEnabledCameras  = new ArrayList<>();

    private HomeInfo mHomeInfo;

    @NonNull
    public ArrayList<Camera> getAllCameras() {
        return mAllCameras;
    }

    @NonNull
    public ArrayList<Camera> getDisabledCameras() {
        return mDisabledCameras;
    }

    @NonNull
    public ArrayList<Camera> getEnabledCameras() {
        return mEnabledCameras;
    }

    public HomeInfo getHomeInfo() {
        return mHomeInfo;
    }

    public void setCameras(@NonNull ArrayList<Camera> cameras) {
        if (cameras.size() > 1) {
            Collections.sort(cameras, (thisCamera, thatCamera) -> thisCamera.getName()
                    .compareTo(thatCamera.getName()));
        }

        mAllCameras.clear();
        mDisabledCameras.clear();
        mEnabledCameras.clear();

        mAllCameras.addAll(cameras);

        for (Camera camera : cameras) {
            if (camera.isDisabled()) {
                mDisabledCameras.add(camera);
            } else {
                mEnabledCameras.add(camera);
            }
        }
    }

    public void setHomeInfo(HomeInfo homeInfo) {
        mHomeInfo = homeInfo;
    }
}