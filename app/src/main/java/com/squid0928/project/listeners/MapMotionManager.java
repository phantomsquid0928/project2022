package com.squid0928.project.listeners;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.fragments.PopupFragment;

public class MapMotionManager implements GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener{
    private MapsActivity mapsActivity;
    private GoogleMap map;

    public MapMotionManager(MapsActivity mapsActivity, GoogleMap map) {
        this.mapsActivity = mapsActivity;
        this.map = map;
    }

    @Override
    public void onCameraMoveStarted(int i) {
        switch(i) {
            case REASON_DEVELOPER_ANIMATION:
                break;
            case REASON_API_ANIMATION:
            case REASON_GESTURE:
            {
                FragmentManager manager = mapsActivity.getSupportFragmentManager();
                Fragment createdsearch = manager.findFragmentByTag("topsearch");
                FragmentTransaction transaction = manager.beginTransaction();
                if (createdsearch != null) {
                    transaction.remove(createdsearch);
                    transaction.commit();
                }
                break;
            }
        }
    }

    @Override
    public void onCameraIdle() {
        FragmentManager manager = mapsActivity.getSupportFragmentManager();
        Fragment createdsearch = manager.findFragmentByTag("topsearch");
        if (createdsearch == null) {
            mapsActivity.createTopSearch();
        }
    }
}
