package com.squid0928.project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squid0928.project.databinding.ActivityMapsBinding;
import com.squid0928.project.fragments.InputTemplateFragment;
import com.squid0928.project.fragments.SettingsFragment;
import com.squid0928.project.fragments.TimetableFragment;
import com.squid0928.project.fragments.TopSearchFragment;
import com.squid0928.project.listeners.MapClickManager;
import com.squid0928.project.listeners.MapMarkerManager;
import com.squid0928.project.listeners.MapMotionManager;
import com.squid0928.project.utils.UserData;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnPoiClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1010;

    public static HashMap<String, UserData> user_data = new HashMap<>(); //서버에서 받아야함, 위험한 정보

    public BottomNavigationView bottomNav;
    public HashMap<String, Marker> markers = new HashMap<>();
    private LinearLayout ly;
    private ActivityMapsBinding binding;

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient locationProviderClient;

    private PlacesClient placesClient;
    //final String apiKey = BuildConfig.MAPS_API_KEY;

    SettingsFragment settingsFragment;
    TimetableFragment timetableFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());*/
        setContentView(R.layout.activity_maps);

        settingsFragment = new SettingsFragment();
        timetableFragment = new TimetableFragment();

        bottomNav = findViewById(R.id.bottomView);
        ly = findViewById(R.id.home_layout);
        //Places.initialize(getApplicationContext(), apiKey);
        //placesClient = Places.createClient(this);
        bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
            switch (id) {
                case R.id.tab_map:
                    break;
                case R.id.tab_friend:
                    break;
                case R.id.tab_timetable:
                    getSupportFragmentManager().beginTransaction().replace(R.id.map, timetableFragment).commit();
                    break;
                case R.id.tab_settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.map, settingsFragment).commit();
                    break;
            }
            return true;
        });
        bottomNav.setSelectedItemId(R.id.tab_map);

        getLocationPermission(); //permission 후 자동 맵 호출
    }
    public void createTopSearch() {
        FragmentManager manager = getSupportFragmentManager();
        //Fragment createdLast = manager.findFragmentByTag("topsearch");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map, new TopSearchFragment(this), "topsearch");
        transaction.commit();
    }
    private void initMap() {
        createTopSearch();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            getDeviceLocation();
        }
        mMap.setOnMarkerClickListener(new MapMarkerManager(this, mMap));
        mMap.setOnMapClickListener(new MapClickManager(this, mMap));
        //mMap.setOnMapLongClickListener(new MapLongClickManager(this, mMap));
        mMap.setOnPoiClickListener(this);
        mMap.setOnCameraMoveStartedListener(new MapMotionManager(this, mMap));
        mMap.setOnCameraIdleListener(new MapMotionManager(this, mMap));

    }
    private Location getDeviceLocation() {
        Log.d("ff", "getting location of user");
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //final Location[] ret = {};

        try {
            if (mLocationPermissionsGranted) {
                Log.i("ff", "trying...");
                Task<Location> locationResult = locationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location currentLocation = task.getResult();
                        if (currentLocation == null) {
                            Log.i("ff", "DANGER");
                            return;
                        }
                        Log.i("ff", "" + currentLocation.getLatitude());
                        LatLng latLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLocation).title("ur location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLocation));
                        markers.put("myloc", marker);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.i("ff", "no security");
        }
        return null;
    }

    private void getLocationPermission() {
        String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionsGranted = false;
                        return;
                    }
                }
                mLocationPermissionsGranted = true;
                Log.i("tt", "permission granted");
                initMap();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        FragmentManager manager = getSupportFragmentManager();
        Fragment createdLast = manager.findFragmentByTag("fff");
        FragmentTransaction transaction = manager.beginTransaction();
        if (createdLast != null) {
            transaction.remove(createdLast);
            transaction.commit();
            return;
        }
        transaction.add(R.id.map, new InputTemplateFragment(), "fff");
        transaction.commit();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("dd");//ui 에서 input
        Marker marker = mMap.addMarker(markerOptions);
        markers.put("myloc", marker);
    }
    @Override
    public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {

        LatLng latlng = pointOfInterest.latLng;
        MarkerOptions markerOptions = new MarkerOptions().position(latlng).title(pointOfInterest.name);
        Marker marker = mMap.addMarker(markerOptions);
        markers.put("poi", marker);
    }
}