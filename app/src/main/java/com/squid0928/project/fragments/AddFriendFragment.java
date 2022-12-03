package com.squid0928.project.fragments;

import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.utils.UserData;

import java.util.Map;

public class AddFriendFragment extends Fragment implements View.OnClickListener {
    private MapsActivity mapsActivity;
    private GoogleMap map;

    public AddFriendFragment(MapsActivity maps, GoogleMap map) {
        this.mapsActivity = maps;
        this.map = map;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        //setEnterTransition(inflater.inflateTransition(R.transition.top_slide_down));
        //setExitTransition(inflater.inflateTransition(R.transition.top_slide_up));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_add_popup, container, false);
        Button button = view.findViewById(R.id.friend_add_button);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            View parent = (View)view.getParent();
            EditText text = parent.findViewById(R.id.friend_add_edittext);
            String input = text.getText().toString();
            Log.i("ff", input);
            if (input.equals("")) {
                Toast.makeText(parent.getContext(), "please input email", Toast.LENGTH_SHORT);
                return;
            }
            mapsActivity.db.collection("userdata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    boolean success = false;
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.i("ff", snapshot.getId() + "->" + snapshot.getData());
                            if (snapshot.getId().equals(input)) {
                                success = true;
                                UserData target = snapshot.toObject(UserData.class);
                                if (snapshot.getId().equals(mapsActivity.user)) {
                                    text.setHint("you cant be your friend yourself :P");
                                    text.setText("");
                                    break;
                                }
                                if (mapsActivity.user_data.get(mapsActivity.user).getFriends().contains(snapshot.getId())) {
                                    text.setHint("already existing friend :P");
                                    text.setText("");
                                    break;
                                }
                                mapsActivity.user_data.get(mapsActivity.user).addFriends(snapshot.getId());
                                mapsActivity.db.collection("userdata").document(mapsActivity.user).update("friends", mapsActivity.user_data.get(mapsActivity.user).getFriends());
                            }
                        }
                        if (!success) {
                            Toast.makeText(parent.getContext(), "no user found", Toast.LENGTH_SHORT).show();
                            text.setHint("there is no user using that email");
                            text.setText("");
                        }
                    }
                    else{
                        Log.i("ff", "failed to get db");
                    }
                }
            });
        }
    }
}
