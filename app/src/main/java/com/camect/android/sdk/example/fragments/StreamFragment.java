package com.camect.android.sdk.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.camect.android.sdk.R;
import com.camect.android.sdk.example.viewmodels.CamectViewModel;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class StreamFragment extends Fragment {

    public static StreamFragment newInstance() {
        return new StreamFragment();
    }

    private SimpleExoPlayer mExoPlayer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onDestroyView() {
        mExoPlayer.release();

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CamectViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(CamectViewModel.class);

        mExoPlayer = new SimpleExoPlayer.Builder(requireContext()).build();

        StyledPlayerView playerView = view.findViewById(R.id.player_view);

        playerView.setPlayer(mExoPlayer);

        MediaItem mediaItem = MediaItem.fromUri(viewModel.getSelectedCamera().getStreamingUrl());

        mExoPlayer.setMediaItem(mediaItem);
        mExoPlayer.prepare();
        mExoPlayer.play();
    }
}