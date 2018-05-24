package com.example.muhammadkhan.appointmentproject.userShowFragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muhammadkhan.appointmentproject.DataModel.Users;
import com.example.muhammadkhan.appointmentproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Muhammad Khan on 05/11/2017.
 */

public class UserShowFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private static AdapterUsers adapterUsers;
    private static List<Users> list=new ArrayList<Users>();
    private String userID;
    Context context;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        new FetchUsers().execute();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.recycler_view_usershow, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.recycle);
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        return view;
    }
    public class FetchUsers extends AsyncTask<Void,List<Users>,Void> {

        //---------------------------------Users for students--------------------------------------------------//
        public void userBasedUsers() {
            list.clear();
            databaseReference = FirebaseDatabase.getInstance().getReference("keys").child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getValue().toString();
                    if (key.equals("teacher")) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("student");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //String val= dataSnapshot.getValue(String.class);
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Users user = child.getValue(Users.class);
                                    list.add(user);
                                    onProgressUpdate(list);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i("issues dectected", "onCancelled");
                                return;
                            }
                        });
                    } else {
                        databaseReference = FirebaseDatabase.getInstance().getReference("teacher");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //String val= dataSnapshot.getValue(String.class);
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Users user = child.getValue(Users.class);
                                    list.add(user);
                                    onProgressUpdate(list);
                                }
                                ;
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i("issues dectected", "onCancelled");
                                return;
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        //---------------------------------find logged user---------------------------------------------------//

        @Override
        protected Void doInBackground(Void... voids) {
            userBasedUsers();
            return null;
        }

        //---------------------------------call back method----------------------------------------------------//
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            auth = FirebaseAuth.getInstance();
            userID = auth.getCurrentUser().getUid();
            adapterUsers=new AdapterUsers(context);
        }

        @Override
        protected void onProgressUpdate(List<Users>... values) {
            super.onProgressUpdate(values);
            adapterUsers.addData(list);
            recyclerView.setAdapter(adapterUsers);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            progressDialog.dismiss();
        }
    }
    public static void search (SearchView searchView){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Users> filteredModelList = filter(list, newText);
                adapterUsers.setFilter(filteredModelList);
                return true;
            }
        });
    }

    private static List<Users> filter(List<Users> models, String query) {
        query = query.toLowerCase();
        final List<Users> filteredModelList = new ArrayList<>();
        for (Users model : models) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}