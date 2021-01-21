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

import com.camect.android.example.R;

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

    private AlertDialog mAlertDialog;
    private String      mText;
    private String      mTitle;

    private void ensureData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString("title");
            mText = bundle.getString("text", null);
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ensureData();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setTitle(mTitle);
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
        ensureData();

        TextView textView = view.findViewById(R.id.model_text);

        textView.setText(mText);

        mAlertDialog.setView(view);
    }
}
