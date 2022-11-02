package com.example.project.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class UserData {
    private HashMap<String, Locations> savedLocations = new HashMap<>();
    private String name;              //니이름
    private List<String> friends;     //칭구
    private List<String> blacklist;  //차단한 유저?

    public UserData (String name) {
        this.name = name;
    }
    public HashMap<String, Locations> getSavedLocations() {
        return savedLocations;
    }
    public List<Locations> getMatchHistories(UserData friend) {  //추억 약속 겹치는거 전부 겟, 열라오래걸릴거같은데... uuid 비교로는 해결가능
        HashMap<String, Locations> temp = friend.getSavedLocations();

        List<Locations> ret = new ArrayList<>();
        Set<String> keys = temp.keySet();
        Set<String> keys2 = savedLocations.keySet();
        for (String key : keys) {
            Locations target = temp.get(key);
            for (String key2 : keys2) {
                Locations target2 = savedLocations.get(key2);
                if (target.compareTo(target2) != 0) continue;
                ret.add(target);
            }
        }
        if (ret.isEmpty()) return null;
        return ret;
    }
    public List<String> getFriends() {
        return friends;
    }
    public boolean addFriends(UserData friend) {
        return false;
    }

}
