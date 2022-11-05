package com.example.project;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.net.URI;

public class InputTemplate extends AppCompatActivity {

    Dialog dialog_stayed_time;
    ImageView photo;
    RadioGroup memory_or_promise;
    RadioButton memory;
    RadioButton promise;
    Spinner category;
    CheckBox check_stayed_time;
    TextView stayed_time;
    EditText memo;
    Button accept_Btn;
    DatePicker stayed_date;
    TimePicker stayed_time_from;
    TimePicker stayed_time_to;
    String concat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_template);

        //  View 객체 획득
        photo = findViewById(R.id.photo);
        memory_or_promise = findViewById(R.id.memory_or_promise);
        memory = findViewById(R.id.memory);
        promise = findViewById(R.id.promise);
        category = findViewById(R.id.category);
        check_stayed_time = findViewById(R.id.check_stayed_time);
        stayed_time = findViewById(R.id.stayed_time);
        memo = findViewById(R.id.memo);
        dialog_stayed_time = new Dialog(this);
        dialog_stayed_time.setContentView(R.layout.stayed_time);
        accept_Btn = dialog_stayed_time.findViewById(R.id.acceptBtn);
        stayed_date = dialog_stayed_time.findViewById(R.id.stayed_date);
        stayed_time_from = dialog_stayed_time.findViewById(R.id.stayed_time_from);
        stayed_time_to = dialog_stayed_time.findViewById(R.id.stayed_time_to);


        //  이미지뷰를 클릭했을 때 비어있는 지 체크
        photo.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photo.getDrawable() == null) {
                    //  이미지뷰에 아무 것도 없을 때(갤러리로 추가, 카메라로 추가)
                } else {
                    //  이미지뷰에 사진이 이미 있을 때(갤러리에서 새 사진 가져오기, 사진 삭제하기)
                }
            }
        });

        //  추억 or 약속 어떤 것인지 체크
        memory_or_promise.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.memory:
                        //  사용자의 추억 범주 불러오기 (SharedPreference)
                        break;
                    case R.id.promise:
                        //  사용자의 약속 범주 불러오기 (SharedPreference)
                        break;
                }
            }
        });

        //  머문 시간을 기록할 것인지 체크박스를 통해 묻고
        //  기록한다 하면 다이얼로그를 띄워 저장
        //  이후 TimeTable 만들 때 이 데이터를 사용하면 될 듯 합니다.
        check_stayed_time.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_stayed_time.isChecked()) {
                    if(!dialog_stayed_time.isShowing())
                        dialog_stayed_time.show();
                    accept_Btn.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            concat=concat.concat(stayed_date.getYear() + "." + stayed_date.getMonth() + "." + stayed_date.getDayOfMonth() + " ");
                            concat=concat.concat(stayed_time_from.getHour() + ":" + stayed_time_from.getMinute() + " ~ ");
                            concat=concat.concat(stayed_time_to.getHour() + ":" + stayed_time_to.getMinute());
                            stayed_time.setText(concat);
                            concat="";
                            if(dialog_stayed_time.isShowing())
                                dialog_stayed_time.dismiss();
                        }
                    });
                    stayed_time.setVisibility(View.VISIBLE);
                } else {
                    stayed_time.setText("");
                    stayed_time.setVisibility(View.GONE);
                }
            }
        });
    }
}