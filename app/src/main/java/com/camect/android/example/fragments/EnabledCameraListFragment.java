package com.camect.android.example.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.camect.android.example.R;
import com.camect.android.example.activities.LiveViewActivity;
import com.camect.android.example.util.AsyncTask;
import com.camect.android.example.viewmodels.CamectViewModel;
import com.camect.android.library.CamectSDK;
import com.camect.android.library.model.Camera;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ThreadPoolExecutor;

public class EnabledCameraListFragment extends Fragment implements OnItemClickListener,
        OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static EnabledCameraListFragment newInstance() {
        return new EnabledCameraListFragment();
    }

    private CamectViewModel          mCamectViewModel;
    private ThreadPoolExecutor       mExecutor;
    private EnabledCameraListAdapter mListAdapter;
    private SwipeRefreshLayout       mSwipe;

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
        Camera camera = mCamectViewModel.getEnabledCameras().get(position);

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(camera.getStreamingUrl()));
//        Intent chooser = Intent.createChooser(intent, "View Stream With");
//
//        startActivity(chooser);

        LiveViewActivity.launch(requireContext(), camera.getStreamingUrl());
    }

    @Override
    public boolean onItemLongClick(View view, int position, long id) {
        Camera camera = mCamectViewModel.getEnabledCameras().get(position);

        ModelInspectorDialogFragment.newInstance(camera.getName(), camera.toString())
                .show(getChildFragmentManager(), null);

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

        mExecutor = AsyncTask.newCachedThreadPool();

        mSwipe = view.findViewById(R.id.swipe);
        mSwipe.setEnabled(true);

        mListAdapter = new EnabledCameraListAdapter(requireContext(), this, this);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mListAdapter);
    }

    private class EnabledCameraListAdapter extends RecyclerView.Adapter<EnabledCameraViewHolder> {

        private final Context                 mContext;
        private final LayoutInflater          mInflater;
        private final OnItemClickListener     mListener;
        private final OnItemLongClickListener mLongListener;

        public EnabledCameraListAdapter(Context context, OnItemClickListener listener,
                                        OnItemLongClickListener longListener) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mListener = listener;
            mLongListener = longListener;
        }

        @Override
        public int getItemCount() {
            return mCamectViewModel.getEnabledCameras().size();
        }

        @Override
        public void onBindViewHolder(@NonNull final EnabledCameraViewHolder holder, int position) {
            final Camera camera = mCamectViewModel.getEnabledCameras().get(position);

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
        public EnabledCameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.list_item_enabled_camera, parent,
                    false);

            return new EnabledCameraViewHolder(view, mListener, mLongListener);
        }
    }

    private class EnabledCameraViewHolder extends RecyclerView.ViewHolder {
        public final  ImageView mDots;
        public final  TextView  mName;
        public final  ImageView mSnapshot;
        private final Context   mContext;
        public        Camera    mCamera;

        public EnabledCameraViewHolder(final View view, final OnItemClickListener listener,
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

        private void showPopupMenu() {
            PopupMenu popup = new PopupMenu(mContext, mDots);
            popup.inflate(R.menu.camera_alerts_popup_menu);
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.rename) {
                    Snackbar.make(mDots, "Need an API reference for this!",
                            Snackbar.LENGTH_LONG).show();

                    return true;
                } else if (id == R.id.disable) {
                    Snackbar.make(mDots, "Need an API reference for this!",
                            Snackbar.LENGTH_LONG).show();

                    return true;
                } else if (id == R.id.alerts) {
                    ObjectAlertChooserDialogFragment.newInstance(mCamera.getId())
                            .show(getChildFragmentManager(), null);

                    return true;
                }

                return false;
            });

            Menu menu = popup.getMenu();
            menu.findItem(R.id.enable).setVisible(false);

            popup.show();
        }
    }
}