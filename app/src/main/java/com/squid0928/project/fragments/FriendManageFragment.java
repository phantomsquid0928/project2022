package com.squid0928.project.fragments;

import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;

public class FriendManageFragment extends Fragment implements View.OnClickListener{
    private MapsActivity mapsActivity;
    private GoogleMap map;

    public FriendManageFragment(MapsActivity maps, GoogleMap map) {
        this.mapsActivity = maps;
        this.map = map;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.bottom_slide_up));
        setExitTransition(inflater.inflateTransition(R.transition.bottom_slide_down));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marker_tray, container, false);

        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
