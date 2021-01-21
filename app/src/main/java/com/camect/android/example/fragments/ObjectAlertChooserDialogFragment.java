package com.camect.android.example.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.camect.android.example.util.AsyncTask;
import com.camect.android.example.viewmodels.ObjectAlertViewModel;
import com.camect.android.library.CamectSDK;

import java.util.concurrent.ThreadPoolExecutor;

public class ObjectAlertChooserDialogFragment extends DialogFragment {

    public static ObjectAlertChooserDialogFragment newInstance(@Nullable String cameraId) {
        ObjectAlertChooserDialogFragment fragment = new ObjectAlertChooserDialogFragment();
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(cameraId)) {
            bundle.putString("cameraId", cameraId);
        }

        fragment.setArguments(bundle);

        return fragment;
    }

    private ThreadPoolExecutor mExecutor;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ObjectAlertViewModel alertViewModel = new ViewModelProvider(requireActivity())
                .get(ObjectAlertViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            alertViewModel.setCameraId(bundle.getString("cameraId", null));

            // throw it away so we rely on the viewmodel for the rest of the lifecycle
            setArguments(null);
        }

        mExecutor = AsyncTask.newCachedThreadPool();

        final String[] labels = alertViewModel.getLabels();
        final boolean[] checked = alertViewModel.getChecked();
        final String cameraId = alertViewModel.getCameraId();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false)
                .setTitle(TextUtils.isEmpty(cameraId) ?
                        "Select Alerts For Home" : "Select Alerts For Camera")
                .setMultiChoiceItems(labels, checked,
                        (dialog, which, isChecked) -> {
                            checked[which] = isChecked;

                            new AsyncTask<Void, Void, Boolean>(mExecutor) {
                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    if (TextUtils.isEmpty(cameraId)) {
                                        return CamectSDK.getInstance()
                                                .setAlertForHome(labels[which], isChecked);
                                    } else {
                                        return CamectSDK.getInstance()
                                                .setAlertForCameras(labels[which], isChecked,
                                                        cameraId);
                                    }
                                }
                            }.executeNow();
                        })
                .setPositiveButton("Done", null);

        return builder.create();
    }

    @Override
    public void onDestroyView() {
        mExecutor.shutdownNow();

        super.onDestroyView();
    }
}
