package com.squid0928.project.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
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

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class TopSearchFragment extends Fragment implements View.OnClickListener{
    private MapsActivity maps;
    public TopSearchFragment(MapsActivity maps) {
        this.maps = maps;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.top_slide_up));
        setEnterTransition(inflater.inflateTransition(R.transition.top_slide_down));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topsearch, container, false);
        Button search = (Button)view.findViewById(R.id.button_search);
        TextInputLayout layout = (TextInputLayout)view.findViewById(R.id.top_search_input);
        layout.getEditText().setOnClickListener(this);
        search.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) { ///api로 찾기
        if (view instanceof Button) {
            Log.i("ff", "button cli");
            Button search = (Button) view.findViewById(R.id.button_search);
            View parentView = view.getRootView();
            TextInputLayout textInputLayout = (TextInputLayout) parentView.findViewById(R.id.top_search_input);
            String input = textInputLayout.getEditText().getText().toString();
            if (input == null) {
                Log.i("ff", "null string input");
            }
            Toast.makeText(this.getContext(), input, Toast.LENGTH_SHORT).show();
        }
        if (view instanceof EditText) {
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                    Arrays.asList(Place.Field.ID, Place.Field.NAME)).build(this.getContext());

            startActivityForResult(intent, 1);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("ff", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("ff", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
