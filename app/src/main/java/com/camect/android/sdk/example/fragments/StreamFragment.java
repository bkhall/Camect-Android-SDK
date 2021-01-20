package com.camect.android.sdk.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.camect.android.sdk.R;
import com.camect.android.sdk.example.viewmodels.CamectViewModel;

public class StreamFragment extends Fragment {

    public static StreamFragment newInstance() {
        return new StreamFragment();
    }

    private CamectViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(CamectViewModel.class);
    }
}