package com.squid0928.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squid0928.project.utils.UserAccount;
import com.squid0928.project.utils.UserData;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // firebase 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private FirebaseFirestore db;
    private EditText mEtEmail, mEtPwd, mEtName; // 회원 가입 입력 필드
    private Button mBtnRegister; // 회원 가입 입력 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        // 실시간 데이터베이스 설정
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("firebaselogintest");
        db = FirebaseFirestore.getInstance();


        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtName = findViewById(R.id.et_name);

        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 회원 가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strName = mEtName.getText().toString();

                // FireBase Auth 인증
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
                {
                    // 자동 완성이 완료되었을 때
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            UserData userdata = new UserData(firebaseUser.getUid(), account);
                            //MapsActivity.user_data.put(firebaseUser.getUid(), userdata);
                            db.collection("userdata").document(firebaseUser.getEmail()).set(userdata);

                            // 성공했을 때에만 객체에 자동으로 생성되는 UID 값( idToken )
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setName(strName);
                            account.setColor("8B008B");
                            account.setPassword(strPwd);

                            //UserData data = new UserData(firebaseUser.getEmail(), account);
                            //userdata.put(firebaseUser.getEmail(), data);

                            // setValue : database에 insert 삽입하는 행위
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                            db.collection("userdata").document(firebaseUser.getEmail()).set(userdata);

                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_LONG).show();
                            finish();
                        } else { // 실패
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
       });
    }
}