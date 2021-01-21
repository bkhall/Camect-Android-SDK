package com.camect.android.sdk.example.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.camect.android.sdk.R;

public class ObjectAlertChooserDialogFragment extends DialogFragment {

    public static ObjectAlertChooserDialogFragment newInstance(String title, String text) {
        ObjectAlertChooserDialogFragment fragment = new ObjectAlertChooserDialogFragment();
        fragment.mText = text;
        fragment.mTitle = title;

        return fragment;
    }

    private AlertDialog mAlertDialog;
    private String      mText;
    private String      mTitle;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        TextView textView = view.findViewById(R.id.model_text);

        textView.setText(mText);

        mAlertDialog.setView(view);
    }
}
