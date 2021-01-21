package com.camect.android.example.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.camect.android.library.model.Camera;
import com.camect.android.library.model.HomeInfo;

import java.util.ArrayList;
import java.util.Collections;

public class CamectViewModel extends ViewModel {
    private final ArrayList<Camera> mCameras = new ArrayList<>();

    private HomeInfo mHomeInfo;

    @NonNull
    public ArrayList<Camera> getCameras() {
        return mCameras;
    }

    public HomeInfo getHomeInfo() {
        return mHomeInfo;
    }

    public void setCameras(@NonNull ArrayList<Camera> cameras) {
        mCameras.clear();

        for (Camera camera : cameras) {
            if (camera.isDisabled()) {
                continue;
            }

            mCameras.add(camera);
        }

        if (mCameras.size() > 1) {
            Collections.sort(mCameras, (thisCamera, thatCamera) -> thisCamera.getName()
                    .compareTo(thatCamera.getName()));
        }
    }

    public void setHomeInfo(HomeInfo homeInfo) {
        mHomeInfo = homeInfo;
    }
}