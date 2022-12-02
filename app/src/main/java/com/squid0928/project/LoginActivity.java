package com.squid0928.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squid0928.project.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // firebase 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; // 회원 가입 입력 필드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 요청하는 버튼
        mFirebaseAuth = FirebaseAuth.getInstance();
        // 실시간 데이터베이스 설정
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("firebaselogintest");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);

        // 로그인
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if(strEmail.length() == 0 || strPwd.length() == 0)
                {
                    Toast.makeText(LoginActivity.this, "이메일 혹은 비밀번호를 입력하지 않았습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        System.out.println("login buttons");
                        // 로그인이 성공적이면
                        if(task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            Log.i("ff", mFirebaseAuth.getCurrentUser().getUid());
                            bundle.putString("useruid", mFirebaseAuth.getCurrentUser().getEmail());
                            bundle.putString("userpass", strPwd);
                            intent.putExtra("userInfo", bundle);
                            startActivity(intent);
                            finish(); // 현재 액티비티 파괴
                        } else // 로그인 실패
                        {
                            Toast.makeText(LoginActivity.this, "로그인 실패하였습니다!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        // 회원가입 버튼 생성
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원 가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}