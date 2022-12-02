package com.squid0928.project.fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;

public class FabOnFriendFragment extends Fragment implements View.OnClickListener {
    private MapsActivity mapsActivity;
    private GoogleMap map;
    private boolean status = false;

    public FabOnFriendFragment(MapsActivity maps, GoogleMap map) {
        this.mapsActivity = maps;
        this.map = map;
    }
    @Override
    public void onClick(View v){
        if (v instanceof FloatingActionButton) {
            FragmentManager manager = mapsActivity.getSupportFragmentManager();
            if (status) {
                Fragment created = manager.findFragmentByTag("FabFriendMenu");
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(created);
                transaction.commit();
                status = false;
            }
            else {
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.map, new FriendTabTrayFragment(mapsActivity, map), "FabFriendMenu");
                transaction.commit();
                status = true;
            }
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fab, container, false);
        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(this);
        return view;
    }

}
