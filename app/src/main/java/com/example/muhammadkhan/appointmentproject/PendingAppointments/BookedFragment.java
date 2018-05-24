package com.example.muhammadkhan.appointmentproject.PendingAppointments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.muhammadkhan.appointmentproject.DataModel.Users;
import com.example.muhammadkhan.appointmentproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Muhammad Khan on 05/11/2017.
 */

public class BookedFragment extends Fragment {
    DatabaseReference firebaseDatabase;
    private RecyclerView recyclerView;
    Context context;
    AdapterMultiple adapterMultiple;
    private ProgressDialog progressDialog;
    private static List<Object> list=new ArrayList<Object>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        new BookedAppointments().execute();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.pending_recycler, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycle_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        return view;
    }

    public  class BookedAppointments extends AsyncTask<Void,List<Object>,Void> {
        public void pendingAppointment(){
           list.clear();
            firebaseDatabase=FirebaseDatabase.getInstance().getReference("keys").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            firebaseDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userKey=dataSnapshot.getValue().toString();
                    if(userKey.equals("teacher")){
                        firebaseDatabase=FirebaseDatabase.getInstance().getReference("Pending").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        firebaseDatabase.addValueEventListener(new ValueEventListener() {
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
                        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Pending").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        firebaseDatabase.addValueEventListener(new ValueEventListener() {
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
        @Override
        protected Void doInBackground(Void... voids) {
            //method call for user based search
            pendingAppointment();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapterMultiple=new AdapterMultiple(context);
        }

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