package com.squid0928.project.listeners;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squid0928.project.fragments.InputTemplateFragment;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;

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
            if (mapsActivity.markers.containsKey("poi")) {
                mapsActivity.markers.get("poi").remove();
            }
            if (mapsActivity.markers.containsKey("click")) { //아무거도 입력 안할시 그냥 삭제로 바꾸기
                mapsActivity.markers.get("click").remove();
            }
            return;
        }
        transaction.add(R.id.map, new InputTemplateFragment(), "fff");
        transaction.commit();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("dd");//ui 에서 input
        Marker marker = map.addMarker(markerOptions);
        mapsActivity.markers.put("click", marker);
    }
}
