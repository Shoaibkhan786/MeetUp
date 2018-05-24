package com.example.muhammadkhan.appointmentproject.DataModel;

import java.io.Serializable;

/**
 * Created by Muhammad Khan on 25/10/2017.
 */

public class Users implements Serializable{

    // teacher,student attributes
    private String name;
    private String imageUri;
    // for student
    private String year;
    private String major;

    private String key;
    private String uid;

    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    //constructor for teacher
    public Users(String teacherName,String teacher_key,String imageUri,String uid) {
        this.name=teacherName;
        this.key=teacher_key;
        this.imageUri=imageUri;
        this.uid=uid;
    }
    //constructor for student
    public Users(String studentName,String year,String major, String imageStudent,String student_key,String uid) {
        this.name=studentName;
        this.year=year;
        this.major=major;
        this.key=student_key;
        this.imageUri=imageStudent;
        this.uid=uid;
    }

    public String getName() {return name;}
    public String getImageUri(){return imageUri;};
    public String getKey()
    {
        return key;
    }
    public String getYear()
    {
        return year;
    }
    public String getMajor()
    {
        return major;
    }
    public String getUid(){return uid;}
}
