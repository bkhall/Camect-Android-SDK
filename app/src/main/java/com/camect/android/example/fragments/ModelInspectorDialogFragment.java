package com.camect.android.example.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.camect.android.example.R;
import com.camect.android.example.viewmodels.ModelInspectorViewModel;

public class ModelInspectorDialogFragment extends DialogFragment {

    public static ModelInspectorDialogFragment newInstance(@NonNull String title,
                                                           @Nullable String text) {
        ModelInspectorDialogFragment fragment = new ModelInspectorDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        if (!TextUtils.isEmpty(text)) {
            bundle.putString("text", text);
        }

        fragment.setArguments(bundle);

        return fragment;
    }

    private AlertDialog             mAlertDialog;
    private ModelInspectorViewModel mInspectorViewModel;

    private void ensureViewModel() {
        if (mInspectorViewModel == null) {
            mInspectorViewModel = new ViewModelProvider(requireActivity())
                    .get(ModelInspectorViewModel.class);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            mInspectorViewModel.setTitle(bundle.getString("title"));
            mInspectorViewModel.setText(bundle.getString("text", null));

            // throw it away so we rely on the viewmodel for the rest of the lifecycle
            setArguments(null);
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
