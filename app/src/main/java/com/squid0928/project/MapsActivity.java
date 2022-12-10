package com.squid0928.project;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squid0928.project.databinding.ActivityMapsBinding;
import com.squid0928.project.fragments.FABFragment;
import com.squid0928.project.fragments.FriendTabFragment;
import com.squid0928.project.fragments.InputTemplateFragment;
import com.squid0928.project.fragments.SettingsFragment;
import com.squid0928.project.fragments.SliderFragment;
import com.squid0928.project.fragments.TimetableFragment;
import com.squid0928.project.fragments.TopSearchFragment;
import com.squid0928.project.listeners.MapClickManager;
import com.squid0928.project.listeners.MapMarkerManager;
import com.squid0928.project.listeners.MapMotionManager;
import com.squid0928.project.utils.InputData;
import com.squid0928.project.utils.Locations;
import com.squid0928.project.utils.StorageManager;
import com.squid0928.project.utils.UserData;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnPoiClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1010;

    public static HashMap<String, String> user_data = new HashMap<>(); //서버에서 받아야함, 위험한 정보
    public static UserData userdata = null;
    public static HashMap<String, Marker> markers = new HashMap<>();

    public BottomNavigationView bottomNav;
    private LinearLayout ly;
    private ActivityMapsBinding binding;

    public GoogleMap mMap;

    private UiSettings mUiSettings;
    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient locationProviderClient;
    private View mapView;

    private PlacesClient placesClient;
    final String apiKey = BuildConfig.MAPS_API_KEY;
    private LocationManager manager = null;

    private int status = 0; // home
    public static String user;
    public SliderFragment slider;

    private DatabaseReference mDatabaseRef;
    public static FirebaseFirestore db;
    public static FirebaseAuth mFirebaseAuth;
    public static StorageManager storageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("firebaseInfos");
        db = FirebaseFirestore.getInstance();
        storageManager = new StorageManager(this, FirebaseStorage.getInstance());


        //loginFromPref();
        //if (loadDB()) {}

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        user = intent.getBundleExtra("userInfo").getString("useruid");

        db = FirebaseFirestore.getInstance();
        db.collection("userdata").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userdata = task.getResult().toObject(UserData.class);
                    getLocationPermission(); //permission 후 자동 맵 호출
                    getAlarmPermission();
                    getStoragePermission();
                } else{
                    Log.e("ff", "failed to get info from db");
                    finishAffinity();
                }
            }
        });



        /*binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());*/
        setContentView(R.layout.activity_maps);

        bottomNav = findViewById(R.id.bottomView);
        ly = findViewById(R.id.home_layout);
        Places.initialize(getApplicationContext(), apiKey);
        placesClient = Places.createClient(this);
        AndroidThreeTen.init(this);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment[] fragment = {manager.findFragmentByTag("friendList"),
                    manager.findFragmentByTag("time"),
                    manager.findFragmentByTag("setting")};
            for(int i = 0; i < fragment.length; i++) {
                if(fragment[i] != null) {
                    transaction.remove(fragment[i]);
                }
            }
            switch (id) {
                case R.id.tab_map:
                    transaction.commit();
                    break;
                case R.id.tab_friend:
                    transaction.add(R.id.map, new FriendTabFragment(this, mMap), "friendList");
                    transaction.commit();
                    break;
                case R.id.tab_timetable:
                    transaction.add(R.id.map, new TimetableFragment(), "time");
                    transaction.commit();
                    break;
                case R.id.tab_settings:
                    transaction.add(R.id.map, new SettingsFragment(), "setting");
                    transaction.commit();
                    break;
            }
            return true;
        });
        bottomNav.setOnItemReselectedListener(item -> {
            return;
        });
        bottomNav.setSelectedItemId(R.id.tab_map);
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void createTopSearch() {
        FragmentManager manager = getSupportFragmentManager();
        //Fragment createdLast = manager.findFragmentByTag("topsearch");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map, new TopSearchFragment(this, mMap, placesClient), "topsearch");
        transaction.commit();
    }
    public void createFab() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map, new FABFragment(this, mMap), "FAB");
        transaction.commit();
    }
    public void createSlider() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        slider = SliderFragment.newInstance(this, mMap, placesClient);
        transaction.add(R.id.map, slider, "slider");
        transaction.commit();
    }
    private void initMap() {
        createTopSearch();
        createFab();
        createSlider();
        getDeviceLocation();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        moveMapButtons(0, 1);
        moveMapButtons(1, 1);
    }
    public void moveMapButtons(int target, int mod) {
        final String[] targetList = {"GoogleMapMyLocationButton", "GoogleMapZoomInButton", "GoogleMapToolbar"};
        final int[][] modUpMargins = {{0, 0, 30, 1000}, {0, 0, 30, 800}, {0, 0, 30, 600}};
        final int[][] modDownMargins = {{0, 0, 30, 1000}, {0, 0, 30, 300}};
        switch(mod) {
            case 0: //move up
                View targetView = null;
                if (target == 0) targetView = mapView.findViewWithTag(targetList[target]);
                if (target == 1) targetView = (View)mapView.findViewWithTag(targetList[target]).getParent();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        targetView.getLayoutParams();
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(modUpMargins[target][0], modUpMargins[target][1], modUpMargins[target][2], modUpMargins[target][3]);
                break;
            case 1: //move down
                View targetView2 = null;
                if (target == 0) targetView2 = mapView.findViewWithTag(targetList[target]);
                if (target == 1) targetView2 = (View)mapView.findViewWithTag(targetList[target]).getParent();
                RelativeLayout.LayoutParams layoutParamss = (RelativeLayout.LayoutParams)
                        targetView2.getLayoutParams();
                // position on right bottom
                layoutParamss.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParamss.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParamss.setMargins(modDownMargins[target][0], modDownMargins[target][1], modDownMargins[target][2], modDownMargins[target][3]);
                break;

        }
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
        mUiSettings.setMapToolbarEnabled(true);

        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
        mMap.setOnMarkerClickListener(new MapMarkerManager(this, mMap));
        mMap.setOnMapClickListener(new MapClickManager(this, mMap));
        //mMap.setOnMapLongClickListener(new MapLongClickManager(this, mMap));
        mMap.setOnPoiClickListener(this);
        mMap.setOnCameraMoveStartedListener(new MapMotionManager(this, mMap));
        mMap.setOnCameraIdleListener(new MapMotionManager(this, mMap));
        if (!restoreUserMarkers()) {
            Log.i("ff", "weird");
        }
    }

    /*@Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        db.collection("userdata").document(user).set(user_data.get(user));
    }*/
    /*public boolean loginFromPref() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String id = preferences.getString("id", "<<>>");
        String pass = preferences.getString("pass", "<<>>");
        if (id.equals("<<>>") || pass.equals("<<>>")) {
            return false;
        }

        mFirebaseAuth.signInWithEmailAndPassword(id, pass).addOnCompleteListener(MapsActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                System.out.println("login buttons");
                // 로그인이 성공적이면
                if(task.isSuccessful()) {
                    return;
                } else // 로그인 실패
                {
                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
            }
        });
        return
    }*/
    public void saveToDB() {
        db.collection("userdata").document(user).set(userdata);
    }
    //TODO change below code can access to server data
    private boolean restoreUserMarkers() {

        //user_data.put("phantomsquid0928", tempInfo);
        Log.i("ff", "username: " + user);
        //Log.i("ff", "hashmap: " + user_data.keySet().toString() + "values" + user_data.values().toString());

        //UserData userData = user_data.get(user); //서버에서 받아야함
        //Log.i("ff", "info ofuserdata: " + userData.getSavedLocations());

        if(userdata == null) return false;
        Set<String> keySet = userdata.getSavedLocations().keySet();
        if (keySet.isEmpty()) return false;
        for(String target : keySet) {
            Log.i("ff", "key: " + target);
            InputData data = userdata.getSavedInputMarkers().get(target);
            Locations locdata = userdata.getSavedLocations().get(target);
            Log.i("ff", "info of inputdata: " + data);
            //temp.title(target.getName());
            Marker marker = MapMarkerManager.addMarker(data.getScheduleName(), new LatLng(locdata.getLatitude(), locdata.getLongtitude()), data.getType());
            markers.put(target, marker);
        }
        return true;
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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLocation, 15));
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
    private void getAlarmPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.SET_ALARM)!=PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SET_ALARM}, 356);
    }
    public void getStoragePermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
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
            case 3:

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    @Override
    public boolean onMyLocationButtonClick() {

        FragmentManager manager = getSupportFragmentManager();
        Fragment createdLast = manager.findFragmentByTag("fff");
        FragmentTransaction transaction = manager.beginTransaction();
        if (createdLast != null) {
            transaction.remove(createdLast);
            transaction.commit();
            return false;
        }
        return false;
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        if (markers.containsKey("myloc")) return;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        FragmentManager manager = getSupportFragmentManager();
        Fragment createdLast = manager.findFragmentByTag("fff");
        FragmentTransaction transaction = manager.beginTransaction();
        if (createdLast != null) {
            transaction.remove(createdLast);
            transaction.commit();
            return;
        }
        /*transaction.add(R.id.map, new InputTemplateFragment(), "fff");
        transaction.commit();*/
        Marker marker = MapMarkerManager.addMarker("myloc", latLng, 1);
        markers.put("myloc", marker);
    }
    @Override
    public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {
        /*FindCurrentPlaceRequest request = new FindCurrentPlaceRequest() {
            @Nullable
            @Override
            public CancellationToken getCancellationToken() {
                return null;
            }

            @NonNull
            @Override
            public List<Place.Field> getPlaceFields() {
                return pointOfInterest.placeId
            }
        };*/
        //placesClient.findCurrentPlace();
        if (MapMarkerManager.isMarkerClicked()) return;
        LatLng latlng = pointOfInterest.latLng;
        if (markers.containsKey("poi - " + pointOfInterest.name)) return;
        Marker marker = MapMarkerManager.addMarker("poi - " + pointOfInterest.name, latlng,1);
        markers.put("poi - " + pointOfInterest.name, marker);
    }
}