package com.squid0928.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
/* 깡통용 메인 -> 로그인 화면 설계후 대체예정
   디버그 할라면 버튼 추가후 intent로 자기 activity 호출후 view 제대로 뜨나 보면 됩니다
   services, utils -> db연결 기능 백그라운드 기능 등은 나중에 여기 몰아넣겠습니다
   바깥에 activity들 막 만드시면 됩니다
   */

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.mapOpen);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputTemplate.class); // debug
                startActivity(intent);
            }
        });

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(MainActivity.this, "로그아웃합니다.", Toast.LENGTH_LONG).show();
                // 로그인 아웃
                if (mFirebaseAuth != null) {
                    mFirebaseAuth.signOut();
                }

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}