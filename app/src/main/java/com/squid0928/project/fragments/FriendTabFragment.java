package com.squid0928.project.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FriendTabFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static FriendTabFragment newInstance(int columnCount) {
        FriendTabFragment fragment = new FriendTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_tab_list, container, false);

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
            MyItemRecyclerViewAdapter adapter = new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS);
            adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onItemClick(View v, int position) {
        UserData data = MapsActivity.user_data.get("phantomsquid0928");
        List<UserData> friends = data.getFriends();
        UserData clickedFriend = friends.get(position);
        Toast.makeText(v.getContext(), "fsf", Toast.LENGTH_SHORT);
        Log.i("ff", "itemclick " + position + " on view :" + v.toString());

    }
}