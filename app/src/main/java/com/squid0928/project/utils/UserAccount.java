package com.squid0928.project.utils;

/**
 * 사용자 계정 정보 모델 클래스
 *
 */
public class UserAccount
{

    private String idToken; // firebase UID : 고유 토큰정보
    private String emailId; // 이메일 아이디
    private String password; // 비밀번호
    //TODO profile 사진 저장용 필요

    // constructor : Alt + Insert
    public UserAccount() {

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
}
