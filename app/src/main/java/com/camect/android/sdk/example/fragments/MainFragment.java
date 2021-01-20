package com.camect.android.sdk.example.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.util.AsyncTask;
import com.camect.android.sdk.model.HomeInfo;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ThreadPoolExecutor;

public class MainFragment extends Fragment implements View.OnClickListener, TextWatcher {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private final ThreadPoolExecutor mExecutor = AsyncTask.newSingleThreadExecutor();

    private EditText mCamectId;
    private Button   mConnect;
    private HomeInfo mHomeInfo;
    private EditText mPassword;

    @Override
    public void afterTextChanged(Editable s) {
        mConnect.setEnabled(isReady());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // not used; here to complete the interface
    }

    private void getHomeInfo() {
        new AsyncTask<Void, Void, HomeInfo>(mExecutor) {
            private Snackbar mSnackbar;

            @Override
            protected HomeInfo doInBackground(Void... voids) {
                if (mHomeInfo == null) {
                    publishProgress(null);
                    mHomeInfo = CamectSDK.getInstance().getHomeInfo();
                }

                return mHomeInfo;
            }

            @Override
            protected void onPostExecute(HomeInfo homeInfo) {
                String text;
                if (homeInfo == null) {
                    text = "FAILED";
                } else {
                    text = homeInfo.toString();
                }

                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }

                ModelInspectorDialogFragment fragment = ModelInspectorDialogFragment.newInstance(
                        "HomeInfo", text);
                fragment.show(getChildFragmentManager(), null);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, MethodsFragment.newInstance())
                        .addToBackStack("methods")
                        .commitNow();

                mConnect.setEnabled(true);
            }

            @Override
            protected void onProgressUpdate(Void... progress) {
                mSnackbar = Snackbar.make(mCamectId, "Getting Home Info...",
                        Snackbar.LENGTH_INDEFINITE);
                mSnackbar.setAction("Cancel", v -> {
                    cancel(true);
                    mSnackbar.dismiss();
                });
                mSnackbar.show();
            }
        }.executeNow();
    }

    private boolean isReady() {
        String camectId = mCamectId.getText().toString();
        String password = mPassword.getText().toString();

        return !TextUtils.isEmpty(camectId.trim()) && !TextUtils.isEmpty(password.trim());
    }

    @Override
    public void onClick(View v) {
        mConnect.setEnabled(false);

        if (!CamectSDK.isInitialized()) {
            String camectId = mCamectId.getText().toString();
            String password = mPassword.getText().toString();

            CamectSDK.init(getContext(), camectId.trim(), password.trim());
        }

        getHomeInfo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroyView() {
        mExecutor.shutdownNow();

        super.onDestroyView();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // not used; here to complete the interface
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCamectId = view.findViewById(R.id.camect_id);
        mCamectId.addTextChangedListener(this);

        mPassword = view.findViewById(R.id.password);
        mPassword.addTextChangedListener(this);

        mConnect = view.findViewById(R.id.connect);
        mConnect.setOnClickListener(this);
    }
}