package com.camect.android.example.viewmodels;

import androidx.lifecycle.ViewModel;

public class CameraListPagerViewModel extends ViewModel {
    private int mCurrentPage;

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }
}
