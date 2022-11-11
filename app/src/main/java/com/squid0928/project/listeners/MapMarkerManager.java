package com.squid0928.project.listeners;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.utils.Locations;
import com.squid0928.project.utils.UserData;

public class MapMarkerManager implements GoogleMap.OnMarkerClickListener {
    private MapsActivity mapsActivity;
    private GoogleMap map;
    public MapMarkerManager(MapsActivity mapsActivity, GoogleMap map) {
        this.mapsActivity = mapsActivity;
        this.map = map;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) { //마커에 입력된 데이터 불러오기
        return false;

    }
}
