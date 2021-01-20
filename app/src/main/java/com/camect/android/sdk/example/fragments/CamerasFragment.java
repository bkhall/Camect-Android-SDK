package com.camect.android.sdk.example.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.util.AsyncTask;
import com.camect.android.sdk.example.viewmodels.CamectViewModel;
import com.camect.android.sdk.model.Camera;

import java.util.concurrent.ThreadPoolExecutor;

public class CamerasFragment extends Fragment implements OnItemClickListener,
        OnItemLongClickListener {

    public static CamerasFragment newInstance() {
        return new CamerasFragment();
    }

    private ThreadPoolExecutor mExecutor;
    private CamectViewModel    mViewModel;

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
        Camera camera = mViewModel.getCameras().get(position);

        mViewModel.setSelectedCamera(camera);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, StreamFragment.newInstance())
                .addToBackStack("stream")
                .commit();
    }

    @Override
    public boolean onItemLongClick(View view, int position, long id) {
        Camera camera = mViewModel.getCameras().get(position);

        ModelInspectorDialogFragment fragment =
                ModelInspectorDialogFragment.newInstance(camera.getName(), camera.toString());
        fragment.show(getChildFragmentManager(), null);

        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(CamectViewModel.class);

        mExecutor = AsyncTask.newCachedThreadPool();

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CamerasAdapter(getActivity(), this, this));
    }

    private static class CameraViewHolder extends RecyclerView.ViewHolder {
        public final TextView  mName;
        public final ImageView mSnapshot;

        public CameraViewHolder(final View view, final OnItemClickListener listener,
                                OnItemLongClickListener longListener) {
            super(view);

            mName = view.findViewById(R.id.camera_name);
            mSnapshot = view.findViewById(R.id.snapshot);

            if (listener != null) {
                view.setOnClickListener(v -> listener.onItemClick(view, getAdapterPosition(),
                        getItemId()));
            }

            if (longListener != null) {
                view.setOnLongClickListener(v -> longListener.onItemLongClick(view,
                        getAdapterPosition(), getItemId()));
            }
        }
    }

    private class CamerasAdapter extends RecyclerView.Adapter<CameraViewHolder> {

        private final Context                 mContext;
        private final LayoutInflater          mInflater;
        private final OnItemClickListener     mListener;
        private final OnItemLongClickListener mLongListener;

        public CamerasAdapter(Context context, OnItemClickListener listener,
                              OnItemLongClickListener longListener) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mListener = listener;
            mLongListener = longListener;
        }

        @Override
        public int getItemCount() {
            return mViewModel.getCameras().size();
        }

        @Override
        public void onBindViewHolder(@NonNull final CameraViewHolder holder, int position) {
            final Camera camera = mViewModel.getCameras().get(position);

            holder.mName.setText(camera.getName());

            new AsyncTask<Void, Void, Drawable>(mExecutor) {
                @Override
                protected Drawable doInBackground(Void... voids) {
                    Bitmap bitmap = CamectSDK.getInstance().getCameraSnapshot(camera.getId(),
                            camera.getWidth() / 2, camera.getHeight() / 2);

                    if (bitmap == null) {
                        return null;
                    }

                    RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create
                            (mContext.getResources(), bitmap);
                    rounded.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight()) / 16f);

                    return rounded;
                }

                @Override
                protected void onPostExecute(Drawable drawable) {
                    holder.mSnapshot.setImageDrawable(drawable);
                }
            }.executeNow();
        }

        @NonNull
        @Override
        public CameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.list_item_camera, parent,
                    false);

            return new CameraViewHolder(view, mListener, mLongListener);
        }
    }
}