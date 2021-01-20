package com.camect.android.sdk.example.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.util.AsyncTask;
import com.camect.android.sdk.model.Camera;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class CamerasFragment extends Fragment implements OnItemClickListener {

    public static CamerasFragment newInstance(@NonNull ArrayList<Camera> cameras) {
        CamerasFragment fragment = new CamerasFragment();
        for (Camera camera : cameras) {
            if (camera.isDisabled()) {
                continue;
            }

            fragment.mCameras.add(camera);
        }

        return fragment;
    }

    private final ThreadPoolExecutor mExecutor = AsyncTask.newCachedThreadPool();

    private ArrayList<Camera> mCameras = new ArrayList<>();

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
        Camera camera = mCameras.get(position);

        ModelInspectorDialogFragment fragment =
                ModelInspectorDialogFragment.newInstance(camera.getName(), camera.toString());
        fragment.show(getChildFragmentManager(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CamerasAdapter(getActivity(), this));
    }

    private static class CameraViewHolder extends RecyclerView.ViewHolder {
        public final TextView  mName;
        public final ImageView mSnapshot;

        public CameraViewHolder(View view, final OnItemClickListener listener) {
            super(view);

            mName = view.findViewById(R.id.camera_name);
            mSnapshot = view.findViewById(R.id.snapshot);

            if (listener != null) {
                view.setOnClickListener(v -> listener.onItemClick(view, getAdapterPosition(),
                        getItemId()));
            }
        }
    }

    private class CamerasAdapter extends RecyclerView.Adapter<CameraViewHolder> {

        private final LayoutInflater      mInflater;
        private final OnItemClickListener mListener;

        public CamerasAdapter(Context context, OnItemClickListener listener) {
            mInflater = LayoutInflater.from(context);
            mListener = listener;
        }

        @Override
        public int getItemCount() {
            return mCameras.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final CameraViewHolder holder, int position) {
            final Camera camera = mCameras.get(position);

            holder.mName.setText(camera.getName());

            new AsyncTask<Void, Void, Bitmap>(mExecutor) {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    return CamectSDK.getInstance().getCameraSnapshot(camera.getId(),
                            camera.getWidth() / 2, camera.getHeight() / 2);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.mSnapshot.setImageBitmap(bitmap);
                }
            }.executeNow();
        }

        @NonNull
        @Override
        public CameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.list_item_camera, parent,
                    false);

            return new CameraViewHolder(view, mListener);
        }
    }
}