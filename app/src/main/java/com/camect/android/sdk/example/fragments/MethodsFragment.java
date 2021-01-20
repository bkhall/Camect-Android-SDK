package com.camect.android.sdk.example.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.util.AsyncTask;
import com.camect.android.sdk.example.viewmodels.CamectViewModel;
import com.camect.android.sdk.model.Camera;
import com.camect.android.sdk.model.HomeInfo;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class MethodsFragment extends Fragment implements OnItemClickListener {

    public static MethodsFragment newInstance() {
        return new MethodsFragment();
    }

    private final ArrayList<Method<?>> mMethods = new ArrayList<>();

    private ThreadPoolExecutor mExecutor;
    private CamectViewModel    mViewModel;

    private void buildList() {
        mMethods.clear();

        mMethods.add(new Method<HomeInfo>("Get Home Info") {
            @Override
            protected HomeInfo doInBackground(Void... voids) {
                HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                mViewModel.setHomeInfo(homeInfo);

                return mViewModel.getHomeInfo();
            }
        });
        mMethods.add(new Method<HomeInfo>("Set Mode to HOME") {
            @Override
            protected HomeInfo doInBackground(Void... voids) {
                if (CamectSDK.getInstance().setMode(CamectSDK.Mode.HOME)) {
                    HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                    mViewModel.setHomeInfo(homeInfo);
                }

                return mViewModel.getHomeInfo();
            }
        });
        mMethods.add(new Method<HomeInfo>("Set Mode to AWAY") {
            @Override
            protected HomeInfo doInBackground(Void... voids) {
                if (CamectSDK.getInstance().setMode(CamectSDK.Mode.AWAY)) {
                    HomeInfo homeInfo = CamectSDK.getInstance().getHomeInfo();

                    mViewModel.setHomeInfo(homeInfo);
                }

                return mViewModel.getHomeInfo();
            }
        });
        mMethods.add(new Method<ArrayList<Camera>>("List Cameras") {
            @Override
            protected ArrayList<Camera> doInBackground(Void... voids) {
                return CamectSDK.getInstance().getCameras();
            }

            @Override
            protected void onPostExecute(ArrayList<Camera> cameras) {
                mViewModel.setCameras(cameras);

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, CamerasFragment.newInstance())
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
        mViewModel = new ViewModelProvider(requireActivity()).get(CamectViewModel.class);

        mExecutor = AsyncTask.newSingleThreadExecutor();

        buildList();

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MethodsAdapter(getActivity(), this));
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
            ModelInspectorDialogFragment fragment = ModelInspectorDialogFragment
                    .newInstance(mName, result.toString());

            fragment.show(getChildFragmentManager(), null);

            // reset this task so it can run again
            reset();
        }
    }

    private class MethodsAdapter extends RecyclerView.Adapter<MethodViewHolder> {

        private final LayoutInflater      mInflater;
        private final OnItemClickListener mListener;

        public MethodsAdapter(Context context, OnItemClickListener listener) {
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