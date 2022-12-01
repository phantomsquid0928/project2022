package com.squid0928.project.listeners;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
            if (mapsActivity.markers.containsKey("poi")) {
                mapsActivity.markers.get("poi").remove();
            }
            if (mapsActivity.markers.containsKey("click")) { //아무거도 입력 안할시 그냥 삭제로 바꾸기
                mapsActivity.markers.get("click").remove();
            }
            return;
        }

        Marker temp = MapMarkerManager.addMarker("name?", latLng);
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
                UserData target = mapsActivity.user_data.get("phantomsquid0928");

                MapMarkerManager.addMarker(latLng.toString(), latLng); //TODO 바꿔라
                target.getSavedInputMarkers().put(latLng.toString(), res);
                Locations loc = new Locations(null, latLng, null, 0, 0, res.getType());
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
