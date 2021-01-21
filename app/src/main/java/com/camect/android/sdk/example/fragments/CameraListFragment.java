package com.camect.android.sdk.example.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.util.AsyncTask;
import com.camect.android.sdk.example.viewmodels.CamectViewModel;
import com.camect.android.sdk.example.viewmodels.ModelInspectorViewModel;
import com.camect.android.sdk.model.Camera;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CameraListFragment extends Fragment implements OnItemClickListener,
        OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static CameraListFragment newInstance() {
        return new CameraListFragment();
    }

    private CamectViewModel         mCamectViewModel;
    private ThreadPoolExecutor      mExecutor;
    private ModelInspectorViewModel mInspectorViewModel;
    private CameraListAdapter       mListAdapter;
    private SwipeRefreshLayout      mSwipe;

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
        Camera camera = mCamectViewModel.getCameras().get(position);

        mCamectViewModel.setSelectedCamera(camera);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, StreamFragment.newInstance())
                .addToBackStack("stream")
                .commit();
    }

    @Override
    public boolean onItemLongClick(View view, int position, long id) {
        Camera camera = mCamectViewModel.getCameras().get(position);

        mInspectorViewModel.setTitle(camera.getName());
        mInspectorViewModel.setText(camera.toString());

        ModelInspectorDialogFragment.newInstance().show(getChildFragmentManager(), null);

        return true;
    }

    @Override
    public void onRefresh() {
        mListAdapter.notifyDataSetChanged();

        mSwipe.postDelayed(() -> {
            mSwipe.setRefreshing(false);
        }, 10000);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mCamectViewModel = new ViewModelProvider(requireActivity()).get(CamectViewModel.class);
        mInspectorViewModel = new ViewModelProvider(requireActivity())
                .get(ModelInspectorViewModel.class);

        mExecutor = AsyncTask.newCachedThreadPool();

        mSwipe = view.findViewById(R.id.swipe);
        mSwipe.setEnabled(true);

        mListAdapter = new CameraListAdapter(getActivity(), this, this);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mListAdapter);
    }

    private static class CameraViewHolder extends RecyclerView.ViewHolder {
        public final  ImageView mDots;
        public final  TextView  mName;
        public final  ImageView mSnapshot;
        private final Context   mContext;
        public        Camera    mCamera;

        public CameraViewHolder(final View view, final OnItemClickListener listener,
                                OnItemLongClickListener longListener) {
            super(view);

            mContext = view.getContext();

            mName = view.findViewById(R.id.camera_name);
            mSnapshot = view.findViewById(R.id.snapshot);

            mDots = view.findViewById(R.id.context);
            mDots.setOnClickListener(v -> showPopupMenu());

            if (listener != null) {
                mSnapshot.setOnClickListener(v -> listener.onItemClick(view, getAdapterPosition(),
                        getItemId()));
            }

            if (longListener != null) {
                mSnapshot.setOnLongClickListener(v -> longListener.onItemLongClick(view,
                        getAdapterPosition(), getItemId()));
            }
        }

        private void showAlertsDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

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
                } else if (id == R.id.alerts) {
                    showAlertsDialog();

                    return true;
                }

                return false;
            });

            popup.show();
        }
    }

    private class CameraListAdapter extends RecyclerView.Adapter<CameraViewHolder> {

        private final Context                 mContext;
        private final LayoutInflater          mInflater;
        private final OnItemClickListener     mListener;
        private final OnItemLongClickListener mLongListener;

        public CameraListAdapter(Context context, OnItemClickListener listener,
                                 OnItemLongClickListener longListener) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mListener = listener;
            mLongListener = longListener;
        }

        @Override
        public int getItemCount() {
            return mCamectViewModel.getCameras().size();
        }

        @Override
        public void onBindViewHolder(@NonNull final CameraViewHolder holder, int position) {
            final Camera camera = mCamectViewModel.getCameras().get(position);

            holder.mCamera = camera;
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

                    Drawable oldDrawable = holder.mSnapshot.getDrawable();

                    if (oldDrawable == null) {
                        return rounded;
                    }

                    Drawable[] captures = new Drawable[2];
                    captures[0] = oldDrawable;
                    captures[1] = rounded;

                    return new TransitionDrawable(captures);
                }

                @Override
                protected void onPostExecute(Drawable drawable) {
                    holder.mSnapshot.setImageDrawable(drawable);

                    if (drawable instanceof TransitionDrawable) {
                        ((TransitionDrawable) drawable).startTransition(500);
                    }
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