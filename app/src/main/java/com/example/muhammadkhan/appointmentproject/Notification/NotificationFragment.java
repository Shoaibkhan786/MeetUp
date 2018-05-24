package com.example.muhammadkhan.appointmentproject.Notification;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muhammadkhan.appointmentproject.DataModel.Student;
import com.example.muhammadkhan.appointmentproject.DataModel.Teacher;
import com.example.muhammadkhan.appointmentproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Khan on 17/12/2017.
 */

public class NotificationFragment extends Fragment {
    Context context;
    RecyclerView recyclerView;
    DatabaseReference reference;
    List<Object> list=new ArrayList<>();
    RequestedAppointmentAdapter adapterMultiple;
    private ProgressDialog progressDialog;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        new RequestedAppointments().execute();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.appointment_request_recycler, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.request_recycle_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        return view;
    }

    public  class RequestedAppointments extends AsyncTask<Void,List<Object>,Void> {
        public void notificationAppointments(){
            list.clear();
            reference= FirebaseDatabase.getInstance().getReference("keys").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userKey=dataSnapshot.getValue().toString();
                    if(userKey.equals("teacher")){
                        reference=FirebaseDatabase.getInstance().getReference("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Object data=child.getValue(Student.class);
                                    list.add(data);
                                    onProgressUpdate();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                    else {
                        reference = FirebaseDatabase.getInstance().getReference("Notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Object data = child.getValue(Teacher.class);
                                    list.add(data);
                                    onProgressUpdate();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //---------------------------------call back method----------------------------------------------------//
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapterMultiple=new RequestedAppointmentAdapter(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //method call for appointments notification
            notificationAppointments();
            return null;
        }
        //---------------------------------call back method----------------------------------------------------//
        @Override
        protected void onProgressUpdate(List<Object>... values) {
            super.onProgressUpdate(values);
            adapterMultiple.addData(list);
            recyclerView.setAdapter(adapterMultiple);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

}
