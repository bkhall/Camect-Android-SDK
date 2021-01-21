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
import androidx.lifecycle.ViewModelProvider;

import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.util.AsyncTask;
import com.camect.android.sdk.example.viewmodels.CamectViewModel;
import com.camect.android.sdk.model.HomeInfo;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ThreadPoolExecutor;

public class MainFragment extends Fragment implements View.OnClickListener, TextWatcher {

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private EditText           mCamectId;
    private CamectViewModel    mCamectViewModel;
    private Button             mConnect;
    private ThreadPoolExecutor mExecutor;
    private EditText           mPassword;

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
                if (mCamectViewModel.getHomeInfo() == null) {
                    publishProgress(null);

                    HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                    mCamectViewModel.setHomeInfo(homeInfo);
                }

                return mCamectViewModel.getHomeInfo();
            }

            @Override
            protected void onPostExecute(HomeInfo homeInfo) {
                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }

                if (homeInfo == null) {
                    ModelInspectorDialogFragment fragment = ModelInspectorDialogFragment
                            .newInstance("Connection Failed", null);

                    fragment.show(getChildFragmentManager(), null);
                } else {
                    CamectSDK.getInstance().updateHost(homeInfo.getId());

                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, MethodListFragment.newInstance())
                            .commit();
                }
            }

            @Override
            protected void onProgressUpdate(Void... progress) {
                mSnackbar = Snackbar.make(mCamectId, "Connecting...",
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

        String camectId = mCamectId.getText().toString();
        String password = mPassword.getText().toString();

        CamectSDK.init(getContext(), camectId.trim(), password.trim());

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
        mCamectViewModel = new ViewModelProvider(requireActivity()).get(CamectViewModel.class);

        mExecutor = AsyncTask.newSingleThreadExecutor();

        mCamectId = view.findViewById(R.id.camect_id);
        mCamectId.addTextChangedListener(this);

        mPassword = view.findViewById(R.id.password);
        mPassword.addTextChangedListener(this);

        mConnect = view.findViewById(R.id.connect);
        mConnect.setOnClickListener(this);
    }
}