package com.muatik.flj.flj.UI.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by muatik on 29.07.2016.
 */
public class MyFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("FLJ", this.getClass().getSimpleName() + " onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("FLJ", this.getClass().getSimpleName() + " onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.i("FLJ", this.getClass().getSimpleName() + " onViewCreated " + System.identityHashCode(this));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onDestroyView " + System.identityHashCode(this));

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onStart " + System.identityHashCode(this));
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onStop " + System.identityHashCode(this));
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("FLJ", this.getClass().getSimpleName() + " onPause");
        super.onPause();
    }
}