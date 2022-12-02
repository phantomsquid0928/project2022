package com.squid0928.project.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squid0928.project.LoginActivity;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.utils.UserAccount;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{

    FirebaseUser user;
    TextView userEmail;
    TextView userName;
    String idToken;
    String Name;
    String email;
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
        mDatabase = FirebaseDatabase.getInstance().getReference("firebaselogintest");

        user = FirebaseAuth.getInstance().getCurrentUser();
        View view;
        if(user != null) {
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            userEmail = (TextView)view.findViewById(R.id.userEmail);
            LinearLayout layout = view.findViewById(R.id.settings_all);
           // LinearLayout layout2 = view.findViewById(R.id.settings_all);
            LinearLayout layout3 = view.findViewById(R.id.logout);
            layout.setOnClickListener(this);
            layout3.setOnClickListener(this);

            email = user.getEmail();
            userEmail.setText(email);

            idToken = user.getUid();
            // mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

            userName = (TextView)view.findViewById(R.id.userName);
            userName.setText(idToken);

            mDatabase.child("UserAccount").child(idToken).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.getValue(String.class);
                    userName.setText(value);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return view;
        }

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof LinearLayout) {
            LinearLayout target = (LinearLayout) view;
            TextView target2 = (TextView) target.getChildAt(1);
            Log.i("ff", target2.toString());
            if (target2.getText().toString().contains("아웃")) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(this.getContext(), LoginActivity.class);
                startActivity(intent);
                this.getActivity().finishAffinity();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
            }
        }
    }
}