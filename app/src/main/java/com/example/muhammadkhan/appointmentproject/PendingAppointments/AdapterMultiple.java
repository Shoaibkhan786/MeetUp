package com.example.muhammadkhan.appointmentproject.PendingAppointments;
import android.content.Context;
import android.net.Uri;
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
import com.example.muhammadkhan.appointmentproject.DataModel.Users;
import com.example.muhammadkhan.appointmentproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Muhammad Khan on 16/12/2017.
 */

public class AdapterMultiple extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEACHER = 1, TYPE_STUDENT = 2;
    private LayoutInflater inflater;

    Context context;
    List<Object> list=new ArrayList<Object>();


    public AdapterMultiple(Context context) {
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
                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.teacher_attributes, parent, false);
                viewHolder = new TeacherViewHolder(view);
                break;
            case TYPE_STUDENT:
                View view1 = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.student_attributes, parent, false);
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

        public TeacherViewHolder(View itemView) {
            super(itemView);
            // Initiate view
            teacherName = (TextView) itemView.findViewById(R.id.teacher_name);
            teacherTime = (TextView) itemView.findViewById(R.id.teacher_time);
            teacherDay = (TextView) itemView.findViewById(R.id.teacher_day);
            teacherDate = (TextView) itemView.findViewById(R.id.teacher_date);
            image = (CircleImageView) itemView.findViewById(R.id.teacher_image);
            teacherStatus = (TextView) itemView.findViewById(R.id.status);
        }

        public void showDetails(Teacher teacher) {
            // Attach values for each item
            teacherName.setText(teacher.getAppointmentWith());
            teacherDay.setText(teacher.getDay());
            teacherDate.setText(teacher.getDate());
            teacherStatus.setText(teacher.getStatus());
            teacherTime.setText(teacher.getTime());
            if (teacher.getImageUri().equals("no image")) {
                image.setImageResource(R.drawable.profile);
            } else {
                Uri uri = Uri.parse(teacher.getImageUri());
                Picasso.with(context).load(uri).into(image);
            }
        }
    }

    // Second ViewHolder of object type SMS
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
        }

        public void showDetails(Student student) {
            // Attach values for each item
            name.setText(student.getAppointmentWith());
            day.setText(student.getDay());
            date.setText(student.getDate());
            status.setText(student.getStatus());
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
}
