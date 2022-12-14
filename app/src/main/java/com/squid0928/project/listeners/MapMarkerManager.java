package com.squid0928.project.listeners;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.common.collect.Maps;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.fragments.InputTemplateFragment;
import com.squid0928.project.utils.InputData;
import com.squid0928.project.utils.Locations;
import com.squid0928.project.utils.UserData;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
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
        FragmentManager manager = mapsActivity.getSupportFragmentManager();
        Fragment createdLast = manager.findFragmentByTag("fff");
        FragmentTransaction transaction = manager.beginTransaction();
        if (createdLast != null) {
            transaction.remove(createdLast);
            transaction.commit();
            String[] forbiden = {"search", "poi - ", "myloc"};

            ArrayList<String> removed = new ArrayList<>();
            for (String key : mapsActivity.markers.keySet()) {
                if (key.equals(forbiden[0])) {
                    mapsActivity.markers.get(key).remove();
                    removed.add(key);
                }
                if (key.equals(forbiden[2])) {
                    mapsActivity.markers.get(key).remove();
                    removed.add(key);
                }
                if (key.contains(forbiden[1]) && key.substring(0, 6).equals(forbiden[1])) {
                    mapsActivity.markers.get(key).remove();
                    removed.add(key);
                }
            }
            for (String key : removed) {
                mapsActivity.markers.remove(key);
            }
            return false;
        }

        markerClicked = true;
        //FragmentManager manager = mapsActivity.getSupportFragmentManager();
        //FragmentTransaction transaction = manager.beginTransaction();
        marker.showInfoWindow();


        Set<String> temp = mapsActivity.userdata.getSavedInputMarkers().keySet();
        String target = null;
        for (String key : temp) {

            Log.i("ff", key + " :: " + marker.getTitle());
            if (key.equals(marker.getTitle())) {
                target = key;
            }
        }
        //TODO
        // change below code can access to serverside
        InputData inputData = null;
        if (target != null) {
            inputData = mapsActivity.userdata.getMarker(target);
            Log.i("ff", "inputdata exitst" + inputData.getType());
        }
        File rootsd = mapsActivity.getApplicationContext().getExternalCacheDir();
        File path1;

        if (Build.MODEL.contains("Emulator")) { //TODO useless code
            path1 = new File( "mnt/user/0/primary/DCIM/project");
        }
        else {
            path1 = new File(rootsd.getAbsolutePath() + "/photos");
        }
        //path1 = new File( "/mnt/user/0/primary/DCIM/projectImages/");
        File dd = null;
        if (inputData != null) {
            dd = new File(path1 + "/" + MapsActivity.user + "/" + inputData.getScheduleName() + ".jpg");
        }
        if (inputData != null && inputData.getPhoto() != null && !dd.exists()) {
            Log.i("ff", "loading...");
            Toast.makeText(mapsActivity.getApplicationContext(), "없는 이미지를 다운로드 중입니다... 반영에 시간이 걸립니다.",Toast.LENGTH_SHORT).show();
            MapsActivity.storageManager.setFFPath(inputData.getPhoto());
            //MapsActivity.storageManager.setPath(mapsActivity.getApplicationContext(), Uri.parse(Uri.parse(inputData.getPhoto()).getPath()));
            Log.i("ff", "path: " + MapsActivity.storageManager.path);
            MapsActivity.storageManager.loadImg(inputData.getScheduleName());
        }

        Log.i("ff", "previous" + markerid + "\nnow " + marker.getId());
        if (markerid.equals(marker.getId())) {
            markerid = "";
            InputTemplateFragment fragment;

            if (inputData != null) {
                fragment = new InputTemplateFragment(inputData); //TODO exist inputdata show
                boolean exists = false;
                try {
                    InputStream is = mapsActivity.getApplicationContext().getContentResolver().openInputStream(Uri.parse(inputData.getPhoto()));
                    exists = true;
                }
                catch (Exception e) {

                }
                if (!exists) {
                    Log.i("ff", "file2 not exists");
                    MapsActivity.storageManager.setFFPath(inputData.getPhoto());
                    MapsActivity.storageManager.loadImg(inputData.getScheduleName());
                    fragment = new InputTemplateFragment(inputData, mapsActivity);
                }
            }
            else {
                fragment = new InputTemplateFragment();
            }
            /*if (inputData != null && inputData.getPhoto()) {
                Uri file = Uri.parse(inputData.getPhoto());

                // File dd = new File("Android/sdcard/DCIM/projectImages/" + inputData.getScheduleName());
                Log.i("ff", file.toString() + ": : :: " + file.getEncodedPath());
            }*/

            transaction.add(R.id.map, fragment, "fff");
            transaction.commit();
            LatLng latLng = marker.getPosition();

            String placeName = marker.getTitle();
            Log.i("ff", "marker place" + placeName);

            manager.setFragmentResultListener("key", mapsActivity, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    InputData res = (InputData)result.getSerializable("inputData");
                    boolean mod = result.getBoolean("mod");
                    boolean transitionRes = result.getBoolean("res");
                    if (!transitionRes) {
                        //mapsActivity.markers.remove("myloc");
                        return;
                    }
                    if (res == null && transitionRes) {//삭제
                        UserData target = mapsActivity.userdata;
                        Log.i("ff", "delete all");
                        String oldname = result.getString("old");
                        if (target.getSavedInputMarkers().get(oldname).getPhoto() != null) {
                            MapsActivity.storageManager.delImage(oldname);
                        }
                        MapMarkerManager.removeMarker(oldname);
                        target.getSavedInputMarkers().remove(oldname);
                        target.getSavedLocations().remove(oldname);
                        mapsActivity.saveToDB();

                        mapsActivity.slider.adjustRange(0);
                        return;
                    }
                    UserData target = mapsActivity.userdata; //TODO 바꿔라
                    if (mod) { //이미 잇는 마커 수정
                        Log.i("ff", "we r in mod" + res.getScheduleName());
                        String oldname = result.getString("old");
                        String oldimg = result.getString("oldimg"); //oldimg 잇으면 fire storage삭제
                        boolean isimgchange = result.getBoolean("imgchange");
                        if (isimgchange) {
                            if (res.getPhoto() == null || res.getPhoto() == "") { //사진삭제
                                MapsActivity.storageManager.delImage(oldname);
                            }
                            if (res.getPhoto() != null) {//사진 추가
                                MapsActivity.storageManager.setFFPath(res.getPhoto());
                                MapsActivity.storageManager.saveImg(res.getScheduleName());
                            }
                        }
                        MapMarkerManager.removeMarker(oldname);
                        target.getSavedInputMarkers().remove(oldname);
                        target.getSavedLocations().remove(oldname);
                    }

                    Marker marker = MapMarkerManager.addMarker(res.getScheduleName(), latLng, res.getType()); //TODO 바꿔라
                    target.getSavedInputMarkers().put(res.getScheduleName(), res);
                    Locations loc = new Locations(res.getScheduleName(), latLng, placeName, res.getType());
                    target.getSavedLocations().put(res.getScheduleName(), loc);
                    MapsActivity.markers.put(res.getScheduleName(), marker);

                    mapsActivity.saveToDB();

                    mapsActivity.slider.adjustRange(0);
                    if (!res.isEmpty()) { // TODO : no safe checker

                    }
                }
            });
        } else {
            markerid = marker.getId();
            createdLast = manager.findFragmentByTag("fff");

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

    public static Marker addMarker(String name, Place place, int type) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place.getLatLng());
        markerOptions.title(name);
        if (type == 1) {
            markerOptions.icon(BitmapFromVector(mapsActivity.getApplicationContext(), R.drawable.ic_baseline_circle_1_24));
        }
        if (type == 2) {
            markerOptions.icon(BitmapFromVector(mapsActivity.getApplicationContext(), R.drawable.ic_baseline_circle_24));
        }
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(type);
        return marker;
    }
    public static Marker addMarker(String name, LatLng latLng, int type) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(name);
        if (type == 1) {
            markerOptions.icon(BitmapFromVector(mapsActivity.getApplicationContext(), R.drawable.ic_baseline_circle_1_24));
        }
        if (type == 2) {
            markerOptions.icon(BitmapFromVector(mapsActivity.getApplicationContext(), R.drawable.ic_baseline_circle_24));
        }
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(type);
        return marker;
    }
    public static void removeMarker(String name) {
        Marker target = MapsActivity.markers.get(name);
        Log.i("ff", target.getTitle());
        target.remove();
        MapsActivity.markers.remove(name);
        target.remove();
    }
    public static void updateMarker(int tag, boolean visible) {
        for (Marker target : MapsActivity.markers.values()) {
            if (tag == (int)target.getTag()) {
                target.setVisible(visible);
            }
        }
    }
    public static void updateTarget(Marker marker, boolean visible) {
        marker.setVisible(visible);
    }


    private static BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public static boolean isMarkerClicked() {return markerClicked;}
}
