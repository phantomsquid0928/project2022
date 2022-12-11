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

import java.util.List;
import java.util.Map;

public class AddFriendFragment extends Fragment implements View.OnClickListener {
    private MapsActivity mapsActivity;
    private GoogleMap map;
    private int mod;

    public AddFriendFragment(MapsActivity maps, GoogleMap map, int mod) {
        this.mapsActivity = maps;
        this.map = map;
        this.mod = mod;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.top_slide_down));
        setExitTransition(inflater.inflateTransition(R.transition.top_slide_up));
    }
    public void setMod (int mod) {
        this.mod = mod;
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
            if (mod == 0) { //add
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
                                    if (mapsActivity.userdata.getFriends().contains(snapshot.getId())) {
                                        text.setHint("already existing friend :P");
                                        text.setText("");
                                        break;
                                    }
                                    Toast.makeText(getContext(), "성공적으로 추가했습니다.", Toast.LENGTH_SHORT).show();
                                    mapsActivity.userdata.addFriends(snapshot.getId());
                                    mapsActivity.db.collection("userdata").document(mapsActivity.user).update("friends", mapsActivity.userdata.getFriends());
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
            if (mod == 1) { //del
                View parent = (View)view.getParent();
                EditText text = parent.findViewById(R.id.friend_add_edittext);
                String input = text.getText().toString();
                Log.i("ff", input);
                Log.i("ff", "wer in del");
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
                                    if (snapshot.getId().equals(mapsActivity.user)) {
                                        text.setHint("you cant delete yourself :P");
                                        text.setText("");
                                        break;
                                    }
                                    Toast.makeText(getContext(), "성공적으로 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                    mapsActivity.userdata.delFriends(snapshot.getId());
                                    Log.i("ff", mapsActivity.userdata.getFriends().toString());
                                    mapsActivity.db.collection("userdata").document(mapsActivity.user).update("friends", mapsActivity.userdata.getFriends());
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
}
