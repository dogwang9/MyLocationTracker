package com.example.mylocationtracker.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mylocationtracker.R;

public class SettingsTabFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText(R.string.settings);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(24);
        return textView;
    }
}