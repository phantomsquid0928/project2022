package com.squid0928.project.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squid0928.project.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    FirebaseUser user;
    TextView userEmail;
    TextView userName;
    String idToken;
    private DatabaseReference mDatabase;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // fragment의 레이아웃 로드
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println(user.toString());
        View view;
        if(user != null) {
            view = inflater.inflate(R.layout.fragment_settings, null);
            userEmail = (TextView)view.findViewById(R.id.userEmail);
            userEmail.setText(user.getEmail());

            // mDatabase.child("firebaselogintest").child("UserAccount").
            userName = (TextView)view.findViewById(R.id.userName);

            return view;
        }

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}