package com.squid0928.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.squid0928.project.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1010;
    private double locx, locy;

    public BottomNavigationView bottomNav;
    private LinearLayout ly;
    private ActivityMapsBinding binding;

    private GoogleMap mMap;
    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient locationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());*/
        setContentView(R.layout.activity_maps);
        bottomNav = findViewById(R.id.bottomView);
        ly = findViewById(R.id.home_layout);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.tab_map:
                        break;
                    case R.id.tab_friend:
                        break;
                    case R.id.tab_settings:
                        break;
                }
                return true;
            }
        });
        getLocationPermission(); //permission 후 자동 맵 호출
    }

    private void initMap() {
        bottomNav.setSelectedItemId(R.id.tab_map);
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

        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i("fff", "eeeeeeeeee");
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);

            getDeviceLocation();
            //LatLng latLocation = new LatLng(clientLocation.getLatitude(), clientLocation.getLatitude());
            //mMap.addMarker(new MarkerOptions().position(latLocation).title("ur location"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLocation));
            /*Location clientLocation = getDeviceLocation();

            LatLng latLocation = new LatLng(clientLocation.getLatitude(), clientLocation.getLatitude());
            mMap.addMarker(new MarkerOptions().position(latLocation).title("ur location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLocation));*/
        }

        // Add a marker in Sydney and move the camera

        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private Location getDeviceLocation() {
        Log.d("ff", "getting location of user");
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //final Location[] ret = {};

        try {
            if (mLocationPermissionsGranted) {
                Log.i("ff", "trying...");
                Task<Location> locationResult = locationProviderClient.getLastLocation();
                if (locationResult == null) Log.i("ff", "DANGER");
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location currentLocation = task.getResult();
                        Log.i("ff", "" + currentLocation.getLatitude());
                        LatLng latLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLocation).title("ur location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLocation));
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

    }
}