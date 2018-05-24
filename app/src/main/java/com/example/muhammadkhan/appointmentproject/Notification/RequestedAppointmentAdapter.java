package com.example.muhammadkhan.appointmentproject.Notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muhammadkhan.appointmentproject.DataModel.Student;
import com.example.muhammadkhan.appointmentproject.DataModel.Teacher;
import com.example.muhammadkhan.appointmentproject.R;
import com.example.muhammadkhan.appointmentproject.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Muhammad Khan on 16/12/2017.
 */

public class    RequestedAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEACHER = 1, TYPE_STUDENT = 2;
    private LayoutInflater inflater=null;
    DatabaseReference reference;
    Context context;
    List<Object> list=new ArrayList<Object>();
    public RequestedAppointmentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }
    public void addData(List<Object> data) {
        this.list=data;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Teacher) {
            return TYPE_TEACHER;
        } else if (list.get(position) instanceof Student) {
            return TYPE_STUDENT;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_TEACHER:
                View view = inflater
                        .from(parent.getContext())
                        .inflate(R.layout.request_teacher_attributes, parent, false);
                viewHolder = new TeacherViewHolder(view);
                break;
            case TYPE_STUDENT:
                View view1 = inflater
                        .from(parent.getContext())
                        .inflate(R.layout.student_request, parent, false);
                viewHolder = new StudentViewHolder(view1);
                break;
            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case TYPE_TEACHER:
                Teacher teacher = (Teacher) list.get(position);
                ((TeacherViewHolder) holder).showDetails(teacher);
                break;
            case TYPE_STUDENT:
                Student student = (Student) list.get(position);
                ((StudentViewHolder) holder).showDetails(student);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TeacherViewHolder extends RecyclerView.ViewHolder {
        private TextView teacherName;
        private TextView teacherTime;
        private TextView teacherDate;
        private TextView teacherDay;
        private TextView teacherStatus;
        private CircleImageView image;
        private Button accept;
        private Button reject;

        public TeacherViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            teacherName = (TextView) itemView.findViewById(R.id.teacher_name);
            teacherTime = (TextView) itemView.findViewById(R.id.teacher_time);
            teacherDay = (TextView) itemView.findViewById(R.id.teacher_day);
            teacherDate = (TextView) itemView.findViewById(R.id.teacher_date);
            image = (CircleImageView) itemView.findViewById(R.id.teacher_image);
            teacherStatus = (TextView) itemView.findViewById(R.id.status);
            accept = (Button) itemView.findViewById(R.id.accept_teacher);
            reject = (Button) itemView.findViewById(R.id.reject_teacher);
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    Teacher teacher= (Teacher) list.get(position);
                    teacher.setStatus("Rejected");
                    String uid=teacher.getUid();
                    reference= FirebaseDatabase.getInstance().getReference("Pending");
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uid).setValue(teacher);
                    reference.child(uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("Rejected");

                    //delete the notification
                    deleteNotification(position);
                    new SendNotification().execute(uid,"Appointment has been Rejected");
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    Teacher teacher= (Teacher) list.get(position);
                    teacher.setStatus("Accepted");
                    String uid=teacher.getUid();
                    reference= FirebaseDatabase.getInstance().getReference("Pending");
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uid).setValue(teacher);
                    reference.child(uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("Accepted");

                    //delete the notification
                    deleteNotification(position);
                    new SendNotification().execute(uid,"Appointment has been Accepted");
                }
            });
        }

        public void showDetails(Teacher teacher) {
            // Attach values for each item
            teacherName.setText(teacher.getAppointmentWith());
            teacherDay.setText(teacher.getDay());
            teacherDate.setText(teacher.getDate());
            teacherStatus.setText(teacher.getStatus());
            teacherStatus.setTextColor((Color.parseColor("#006400")));
            teacherTime.setText(teacher.getTime());
            if (teacher.getImageUri().equals("no image")) {
                image.setImageResource(R.drawable.profile);
            } else {
                Uri uri = Uri.parse(teacher.getImageUri());
                Picasso.with(context).load(uri).into(image);
            }
        }
    }
    // Reference to the views for each call items to display desired information
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView time;
        private TextView day;
        private TextView date;
        private TextView major;
        private TextView year;
        private ImageView image;
        private TextView status;
        private Button accept;
        private Button reject;

        public StudentViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            name = (TextView) itemView.findViewById(R.id.student_name);
            time = (TextView) itemView.findViewById(R.id.time);
            day = (TextView) itemView.findViewById(R.id.day);
            date = (TextView) itemView.findViewById(R.id.date);
            major = (TextView) itemView.findViewById(R.id.major);
            year = (TextView) itemView.findViewById(R.id.year);
            image = (ImageView) itemView.findViewById(R.id.student_image);
            status = (TextView) itemView.findViewById(R.id.status);
            accept = (Button) itemView.findViewById(R.id.accept);
            reject = (Button) itemView.findViewById(R.id.reject);
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    Student student= (Student) list.get(position);
                    student.setStatus("Rejected");
                    String uid=student.getUid();
                    reference= FirebaseDatabase.getInstance().getReference("Pending");
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uid).setValue(student);
                    reference.child(uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("Rejected");
                    //delete the notification
                    deleteNotification(position);
                    new SendNotification().execute(uid,"Appointment has been Rejected");
                }
            });
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    Student student= (Student) list.get(position);
                    student.setStatus("Accepted");
                    String uid=student.getUid();
                    reference= FirebaseDatabase.getInstance().getReference("Pending");
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(uid).setValue(student);
                    reference.child(uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("Accepted");
                    //delete the notification
                    deleteNotification(position);
                    new SendNotification().execute(uid,"Appointment has been Accepted");
                }
            });
        }

        public void showDetails(Student student) {
            // Attach values for each item
            name.setText(student.getAppointmentWith());
            day.setText(student.getDay());
            date.setText(student.getDate());
            status.setText(student.getStatus());
            status.setTextColor((Color.parseColor("#006400")));
            time.setText(student.getTime());
            major.setText(student.getMajor());
            year.setText(student.getYear());
            if (student.getImageUri().equals("no image")) {
                image.setImageResource(R.drawable.profile);
            } else {
                Uri uri = Uri.parse(student.getImageUri());
                Picasso.with(context).load(uri).into(image);
            }
        }
    }
    public void deleteNotification(int position){
        reference= FirebaseDatabase.getInstance().getReference("Notification");
        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        list.remove(position);
        notifyItemChanged(position);
    }

    //Inner Async Class for sending Appointments notification
    public class SendNotification extends AsyncTask<String,Void,Void> {

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