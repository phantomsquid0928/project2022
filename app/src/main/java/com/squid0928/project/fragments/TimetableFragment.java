package com.squid0928.project.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squid0928.project.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimetableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimetableFragment extends Fragment {

    FirebaseUser user;
    private DatabaseReference mDatabase;
    ArrayList<ArrayList<TextView>> classes;

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

        // 버튼들을 객체로 받기 위한 배열
        classes = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        View view;
        if(user != null) {
            view = inflater.inflate(R.layout.fragment_timetable, null);

            // 각 tablerow를 가져온 다음 for문을 통해 각 tablerow에 담긴 textview들을 추가한다.
            TableRow am09 = view.findViewById(R.id.am09);
            addTableViews(am09);
            TableRow am10 = view.findViewById(R.id.am10);
            addTableViews(am10);
            TableRow am11 = view.findViewById(R.id.am11);
            addTableViews(am11);
            TableRow pm12 = view.findViewById(R.id.pm12);
            addTableViews(pm12);
            TableRow pm13 = view.findViewById(R.id.pm13);
            addTableViews(pm13);
            TableRow pm14 = view.findViewById(R.id.pm14);
            addTableViews(pm14);
            TableRow pm15 = view.findViewById(R.id.pm15);
            addTableViews(pm15);
            TableRow pm16 = view.findViewById(R.id.pm16);
            addTableViews(pm16);
            TableRow pm17 = view.findViewById(R.id.pm17);
            addTableViews(pm17);
            TableRow pm18 = view.findViewById(R.id.pm18);
            addTableViews(pm18);

            return view;
        }
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    void addTableViews(TableRow row) {
        if(row == null) return;

        TextView view;
        int j;
        ArrayList<TextView> input = new ArrayList<>();
        for(j = 0; j < row.getChildCount(); ++j) {
            view = (TextView)row.getVirtualChildAt(j); // 각 자식들을 받아내고
            if(j != 0) // 시간 축이 아니면 리스너 추가
                view.setOnClickListener(classListener); // 텍스트뷰 클릭 시 리스너 추가
            input.add(view);
            // System.out.println(((TextView)am09.getVirtualChildAt(j)).getText().toString());
        }
        classes.add(input);
    }

    View.OnClickListener classListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_timetable, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("시간표 설정").setMessage("시간표 설정");
            builder.setView(dialogView);

            EditText lectureName = (EditText)dialogView.findViewById(R.id.lectureName);
            EditText lectureRoom = (EditText)dialogView.findViewById(R.id.lectureRoom);

            TextView currentView = (TextView)v;

            builder.setPositiveButton("추가하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int pos) {
                    String name = lectureName.getText().toString();
                    String room = lectureRoom.getText().toString();
                    currentView.setText(name + "\n" + room);

                    int red = (int)(Math.random() * 125) + 125;
                    int blue = (int)(Math.random() * 125) + 125;
                    int green = (int)(Math.random() * 125) + 125;
                    currentView.setBackgroundColor(Color.rgb(red, green, blue));
                }
            });
            builder.setNegativeButton("되돌아가기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    };
}