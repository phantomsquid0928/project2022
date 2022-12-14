package com.squid0928.project.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Locations /*implements Comparable<Locations>*/{
    private String name;      //약속이름잇겟지머
    private double latitude;
    private double longtitude;
    private String locName; //위치이름
   // private double timeStart, timeEnd;//시작시간 끝시간     //todo FFFF
    private int type;         //추억? 약속?
    //private String[] args;    //메모                       //todo ffff
    //private List<UserData> targetFriends = new ArrayList<>(); //todo ffff
    //private UUID locUUID;     //같은 약속, 추억 -> 같은 uuid    //todo ffff

    public Locations() {  //need for db

    }
    public Locations(String name, double locx, double locy, String locName, int type) {
        this.name = name;
        this.latitude = locx;
        this.longtitude = locy;
        this.locName = locName;
        //this.timeStart = timeStart;
        //this.timeEnd = timeEnd;
        this.type = type;
        //this.args = args;
        //this.locUUID = UUID.randomUUID();
    }
    /*public Locations(String name, double locx, double locy, String[] locName, int type) {   //메모 없이 걍 추가
        this.name = name;
        this.latLng = new LatLng(locx, locy);
        this.locName = locName;
        //this.timeStart = timeStart;
        //this.timeEnd = timeEnd;
        this.type = type;
        //this.locUUID = UUID.randomUUID();
    }*/
    public Locations(String name, LatLng latLng, String locName, int type) {   //메모 없이 걍 추가
        this.name = name;
        this.latitude = latLng.latitude;
        this.longtitude = latLng.longitude;
        this.locName = locName;
        //this.timeStart = timeStart;
        //this.timeEnd = timeEnd;
        this.type = type;
        //this.locUUID = UUID.randomUUID();
    }
    public Locations(Locations locations, UUID locUUID) { //친구와 약속 공유시
        this.name = locations.getName();
        this.longtitude = locations.longtitude;
        this.locName = locations.getLocName();
        //this.timeStart = locations.getTimeStart();
        //this.timeEnd = locations.getTimeEnd();
        this.type = locations.getType();
        //this.targetFriends = locations.getTargetFriends();
        //this.locUUID = locUUID;
       // locations.setLocUUID(locUUID);
    }
    public void setName(String name) {   //약속, 추억 이름 바꾸기
        this.name = name;
    }
    public void setLatLng(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }
    /*public void setArgs(String[] args) {   //메모 추가시 이거로
        this.args = args;
    }
    public void setLocUUID(UUID locUUID) {
        this.locUUID = locUUID;
    }

    public void addTargetFriend(UserData[] friends) {    //약속, 추억을 나눈 친구 한꺼번에
        for(UserData friend : friends) {
            targetFriends.add(friend);
        }
    }
    public void addTargetFriend(UserData friend) {   //약속, 추억을 나눈 친구추가
        targetFriends.add(friend);
    }
    public boolean removeTargetFriend(UserData[] friends) {   //약속, 추억을 나눈 친구 삭제
        boolean res = true;
        for(UserData friend : friends) {
            boolean taskres = targetFriends.remove(friend);
            if (taskres == false) {                             //후처리 필요시 추가
                res = false;
                continue;
            }
        }
        return res;
    }
    public boolean removeTargetFriend(UserData friend) {   //약속, 추억을 나눈 친구 삭제
        boolean res = targetFriends.remove(friend);
        return res;
    }
    @Override
    public int compareTo(Locations locations) {
        return locUUID.compareTo(locations.locUUID);
    }*/
    /*public boolean isEqual(Locations locations) {          //둘이 같은 약속임? 아님 추억임? 아 uuid 만 비교하고 싶다가 목표
        if (locations.getLocUUID() == this.locUUID) return true;
        int cnt = 0;
        String target = locations.getName();
        double tempx = locations.getLocx();
        double tempy = locations.getLocy();
        double targetTimeStart = locations.getTimeStart();
        double targetTimeEnd = locations.getTimeEnd();
        String[] targetLocName = locations.getLocName();
        if (this.locx - tempx <= 0.001 && this.locy - tempy <= 0.001) {
            cnt++;
        }
        if (target.equals(this.name)) {
            cnt++;
        }
        if (this.timeStart - targetTimeEnd <= 0.1 && this.timeEnd - targetTimeEnd <= 0.1) {
            cnt++;
        }
        if (cnt == 3) return true;
        return false;

    }*/

    //이 클래스의 get애들

    public String getName() {return name;}
    //public LatLng getLatLng() {return new LatLng(latitude, longtitude);}
    public double getLatitude() {return latitude;}
    public double getLongtitude() {return longtitude;}
    //public double getTimeStart() {return timeStart;}
    //public double getTimeEnd() {return timeEnd;}
    public String getLocName() {return locName;}
    public int getType() {return type;}
    //public String[] getArgs() {return args;}
    //public List<UserData> getTargetFriends() {return targetFriends;}
    //public UUID getLocUUID() {return locUUID;}
}
