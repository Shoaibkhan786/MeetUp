package com.example.muhammadkhan.appointmentproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.muhammadkhan.appointmentproject.DataModel.Student;
import com.example.muhammadkhan.appointmentproject.DataModel.Teacher;
import com.example.muhammadkhan.appointmentproject.R;
import com.example.muhammadkhan.appointmentproject.DataModel.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppointmentBooking extends AppCompatActivity {
    DatabaseReference reference;
    DatePicker datePicker;
    TimePicker timePicker;
    Toolbar toolbar;
    String key;
    String name;
    String uid;
    String major;
    String studentYear;
    String image;

    String day = null;
    String userDate;
    String userTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_booking);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        datePicker = (DatePicker) findViewById(R.id.dp_datepicker);
        timePicker = (TimePicker) findViewById(R.id.tp_timepicker);
        //setting backward arrow icons on toolbar
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //getting data from previous activity
        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("key");
        if (key.equals("student")) {
            name = bundle.getString("userName");
            image = bundle.getString("imageUri");
            uid = bundle.getString("userId");
            major = bundle.getString("major");
            studentYear = bundle.getString("year");
        } else {
            name = bundle.getString("userName");
            image = bundle.getString("imageUri");
            uid = bundle.getString("userId");
        }
        //setting name of the user on toolbar
        toolbar.setTitle(name);
    }

    public boolean getTimeAndDate() {
        int month = datePicker.getMonth() + 1;
        int dayOfMonth = datePicker.getDayOfMonth();
        int year = datePicker.getYear();
        int getHour;
        int getMinute;
        SimpleDateFormat dateFormate;
        SimpleDateFormat timeFormate;
        Date dt1 = null;

        userDate = ""+dayOfMonth+"/"+month+"/"+year;
        if (Build.VERSION.SDK_INT < 23) {
            getHour = timePicker.getCurrentHour();
            getMinute = timePicker.getCurrentMinute();
        } else {
            getHour = timePicker.getHour();
            getMinute = timePicker.getMinute();
        }
        userTime = ""+getHour+":"+getMinute;
        try {
            dateFormate = new SimpleDateFormat("dd/MM/yyyy");
            dt1 = dateFormate.parse(userDate);
            dateFormate = new SimpleDateFormat("EEEE");
            day = dateFormate.format(dt1);
            dateFormate=new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = dateFormate.format(new Date());
            if(dateFormate.parse(userDate).before(dateFormate.parse(currentDate))){
                return false;
            }
            timeFormate=new SimpleDateFormat("HH:mm");
            dateFormate=new SimpleDateFormat("dd/MM/yyyy");
            String currentTime=timeFormate.format(new Date());
            if(dateFormate.parse(userDate).equals(dateFormate.parse(currentDate))&& timeFormate.parse(userTime).before(timeFormate.parse(currentTime))){
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
    //--------------------------------------Method for Appointment Booking------------------------------------------//
    public void booking(View view) {
        Boolean bool=getTimeAndDate();
        if(bool==true){
            findLoggedUser(key);
            saveDateInFirebase();
            new SendNotification().execute(uid,"You have appointment Notification");
        }else {
            Toast.makeText(this,"Choose the valid time and date",Toast.LENGTH_SHORT).show();
        }
    }

    public void saveDateInFirebase(){
        SharedPreferences perf=getSharedPreferences("info",MODE_PRIVATE);
        if(key.equals("student")){
            //Pending info
            Student data=new Student(userTime,userDate,day,name,major,studentYear,image,"Pending",
                    FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference= FirebaseDatabase.getInstance().getReference("Pending");
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uid).setValue(data);
            //Notification  info
            Teacher data1=new Teacher(userTime,day,userDate,perf.getString("teacher_name",""),
                    perf.getString("teacher_image",""),"Pending",FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference=FirebaseDatabase.getInstance().getReference("Notification");
            reference.child(uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data1);
        }
        else {
            Teacher info=new Teacher(userTime,day,userDate,name,image,"Pending",FirebaseAuth.getInstance().getCurrentUser().getUid());
            //Pending info
            reference= FirebaseDatabase.getInstance().getReference("Pending");
            reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uid).setValue(info);
            //Booking info
            Student info1=new Student(userTime,userDate,day,perf.getString("student_name",""),
                    perf.getString("student_major",""),perf.getString("student_year",""),
                    perf.getString("student_image",""),"Pending",FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference=FirebaseDatabase.getInstance().getReference("Notification");
            reference.child(uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(info1);
        }
        Toast.makeText(AppointmentBooking.this, "Request has been made..!", Toast.LENGTH_SHORT).show();
        //go to main activity
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void findLoggedUser(String key){
        if(key.equals("student")){
            key="teacher";
        }
        else {
            key="student";
        }
        reference = FirebaseDatabase.getInstance().getReference(key).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                if(users.getKey().equals("student")){
                    editor.putString("student_name",users.getName());
                    editor.putString("student_image",users.getImageUri());
                    editor.putString("student_year",users.getYear());
                    editor.putString("student_major",users.getMajor());
                    editor.putString("key",users.getKey());
                    editor.commit();
                }
                else {
                    editor.putString("teacher_name",users.getName());
                    editor.putString("teacher_image",users.getImageUri());
                    editor.putString("key",users.getKey());
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Inner Async Class for sending Appointments notification
    public class SendNotification extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            String userID=params[0];
            String message=params[1];
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("uid",userID)
                        .add("message",message)
                        .build();
                Request request = new okhttp3.Request.Builder()
                        .url("http://172.18.0.25/push.php")
                        .post(body)
                        .build();
                 Response response = client.newCall(request).execute();
                Log.i("status", "doInBackground: "+response.message());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}