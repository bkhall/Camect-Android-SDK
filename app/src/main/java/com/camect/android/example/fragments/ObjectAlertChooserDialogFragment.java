package com.camect.android.example.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import com.camect.android.library.CamectSDK;
import com.camect.android.example.util.AsyncTask;
import com.camect.android.example.viewmodels.ObjectAlertViewModel;

import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class ObjectAlertChooserDialogFragment extends DialogFragment {

    public static ObjectAlertChooserDialogFragment newInstance() {
        return new ObjectAlertChooserDialogFragment();
    }

    private ThreadPoolExecutor mExecutor;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ObjectAlertViewModel alertViewModel =
                new ViewModelProvider(requireActivity()).get(ObjectAlertViewModel.class);

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
