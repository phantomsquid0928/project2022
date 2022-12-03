package com.squid0928.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.squid0928.project.utils.InputData;
import com.squid0928.project.utils.Locations;
import com.squid0928.project.utils.UserAccount;
import com.squid0928.project.utils.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/* 깡통용 메인 -> 로그인 화면 설계후 대체예정
   디버그 할라면 버튼 추가후 intent로 자기 activity 호출후 view 제대로 뜨나 보면 됩니다
   services, utils -> db연결 기능 백그라운드 기능 등은 나중에 여기 몰아넣겠습니다
   바깥에 activity들 막 만드시면 됩니다
   */

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef2;
    private FirebaseFirestore db;

    String strEmail = "phantomsquid0928@gmail.com";  //TODO get from preference
    String strPwd = "12345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //Intent intent = getIntent();
        /*if (intent.getBundleExtra("userInfo") != null) {
            strEmail = intent.getBundleExtra("userInfo").getString("useruid");
            strPwd = intent.getBundleExtra("userInfo").getString("userpass");
        }*/
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //String strEmail = pref.getString("id", "<><>");
        //String strPwd = pref.getString("pass", "<><>");


        db = FirebaseFirestore.getInstance();
        db.collection("userdata").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        Log.i("ff", snapshot.getId() + "->" + snapshot.getData());

                        UserData userData = snapshot.toObject(UserData.class);

                        MapsActivity.user_data.put(snapshot.getId(), userData);
                    }
                }
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                System.out.println("login buttons");
                // 로그인이 성공적이면
                if(task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    Log.i("ff", mFirebaseAuth.getCurrentUser().getUid());
                    bundle.putString("useruid", mFirebaseAuth.getCurrentUser().getEmail());
                    intent.putExtra("userInfo", bundle);
                    startActivity(intent);
                    finish();

                } else // 로그인 실패
                {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //TODO id token exists true -> mapsActivity false -> loginActivity
        /*
        Button button = findViewById(R.id.mapOpen);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class); // debug
                Bundle bundle = new Bundle();
                bundle.putString("useruid", mFirebaseAuth.getCurrentUser().getEmail());
                intent.putExtra("userInfo", bundle);
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

         */
    }
}