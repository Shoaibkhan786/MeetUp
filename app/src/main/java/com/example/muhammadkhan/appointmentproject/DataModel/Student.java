package com.example.muhammadkhan.appointmentproject.DataModel;

import java.io.Serializable;

/**
 * Created by Muhammad Khan on 16/12/2017.
 */

public class Student implements Serializable{
    private String time;
    private String date;
    private String day;
    private String appointmentWith;
    private String imageUri;
    private String major;
    private String year;
    private String status;
    private String uid;

    public Student(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Student(String time,String date,String day,String name,String major,String year,String imageUri,String status,String uid){
        this.time=time;
        this.date=date;
        this.day=day;
        this.appointmentWith=name;
        this.major=major;
        this.year=year;
        this.imageUri=imageUri;
        this.status=status;
        this.uid=uid;
    }
    //setter
    public void setStatus(String status){
        this.status=status;
    }
    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getAppointmentWith() {
        return appointmentWith;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getMajor() {
        return major;
    }

    public String getYear() {
        return year;
    }

    public String getStatus() {return status;}
    public String getUid(){return uid;};
}
