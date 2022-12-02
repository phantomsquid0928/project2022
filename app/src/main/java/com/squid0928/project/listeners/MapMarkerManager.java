package com.squid0928.project.listeners;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.fragments.InputTemplateFragment;
import com.squid0928.project.utils.InputData;
import com.squid0928.project.utils.Locations;
import com.squid0928.project.utils.UserData;

import java.util.Set;

public class MapMarkerManager implements GoogleMap.OnMarkerClickListener {
    private static MapsActivity mapsActivity;
    private static GoogleMap map;

    public String markerid = "";
    public static boolean markerClicked = false;
    public MapMarkerManager(MapsActivity mapsActivity, GoogleMap map) {
        this.mapsActivity = mapsActivity;
        this.map = map;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) { //마커에 입력된 데이터 불러오기
        markerClicked = true;
        FragmentManager manager = mapsActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        marker.showInfoWindow();

        Set<String> temp = mapsActivity.markers.keySet();
        String target = null;
        for (String key : temp) {
            if (mapsActivity.markers.get(key).equals(marker.getTitle())) {
                target = key;
            }
        }
        //TODO
        // change below code can access to serverside
        InputData inputData = null;
        if (target != null) {
            inputData = mapsActivity.user_data.get(mapsActivity.user).getMarker(target);
        }

        Log.i("ff", "inputdata exitst" + inputData.getType());

        Log.i("ff", "previous" + markerid + "\nnow " + marker.getId());
        if (markerid.equals(marker.getId())) {
            markerid = "";
            InputTemplateFragment fragment = new InputTemplateFragment();
            transaction.add(R.id.map, fragment, "fff");
            transaction.commit();
            LatLng latLng = marker.getPosition();

            manager.setFragmentResultListener("key", mapsActivity, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    InputData res = (InputData)result.getSerializable("inputData");
                    if (res == null) return;
                    UserData target = mapsActivity.user_data.get(mapsActivity.user); //TODO 바꿔라

                    MapMarkerManager.addMarker(latLng.toString(), latLng); //TODO 바꿔라
                    target.getSavedInputMarkers().put(latLng.toString(), res);
                    Locations loc = new Locations(null, latLng,null,res.getType());
                    //target.getSavedLocations().put();
                    if (!res.isEmpty()) { // TODO : no safe checker

                    }
                }
            });
        } else {
            markerid = marker.getId();
            Fragment createdLast = manager.findFragmentByTag("fff");

            if (createdLast != null) {
                transaction.remove(createdLast);
                transaction.commit();
                return true;
            }
            transaction.commit();
        }

       // transaction.add(R.id.map, new InputTemplateFragment(), "fff");  //TODO change this code that imputtemplate can show old inputdata
        //InputTemplateFragment.instantiate(InputData old) -> 예전에 입력한 정보 보여주기
        markerClicked = false;
        return false;
    }

    public static Marker addMarker(Place place) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place.getLatLng());
        markerOptions.title(place.getName());
        Marker marker = map.addMarker(markerOptions);
        return marker;
    }
    public static Marker addMarker(String name, LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(name);
        Marker marker = map.addMarker(markerOptions);
        return marker;
    }
    public static void removeMarker(String name) {
        Marker target = mapsActivity.markers.get(name);
        mapsActivity.markers.remove(name);
        target.remove();
    }
    public static void removeMarker(Place place) {
        Marker target = mapsActivity.markers.get(place.getName());
        mapsActivity.markers.remove(place.getName());
        target.remove();
    }
    public static boolean isMarkerClicked() {return markerClicked;}
}
