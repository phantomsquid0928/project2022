package com.squid0928.project.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squid0928.project.MainActivity;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.listeners.MapMarkerManager;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class TopSearchFragment extends Fragment implements View.OnClickListener{
    private MapsActivity mapsActivity;
    private GoogleMap map;
    private PlacesClient placesClient;
    private ActivityResultLauncher<Intent> launcher;

    public TopSearchFragment(MapsActivity maps, GoogleMap map, PlacesClient placesClient) {
        this.mapsActivity = maps;
        this.map = map;
        this.placesClient = placesClient;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        //setExitTransition(inflater.inflateTransition(R.transition.top_slide_up));
        setEnterTransition(inflater.inflateTransition(R.transition.top_slide_down));
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult(result));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topsearch, container, false);
        //Button search = (Button)view.findViewById(R.id.button_search);
        TextInputLayout layout = (TextInputLayout)view.findViewById(R.id.top_search_input);
        layout.getEditText().setOnClickListener(this);
        //search.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) { ///api로 찾기
        /*if (view instanceof Button) {
            Log.i("ff", "button cli");
            Button search = (Button) view.findViewById(R.id.button_search);
            View parentView = view.getRootView();
            TextInputLayout textInputLayout = (TextInputLayout) parentView.findViewById(R.id.top_search_input);
            String input = textInputLayout.getEditText().getText().toString();
            if (input == null) {
                Log.i("ff", "null string input");
            }
            Toast.makeText(this.getContext(), input, Toast.LENGTH_SHORT).show();
        }*/
        if (view instanceof EditText) {
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                    Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)).build(getContext());
            launcher.launch(intent);
        }
    }
    public void handleActivityResult(ActivityResult result) {
        Intent intent = result.getData();
        if (result.getResultCode() == RESULT_CANCELED) return;
        if (result.getResultCode() != RESULT_OK) {
            Status status = Autocomplete.getStatusFromIntent(intent);
            Log.i("ff", status.getStatusMessage());
        }
        Place place = Autocomplete.getPlaceFromIntent(intent);

        if (place == null) return;
        Marker marker = MapMarkerManager.addMarker(place.getName(), place, 1);
        Log.i("ff", "result ok" + place.getName() + place.getLatLng().toString());
        mapsActivity.markers.put("search", marker);
        mapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));

        /*
        final String placeId = place.getId();

// Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place1 = response.getPlace();
            Log.i("ff", "Place found: " + place1.getName());
            Log.i("ff", "place loc" + place1.getLatLng());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("ff", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });*/

    }

}
