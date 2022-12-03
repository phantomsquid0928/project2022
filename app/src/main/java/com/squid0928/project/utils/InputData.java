package com.squid0928.project.utils;

import android.net.Uri;

import org.threeten.bp.*;

import java.io.Serializable;

public class InputData implements Serializable {
    private String photo;
    private int type;
    private int category;
    private String dateFrom;
    private String dateTo;
    private String timeStart;
    private String timeEnd;
    private String scheduleName;
    private String memo;
    public static final int PROMISE = 1;
    public static final int MEMORY = 2;

    public InputData() {
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPhoto() {
        return photo;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    public void setCategory(int category) {
        this.category = category;
    }
    public int getCategory() {
        return category;
    }

    public void setDateFrom(String date) {
        this.dateFrom = date;
    }
    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
    public String getDateTo() {
        return dateTo;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }
    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
    public String getTimeEnd() {
        return timeEnd;
    }

    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }
    public String getScheduleName() { return scheduleName; }

    public void setMemo(String memo) {
        this.memo = memo;
    }
    public String getMemo() { return memo; }

    public boolean isEmpty() {
        boolean checker = false;
        if (getTimeEnd() == null) checker = true;
        if (getTimeStart() == null) checker = true;
        if (getDateTo() == null) checker = true;
        if (getDateFrom() == null) checker = true;
        return checker;
    }
}
