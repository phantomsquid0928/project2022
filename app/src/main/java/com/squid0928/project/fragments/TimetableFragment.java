package com.squid0928.project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.squid0928.project.R;

import org.threeten.bp.LocalDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimetableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimetableFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser user;
    private DatabaseReference mDatabase;
    private MaterialCalendarView view_calendarView;
    private ListView view_listView;

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
/*        mDatabase = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        View view;
        if(user != null) {
            view = inflater.inflate(R.layout.fragment_settings, null);

            calendarView = view.findViewById(R.id.calendarView);

            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                    // 먼저 날짜 문자열을 가져온다.

                    // 기존에 저장된 날짜 리스트 가져와서, 그 안에서 같은 것들만 비교

                    // 같은 날짜의 리스트만 가져와 리스트뷰로 구현


                    // 기능 2 : 입력받는 다이얼로그 출력
                    // 나중에 추가 버튼 가져올 거니까,,
                    // 리스트에도 추가, 리스트뷰에도 추가,
                }
            });
            return view = inflater.inflate(R.layout.fragment_settings, null);
        }*/
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view_calendarView = view.findViewById(R.id.calendarView);
        view_listView = view.findViewById(R.id.listView);

        view_calendarView.setSelectedDate(LocalDate.now());

        //TODO  유저의 InputData 중 선택한 날짜의 시간 데이터를 받아서 리스트뷰와 연결
        //  현재보다 이전(추억): 기간
        //  현재보다 나중(약속): 시간
    }
}