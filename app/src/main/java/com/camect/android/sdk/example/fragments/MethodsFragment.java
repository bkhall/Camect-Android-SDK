package com.camect.android.sdk.example.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.camect.android.sdk.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MethodsFragment extends Fragment implements OnItemClickListener {

    private static final ArrayList<Method> sMethods = new ArrayList<>();

    public static MethodsFragment newInstance() {
        return new MethodsFragment();
    }

    static {
        sMethods.add(new Method("Get Home Info", "GetHomeInfo"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_methods_list, container, false);
    }

    @Override
    public void onItemClick(View view, int position, long id) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MethodsAdapter(getActivity(), this));
    }

    private static class Method {
        private final String mEndpoint;
        private final String mName;

        private Method(String name, String endpoint) {
            mName = name;
            mEndpoint = endpoint;
        }

        public String getEndpoint() {
            return mEndpoint;
        }

        public String getName() {
            return mName;
        }
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

    private static class MethodsAdapter extends RecyclerView.Adapter<MethodViewHolder> {

        private final LayoutInflater      mInflater;
        private final OnItemClickListener mListener;

        public MethodsAdapter(Context context, OnItemClickListener listener) {
            mInflater = LayoutInflater.from(context);
            mListener = listener;
        }

        @Override
        public int getItemCount() {
            return sMethods.size();
        }

        @Override
        public void onBindViewHolder(@NonNull MethodViewHolder holder, int position) {
            Method method = sMethods.get(position);

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