package com.squid0928.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/* 깡통용 메인 -> 로그인 화면 설계후 대체예정
   디버그 할라면 버튼 추가후 intent로 자기 activity 호출후 view 제대로 뜨나 보면 됩니다
   services, utils -> db연결 기능 백그라운드 기능 등은 나중에 여기 몰아넣겠습니다
   바깥에 activity들 막 만드시면 됩니다
   */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.mapOpen);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }
}