package com.squid0928.project.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.squid0928.project.MapsActivity;
import com.squid0928.project.R;
import com.squid0928.project.placeholder.PlaceholderContent;
import com.squid0928.project.utils.UserData;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class FriendTabFragment extends Fragment implements MyItemRecyclerViewAdapter.OnItemClicked {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private GoogleMap map;
    private MapsActivity mapsActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public FriendTabFragment(MapsActivity mapsActivity, GoogleMap mMap) {
        this.mapsActivity = mapsActivity;
        this.map = mMap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentManager manager = mapsActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment target = manager.findFragmentByTag("FabFriend");
        Fragment target2 = manager.findFragmentByTag("FabFriendMenu");
        Fragment target3 = manager.findFragmentByTag("addFriend");
        Fragment target4 = manager.findFragmentByTag("delFriend");

        if (target != null) {
            transaction.remove(target);
        }
        if (target2 != null) {
            transaction.remove(target2);
        }
        if (target3 != null) {
            transaction.remove(target3);
        }
        if (target4 != null) {
            transaction.remove(target4);
        }
        transaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_tab_list, container, false);
        FragmentManager manager = mapsActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map, new FabOnFriendFragment(mapsActivity, map), "FabFriend");
        transaction.commit();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            //recyclerView.addOnItemTouchListener();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(PlaceholderContent.createInstance(mapsActivity).ITEMS);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    boolean clicked = false;
    @Override
    public void onItemClick(View v, int position) {
        FragmentManager manager = mapsActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (clicked == true) {
            Fragment target = manager.findFragmentByTag("friendManagePopup");
            clicked = false;
            if (target != null)
                transaction.remove(target);
            return;
        }
        clicked = true;

        UserData data = mapsActivity.userdata; //TODO ??????
        List<String> friends = data.getFriends();
        //mapsActivity.db.collection("userdata").get()
        //UserData clickedFriend = mapsActivity.userdata.getFriends().get(position);
        String clickedFriend = mapsActivity.userdata.getFriends().get(position);
        Log.i("ff", "itemclick " + position + "name: " + clickedFriend/*.getAccount().getName()*/);

        transaction.add(R.id.map, new FriendTabTrayFragment(mapsActivity, map), "FabFriendMenu");
        transaction.commit();

    }
}