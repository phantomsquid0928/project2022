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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SliderFragment extends Fragment implements View.OnDragListener{
    private static MapsActivity mapsActivity;
    private GoogleMap map;
    private PlacesClient placesClient;
    private RangeSlider slider;

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
        adjustRange();
        slider.setValueFrom(0);
        slider.setMinSeparationValue(1);
        slider.setStepSize(1);
        slider.setOnDragListener(this);
        return view;
    }
    public void adjustRange() {
        Collection<InputData> temp = MapsActivity.user_data.get(MapsActivity.user).getSavedInputMarkers().values();
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
        if (oldest.equals(LocalDate.MAX) && latest.equals(LocalDate.MIN) || oldest.equals(latest)) {
            slider.setValueTo(1);
            slider.setValues((float)0, (float)1);
            return;
        }
        Period period = oldest.until(latest);
        int days = period.getDays();
        slider.setValues((float)0, (float)days);
        slider.setValueTo(days);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        return false;
    }
}
