package com.squid0928.project.fragments;

import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.common.collect.Maps;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.listeners.MapMarkerManager;

public class MarkerTrayFragment extends Fragment implements View.OnClickListener{
    private MapsActivity mapsActivity;
    private GoogleMap map;

    public MarkerTrayFragment(MapsActivity maps, GoogleMap map) {
        this.mapsActivity = maps;
        this.map = map;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.bottom_slide_up));
        setExitTransition(inflater.inflateTransition(R.transition.bottom_slide_down));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marker_tray, container, false);
        //RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        //radioGroup.setOnCheckedChangeListener(this);
        CheckBox box1 = view.findViewById(R.id.checkBox);
        CheckBox box2 = view.findViewById(R.id.checkBox2);
        box1.setOnClickListener(this);
        box2.setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        if (!(view instanceof CheckBox)) return;
        boolean checked = ((CheckBox) view).isChecked();
        Log.i("ff", "checkbox clicked");

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBox: //추억
                if (checked) {
                    MapMarkerManager.updateMarker(2, true);
                }
                else {
                    MapMarkerManager.updateMarker(2, false);
                }
                break;
            case R.id.checkBox2: //약속
                if (checked) {
                    MapMarkerManager.updateMarker(1, true);
                }
                else {
                    MapMarkerManager.updateMarker(1, false);
                }
                break;
            // TODO: Veggie sandwich
        }
    }

}
