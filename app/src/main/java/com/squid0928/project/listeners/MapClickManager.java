package com.squid0928.project.listeners;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squid0928.project.fragments.InputTemplateFragment;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.utils.InputData;
import com.squid0928.project.utils.Locations;
import com.squid0928.project.utils.UserData;

public class MapClickManager implements GoogleMap.OnMapClickListener {
    private MapsActivity mapsActivity;
    private GoogleMap map;

    public MapClickManager(MapsActivity mapsActivity, GoogleMap map) {
        this.mapsActivity = mapsActivity;
        this.map = map;
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) { // ui 표시 후 마커 추가
        FragmentManager manager = mapsActivity.getSupportFragmentManager();
        Fragment createdLast = manager.findFragmentByTag("fff");
        FragmentTransaction transaction = manager.beginTransaction();
        if (createdLast != null) {
            transaction.remove(createdLast);
            transaction.commit();
            if (MapMarkerManager.isMarkerClicked()) {
                return;
            }
            String[] forbiden = {"search", "name?", "poi - "};
            for (String key : mapsActivity.markers.keySet()) {
                if (key.equals(forbiden[0])) {
                    mapsActivity.markers.get(key).remove();
                }
                if (key.equals(forbiden[1])) {
                    mapsActivity.markers.get(key).remove();
                }
                if (key.contains(forbiden[2]) && key.substring(0, 6).equals(forbiden[2])) {
                    mapsActivity.markers.get(key).remove();
                }
            }
            return;
        }

        Marker temp = MapMarkerManager.addMarker("name?", latLng, 1);
        mapsActivity.markers.put("name?", temp);
        InputTemplateFragment fragment = new InputTemplateFragment();
        transaction.add(R.id.map, fragment, "fff");
        transaction.commit();
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        manager.setFragmentResultListener("key", mapsActivity, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                InputData res = (InputData)result.getSerializable("inputData");
                temp.remove();
                if (res == null) return;
                UserData target = mapsActivity.user_data.get(mapsActivity.user); //TODO 바꿔라 개인정보

                MapMarkerManager.addMarker(res.getScheduleName(), latLng, res.getType()); //TODO 바꿔라
                target.getSavedInputMarkers().put(res.getScheduleName(), res);
                Locations loc = new Locations(null, latLng, null, res.getType());  //TODO 바꿔라
                target.getSavedLocations().put(res.getScheduleName(), loc);
                mapsActivity.saveToDB();
                //target.getSavedLocations().put();
                if (!res.isEmpty()) { // TODO : no safe checker

                }
            }
        });

        /*MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("dd");//ui 에서 input
        Marker marker = map.addMarker(markerOptions);
        mapsActivity.markers.put("click", marker);*/
    }
}
