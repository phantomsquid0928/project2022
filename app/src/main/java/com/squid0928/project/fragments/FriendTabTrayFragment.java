package com.squid0928.project.fragments;

import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;

public class FriendTabTrayFragment extends Fragment implements View.OnClickListener{
    private MapsActivity mapsActivity;
    private GoogleMap map;

    public FriendTabTrayFragment(MapsActivity maps, GoogleMap map) {
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
        View view = inflater.inflate(R.layout.fragment_friend_add, container, false);
        Button button = view.findViewById(R.id.button3);
        Button button1 = view.findViewById(R.id.button4);
        button.setOnClickListener(this);
        button1.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            Button button1 = (Button)view;
            Log.i("ff", button1.getText().toString());

            if (button1.getText().toString().contains("추가")){
                //mapsActivity.user_data.values();
                FragmentManager manager = mapsActivity.getSupportFragmentManager();
                Fragment fragment = manager.findFragmentByTag("addFriend");
                Fragment fragment2 = manager.findFragmentByTag("delFriend");
                FragmentTransaction transaction = manager.beginTransaction();

                if (fragment != null) return;
                if (fragment2 != null) {
                    transaction.remove(fragment2);
                }
                transaction.add(R.id.map, new AddFriendFragment(mapsActivity, map, 0), "addFriend");
                transaction.commit();
            }
            if (button1.getText().toString().contains("삭제")){
                //mapsActivity.user_data.values();
                FragmentManager manager = mapsActivity.getSupportFragmentManager();
                Fragment fragment = manager.findFragmentByTag("delFriend");
                Fragment fragment1 = manager.findFragmentByTag("addFriend");
                FragmentTransaction transaction = manager.beginTransaction();

                if (fragment != null) return;
                if (fragment1 != null) {
                    transaction.remove(fragment1);
                }
                transaction.add(R.id.map, new AddFriendFragment(mapsActivity, map, 1), "delFriend");
                transaction.commit();
            }

        }
    }
}
