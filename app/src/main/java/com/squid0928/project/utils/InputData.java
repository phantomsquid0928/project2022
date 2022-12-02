package com.squid0928.project.utils;

import android.net.Uri;

import org.threeten.bp.*;

import java.io.Serializable;

public class InputData implements Serializable {
    private Uri photo;
    private int type;
    private int category;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private String schedule_name;
    private String memo;
    public static final int PROMISE = 1;
    public static final int MEMORY = 2;

    public InputData() {
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }
    public Uri getPhoto() {
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

    public void setDateFrom(LocalDate date) {
        this.dateFrom = date;
    }
    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }
    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }
    public LocalTime getTimeStart() {
        return timeStart;
    }

    public void setTimeEnd(LocalTime timeEnd) {
        this.timeEnd = timeEnd;
    }
    public LocalTime getTimeEnd() {
        return timeEnd;
    }

    public void setSchedule_name(String schedule_name) { this.schedule_name = schedule_name; }
    public String getSchedule_name() { return schedule_name; }

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
