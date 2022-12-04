package com.squid0928.project.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squid0928.project.MainActivity;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.listeners.MapMarkerManager;
import com.squid0928.project.utils.InputData;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.Period;
import org.threeten.bp.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SliderFragment extends Fragment implements RangeSlider.OnChangeListener{
    private static MapsActivity mapsActivity;
    private GoogleMap map;
    private PlacesClient placesClient;
    private RangeSlider slider;
    private LocalDate targetoldest; //각각용 둘다 추억 약속 // 올드, 올드, 오늘
    private int mod;          //둘다 추억 약속 0 1 2

    public SliderFragment() {

    }
    public static SliderFragment newInstance(MapsActivity maps, GoogleMap map, PlacesClient placesClient) {
        SliderFragment fragment = new SliderFragment();
        mapsActivity = maps;
        map = map;
        placesClient = placesClient;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.bottom_slide_down));
        setEnterTransition(inflater.inflateTransition(R.transition.bottom_slide_up));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider, container, false);
        slider = view.findViewById(R.id.slider);
        adjustRange(0);
        mod = 0;
        slider.setValueFrom(0);
        slider.setMinSeparationValue(1);
        slider.setStepSize(1);
        slider.addOnChangeListener(this);
        return view;
    }
    public void adjustRange(int mod) {
        Collection<InputData> temp = mapsActivity.userdata.getSavedInputMarkers().values();
        for (InputData target : temp) {
            Log.i("ff", "dates st : " + target.getDateFrom());
            Log.i("ff", "dates ed : " + target.getDateTo());
        }
        LocalDate oldest = LocalDate.MAX;
        LocalDate latest = LocalDate.MIN;
        for (InputData target : temp) {
            LocalDate datePoint;
            LocalDate dateStart;
            LocalDate dateEnd;
            if (target.getType() == 1) { //약속
                if (target.getDateFrom() == null) continue;
                datePoint = LocalDate.parse(target.getDateFrom(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (latest.isBefore(datePoint)) {
                    latest = datePoint;
                }
                if (oldest.isAfter(datePoint)) {
                    oldest = datePoint;
                }
            }
            if (target.getType() == 2) {
                if (target.getDateFrom() == null) continue;
                dateStart = LocalDate.parse(target.getDateFrom(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dateEnd = LocalDate.parse(target.getDateTo(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (latest.isBefore(dateEnd)) {
                    latest = dateEnd;
                }
                if (oldest.isAfter(dateStart)) {
                    oldest = dateStart;
                }
            }
        }
        Log.i("ff", "suceed" + oldest.toString() + " ~ " + latest.toString());
        if (oldest.equals(LocalDate.MAX) && latest.equals(LocalDate.MIN) || oldest.equals(latest)) {
            slider.setValueTo(1);
            slider.setValues((float)0, (float)1);
            return;
        }
        String old = oldest.toString();
        String lat = latest.toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date1 = sdf.parse(old);
            Date date2 = sdf.parse(lat);
            long diff = date2.getTime() - date1.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS); //둘다용
            LocalDate today = LocalDate.now();
            Date tdate = sdf.parse(today.toString());
            long diff2 = tdate.getTime() - date1.getTime(); // 추억용
            long days2 = TimeUnit.DAYS.convert(diff2, TimeUnit.MILLISECONDS);
            long diff3 = date2.getTime() - tdate.getTime();
            long days3 = TimeUnit.DAYS.convert(diff3, TimeUnit.MILLISECONDS); //약속용

            slider.setValues((float)0, (float)days);


            switch(mod) {
                case 0: //둘다
                    slider.setValues((float)0, (float)days);
                    slider.setValueTo((float)days);
                    targetoldest = oldest;
                    break;
                case 1://추억만
                    slider.setValues((float)0, (float)days2);
                    slider.setValueTo((float)days2);
                    targetoldest = oldest;
                    break;
                case 2: //약속만
                    slider.setValues((float)0, (float)days3);
                    slider.setValueTo((float)days3);
                    targetoldest = today;
                    break;
            }
            final LocalDate targetdate = oldest;
            slider.setLabelFormatter(new LabelFormatter() {
                @NonNull
                @Override
                public String getFormattedValue(float value) {
                    return targetdate.plusDays((int)(value)).toString();
                }
            });
        } catch (Exception e) {Log.i("ff", e.toString());}
    }

    public void onStartTrackingTouch(@NonNull RangeSlider slider) {

    }

    public void onStopTrackingTouch(@NonNull RangeSlider slider) {

    }

    @Override
    public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
        //Log.i("ff", "thumb" + slider.getActiveThumbIndex() + ": : : " + value);

        int x = slider.getValues().get(0).intValue();
        int y = slider.getValues().get(1).intValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Collection<InputData> data = mapsActivity.userdata.getSavedInputMarkers().values();
        try {
            // Log.i("ff", "rangex: " + x + "rangey:" + y);
            Date rangex = sdf.parse(targetoldest.plusDays(x).toString());
            Date rangey = sdf.parse(targetoldest.plusDays(y).toString());
            //Log.i("ff", "rangex: " + rangex.toString() + "rangey:" + rangey.toString());
            for(InputData target : data) {
                Marker marker = mapsActivity.markers.get(target.getScheduleName());
                if (target.getType() == 1) {
                    if (target.getDateFrom() == null) continue;
                    Date datePoint = sdf.parse(target.getDateFrom());

                    if (datePoint.after(rangey) || datePoint.before(rangex)) {
                        MapMarkerManager.updateTarget(marker, false);
                    }
                    else {
                        MapMarkerManager.updateTarget(marker, true);
                    }
                }
                if (target.getType() == 2) {
                    if (target.getDateFrom() == null) continue;
                    Date dateStart = sdf.parse(target.getDateFrom());
                    Date dateEnd = sdf.parse(target.getDateTo());

                    if ((dateStart.after(rangex) || dateStart.equals(rangex)) && (dateEnd.before(rangey) || dateEnd.equals(rangey))) {
                        MapMarkerManager.updateTarget(marker, true);
                    }
                    else {
                        MapMarkerManager.updateTarget(marker, false);
                    }
                }

            }
        } catch (Exception e) {}
    }
}
