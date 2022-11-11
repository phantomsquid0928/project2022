package com.squid0928.project.fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squid0928.project.MainActivity;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;

public class TopSearchFragment extends Fragment implements View.OnClickListener{
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
        search.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) { ///api로 찾기
        Button search = (Button)view.findViewById(R.id.button_search);
        TextInputLayout inputEditText = (TextInputLayout) view.findViewById(R.id.top_search_input);
        Toast.makeText(this.getContext(),""+ inputEditText.toString(), Toast.LENGTH_SHORT).show();
    }

}
