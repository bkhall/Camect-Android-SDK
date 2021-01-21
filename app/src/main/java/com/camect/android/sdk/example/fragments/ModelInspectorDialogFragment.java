package com.camect.android.sdk.example.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.camect.android.sdk.R;
import com.camect.android.sdk.example.viewmodels.ModelInspectorViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class ModelInspectorDialogFragment extends DialogFragment {

    public static ModelInspectorDialogFragment newInstance() {
        return new ModelInspectorDialogFragment();
    }

    private AlertDialog             mAlertDialog;
    private ModelInspectorViewModel mInspectorViewModel;

    private void ensureViewModel() {
        if (mInspectorViewModel == null) {
            mInspectorViewModel = new ViewModelProvider(requireActivity())
                    .get(ModelInspectorViewModel.class);
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ensureViewModel();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setTitle(mInspectorViewModel.getTitle());
        builder.setPositiveButton(android.R.string.ok, null);

        mAlertDialog = builder.create();

        return mAlertDialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_model_inspector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ensureViewModel();

        TextView textView = view.findViewById(R.id.model_text);

        textView.setText(mInspectorViewModel.getText());

        mAlertDialog.setView(view);
    }
}
