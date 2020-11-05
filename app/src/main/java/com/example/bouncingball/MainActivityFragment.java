package com.example.bouncingball;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class MainActivityFragment extends Fragment {
    private BallView ballView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =
                inflater.inflate(R.layout.fragment_main, container, false);
        ballView = (BallView) view.findViewById(R.id.ballView);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
    @Override
    public void onPause() {
        super.onPause();
        ballView.stopGame();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ballView.releaseResources();
    }
}

