package com.camect.android.sdk.example.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.util.AsyncTask;
import com.camect.android.sdk.example.viewmodels.CamectViewModel;
import com.camect.android.sdk.example.viewmodels.ModelInspectorViewModel;
import com.camect.android.sdk.example.viewmodels.ObjectAlertViewModel;
import com.camect.android.sdk.model.Camera;
import com.camect.android.sdk.model.HomeInfo;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MethodListFragment extends Fragment implements OnItemClickListener {

    public static MethodListFragment newInstance() {
        return new MethodListFragment();
    }

    private final ArrayList<Method<?>> mMethods = new ArrayList<>();

    private ObjectAlertViewModel    mAlertViewModel;
    private Button                  mButton;
    private CamectViewModel         mCamectViewModel;
    private ThreadPoolExecutor      mExecutor;
    private ModelInspectorViewModel mInspectorViewModel;

    private void buildMethodList() {
        mMethods.clear();

        mMethods.add(new Method<String>("Get 24 Hr Access Token") {
            @Override
            protected String doInBackground(Void... voids) {
                String accessToken = CamectSDK.getInstance().getAccessToken(24 * 3600);

                if (TextUtils.isEmpty(accessToken)) {
                    accessToken = "FAILED";
                }

                return accessToken;
            }
        });
        mMethods.add(new Method<HomeInfo>("Get Home Info") {
            @Override
            protected HomeInfo doInBackground(Void... voids) {
                if (mCamectViewModel.getHomeInfo() == null) {
                    HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                    mCamectViewModel.setHomeInfo(homeInfo);
                }

                return mCamectViewModel.getHomeInfo();
            }
        });
        mMethods.add(new Method<Void>("Set Home Name") {
            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                showNamePrompt();

                // reset this task so it can run again
                reset();
            }
        });
        mMethods.add(new Method<HomeInfo>("Set Mode to HOME") {
            @Override
            protected HomeInfo doInBackground(Void... voids) {
                if (CamectSDK.getInstance().setMode(CamectSDK.Mode.HOME)) {
                    HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                    mCamectViewModel.setHomeInfo(homeInfo);
                }

                return mCamectViewModel.getHomeInfo();
            }
        });
        mMethods.add(new Method<HomeInfo>("Set Mode to AWAY") {
            @Override
            protected HomeInfo doInBackground(Void... voids) {
                if (CamectSDK.getInstance().setMode(CamectSDK.Mode.AWAY)) {
                    HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                    mCamectViewModel.setHomeInfo(homeInfo);
                }

                return mCamectViewModel.getHomeInfo();
            }
        });
        mMethods.add(new Method<Void>("Set Alerts For Home") {
            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mAlertViewModel.setCameraId(null);

                ObjectAlertChooserDialogFragment.newInstance()
                        .show(getChildFragmentManager(), null);
            }
        });
        mMethods.add(new Method<ArrayList<Camera>>("List Cameras") {
            @Override
            protected ArrayList<Camera> doInBackground(Void... voids) {
                return CamectSDK.getInstance().getCameras();
            }

            @Override
            protected void onPostExecute(ArrayList<Camera> cameras) {
                mCamectViewModel.setCameras(cameras);

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, CameraListFragment.newInstance())
                        .addToBackStack("cameras")
                        .commit();

                // reset this task so it can run again
                reset();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onDestroyView() {
        mExecutor.shutdownNow();

        super.onDestroyView();
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        mMethods.get(position).executeNow();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCamectViewModel = new ViewModelProvider(requireActivity()).get(CamectViewModel.class);
        mInspectorViewModel = new ViewModelProvider(requireActivity())
                .get(ModelInspectorViewModel.class);
        mAlertViewModel = new ViewModelProvider(requireActivity())
                .get(ObjectAlertViewModel.class);

        mExecutor = AsyncTask.newSingleThreadExecutor();

        buildMethodList();

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MethodListAdapter(getActivity(), this));
    }

    private void showNamePrompt() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_name_input, null,
                false);

        final EditText editText = view.findViewById(R.id.text_input_box);
        editText.setText(mCamectViewModel.getHomeInfo().getName());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mButton.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not used; here to complete the interface
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // not used; here to complete the interface
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false)
                .setTitle("Set Home Name")
                .setView(view)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> new AsyncTask<Void,
                        Void, HomeInfo>(mExecutor) {

                    @Override
                    protected HomeInfo doInBackground(Void... voids) {
                        String name = editText.getText().toString().trim();
                        if (CamectSDK.getInstance().setHomeName(name)) {
                            HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                            mCamectViewModel.setHomeInfo(homeInfo);
                        }

                        return mCamectViewModel.getHomeInfo();
                    }

                    @Override
                    protected void onPostExecute(HomeInfo homeInfo) {
                        mInspectorViewModel.setTitle("Set Home Info");
                        mInspectorViewModel.setText(homeInfo.toString());

                        ModelInspectorDialogFragment.newInstance()
                                .show(getChildFragmentManager(), null);
                    }
                }.executeNow());

        AlertDialog alert = builder.create();

        alert.setOnShowListener(dialog -> {
            String name = editText.getText().toString().trim();

            mButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            mButton.setEnabled(!TextUtils.isEmpty(name));
        });

        alert.show();
    }

    private static class MethodViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;

        public MethodViewHolder(View view, final OnItemClickListener listener) {
            super(view);

            mName = view.findViewById(R.id.method_name);

            if (listener != null) {
                view.setOnClickListener(v -> listener.onItemClick(view, getAdapterPosition(),
                        getItemId()));
            }
        }
    }

    public abstract class Method<T> extends AsyncTask<Void, Void, T> {
        private final String mName;

        private Method(String name) {
            super(mExecutor);

            mName = name;
        }

        public String getName() {
            return mName;
        }

        @Override
        protected void onPostExecute(T result) {
            mInspectorViewModel.setTitle(mName);
            mInspectorViewModel.setText(result.toString());

            ModelInspectorDialogFragment.newInstance()
                    .show(getChildFragmentManager(), null);

            // reset this task so it can run again
            reset();
        }
    }

    private class MethodListAdapter extends RecyclerView.Adapter<MethodViewHolder> {

        private final LayoutInflater      mInflater;
        private final OnItemClickListener mListener;

        public MethodListAdapter(Context context, OnItemClickListener listener) {
            mInflater = LayoutInflater.from(context);
            mListener = listener;
        }

        @Override
        public int getItemCount() {
            return mMethods.size();
        }

        @Override
        public void onBindViewHolder(@NonNull MethodViewHolder holder, int position) {
            Method<?> method = mMethods.get(position);

            holder.mName.setText(method.getName());
        }

        @NonNull
        @Override
        public MethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.list_item_method, parent,
                    false);

            return new MethodViewHolder(view, mListener);
        }
    }
}