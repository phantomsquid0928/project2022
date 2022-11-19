package com.squid0928.project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimetableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimetableFragment extends Fragment {

    FirebaseUser user;
    private DatabaseReference mDatabase;
    View[][] classes;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TimetableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimetableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimetableFragment newInstance(String param1, String param2) {
        TimetableFragment fragment = new TimetableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        View view;
        if(user != null) {
            view = inflater.inflate(R.layout.fragment_timetable, null);

            int i = 0;
            classes = new View[10][];
            for(i = 0; i < 10; i++)
                classes[i] = new View[5]; // 여기까지 OK


            TableRow am09 = view.findViewById(R.id.am09);
            TableRow am10 = view.findViewById(R.id.am10);
            TableRow am11 = view.findViewById(R.id.am11);
            TableRow pm12 = view.findViewById(R.id.pm12);
            TableRow pm13 = view.findViewById(R.id.pm13);
            TableRow pm14 = view.findViewById(R.id.pm14);
            TableRow pm15 = view.findViewById(R.id.pm15);
            TableRow pm16 = view.findViewById(R.id.pm16);
            TableRow pm17 = view.findViewById(R.id.pm17);
            TableRow pm18 = view.findViewById(R.id.pm18);

            return view;
        }
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    View.OnClickListener classListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "Button Clicked", Toast.LENGTH_LONG).show();
        }
    };
}