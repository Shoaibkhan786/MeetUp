package com.example.muhammadkhan.appointmentproject.DataModel;

import java.io.Serializable;
/**
 * Created by Muhammad Khan on 16/12/2017.
 */
public class Teacher implements Serializable{
    private String time;
    private String date;
    private String day;
    private String appointmentWith;
    private String imageUri;
    private String status;
    private String uid;

    public Teacher() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Teacher(String time,String day,String date,String name,String image,String status,String uid){
        this.time=time;
        this.day=day;
        this.date=date;
        this.appointmentWith=name;
        this.imageUri=image;
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
    public String getStatus() {
        return status;
    }
    public String getUid() {return uid;}
}
