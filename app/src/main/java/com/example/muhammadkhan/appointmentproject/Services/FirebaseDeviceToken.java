package com.example.muhammadkhan.appointmentproject.Services;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

/**
 * Created by Muhammad Khan on 10/12/2017.
 */

public class FirebaseDeviceToken extends FirebaseInstanceIdService {
    private String recentToken=null;
    private  String uid;
    @Override
    public void onTokenRefresh() {
        recentToken= FirebaseInstanceId.getInstance().getToken();
    }
}
