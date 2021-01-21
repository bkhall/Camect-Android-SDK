package com.camect.android.example.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.camect.android.example.R;
import com.camect.android.example.util.AsyncTask;
import com.camect.android.example.viewmodels.CamectViewModel;
import com.camect.android.library.model.Camera;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ThreadPoolExecutor;

public class DisabledCameraListFragment extends Fragment implements OnItemClickListener {

    public static DisabledCameraListFragment newInstance() {
        return new DisabledCameraListFragment();
    }

    private CamectViewModel    mCamectViewModel;
    private ThreadPoolExecutor mExecutor;

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
        Camera camera = mCamectViewModel.getDisabledCameras().get(position);

        ModelInspectorDialogFragment.newInstance(camera.getName(), camera.toString())
                .show(getChildFragmentManager(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCamectViewModel = new ViewModelProvider(requireActivity()).get(CamectViewModel.class);

        mExecutor = AsyncTask.newCachedThreadPool();

        DisabledCameraListAdapter listAdapter = new DisabledCameraListAdapter(requireContext(),
                this);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(listAdapter);
    }

    private class DisabledCameraListAdapter extends RecyclerView.Adapter<DisabledCameraViewHolder> {

        private final Context             mContext;
        private final LayoutInflater      mInflater;
        private final OnItemClickListener mListener;

        public DisabledCameraListAdapter(Context context, OnItemClickListener listener) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mListener = listener;
        }

        @Override
        public int getItemCount() {
            return mCamectViewModel.getDisabledCameras().size();
        }

        @Override
        public void onBindViewHolder(@NonNull final DisabledCameraViewHolder holder, int position) {
            final Camera camera = mCamectViewModel.getDisabledCameras().get(position);

            holder.mCamera = camera;
            holder.mName.setText(camera.getName());
        }

        @NonNull
        @Override
        public DisabledCameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
            View view = mInflater.inflate(R.layout.list_item_disabled_camera, parent,
                    false);

            return new DisabledCameraViewHolder(view, mListener);
        }
    }

    private class DisabledCameraViewHolder extends RecyclerView.ViewHolder {
        public final  ImageView mDots;
        public final  TextView  mName;
        private final Context   mContext;
        public        Camera    mCamera;

        public DisabledCameraViewHolder(final View view, final OnItemClickListener listener) {
            super(view);

            mContext = view.getContext();

            mName = view.findViewById(R.id.camera_name);

            mDots = view.findViewById(R.id.context);
            mDots.setOnClickListener(v -> showPopupMenu());

            if (listener != null) {
                view.setOnClickListener(v -> listener.onItemClick(view, getAdapterPosition(),
                        getItemId()));
            }
        }

        private void showPopupMenu() {
            PopupMenu popup = new PopupMenu(mContext, mDots);
            popup.inflate(R.menu.camera_alerts_popup_menu);
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.rename) {
                    Snackbar.make(mDots, "Need an API reference for this!",
                            Snackbar.LENGTH_LONG).show();

                    return true;
                } else if (id == R.id.enable) {
                    Snackbar.make(mDots, "Need an API reference for this!",
                            Snackbar.LENGTH_LONG).show();

                    return true;
                }

                return false;
            });

            Menu menu = popup.getMenu();
            menu.findItem(R.id.disable).setVisible(false);
            menu.findItem(R.id.alerts).setVisible(false);

            popup.show();
        }
    }
}