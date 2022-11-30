package com.squid0928.project.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 사용자 계정 정보 모델 클래스
 *
 */
public class UserAccount
{

    private String idToken; // firebase UID : 고유 토큰정보
    private String emailId; // 이메일 아이디
    private String password; // 비밀번호
    private String name; // 이름
    private String color; // 색상
    private DatabaseReference databaseReference;

    // constructor : Alt + Insert
    public UserAccount() {
        setColor("#8B008B");
    }

    // getter and setter : Alt + Insert
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
