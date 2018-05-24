package com.example.muhammadkhan.appointmentproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muhammadkhan.appointmentproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_in extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        //getting user inputs
        email = (EditText) findViewById(R.id.signInEmail);
        password = (EditText) findViewById(R.id.signPassword);
        progressDialog = new ProgressDialog(Login_in.this);
        //if user was already logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(Login_in.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
        }
    }

    //----------------------------------------User authentication method--------------------------------------//
    public void signIn(View view) {
        Boolean bool = userInputFieldsCheck();
        if (bool == true) {
            //getting firebase object
            auth = FirebaseAuth.getInstance();
            progressDialog.setMessage("Signing In....");
            progressDialog.show();
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            new UserVarification().execute(userEmail, userPassword);
        }
    }

    //---------------------------------------User input check method----------------------------------------------//
    public boolean userInputFieldsCheck() {
        //getting email and password
        String getEmail = email.getText().toString();
        String getPassword = password.getText().toString();
        //checking empty email or password fields
        if (TextUtils.isEmpty(getEmail)) {
            //showing  empty email message
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(getPassword)) {
            //showing empty password message
            Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //-----------------------------------------sign up intent-----------------------------------------------------//
    public void signUp(View view) {
        Intent intent = new Intent(this, UserKey.class);
        startActivity(intent);
    }

    //-----------------------------inner class for user verification------------------------------------------------//
    public class UserVarification extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            auth.signInWithEmailAndPassword(strings[0],
                    strings[1])
                    .addOnCompleteListener(Login_in.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(Login_in.this, "Done", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login_in.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Login_in.this, "Check your email and password", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
            return null;
        }
    }

}