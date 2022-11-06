package org.techtown.firebaselogintest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{
    MemoFragment memoFragment;
    FriendFragment friendFragment;
    SettingFragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memoFragment = new MemoFragment();
        friendFragment = new FriendFragment();
        settingFragment = new SettingFragment();

        // 초기 화면은 MemoFragment 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, memoFragment).commit();

        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.memo:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, memoFragment).commit();
                        return true;
                    case R.id.friend:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, friendFragment).commit();
                        return true;
                    case R.id.setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.containers, settingFragment).commit();
                }
                return false;
            }
        });

    }
}