package com.example.muhammadkhan.appointmentproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muhammadkhan.appointmentproject.R;
import com.example.muhammadkhan.appointmentproject.activities.StudentSingUp;
import com.example.muhammadkhan.appointmentproject.activities.TeachersSignUp;

public class UserKey extends AppCompatActivity {

    private EditText key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_key);
        key=(EditText) findViewById(R.id.key_id);
    }
    //this method provides layouts on the basis of provided KEY by student or teacher.
    public  void signUp(View view)
    {
        Intent intent;
        //Create the bundle
        Bundle bundle = new Bundle();
        //getting key from the editText
        String keyString=key.getText().toString();

        if(TextUtils.isEmpty(keyString))
        {
            Toast.makeText(this,"Please enter the key",Toast.LENGTH_SHORT).show();
            return;
        }

        if(keyString.equals("teacher")) {
            intent = new Intent(this, TeachersSignUp.class);
            //putting the key for in the bundle for next activity
            intent.putExtra("Teacher_key",key.getText().toString());
            startActivity(intent);
        }
        else if(keyString.equals("student"))
        {
            intent = new Intent (this, StudentSingUp.class);
            //putting the key for in the bundle for next activity
            intent.putExtra("Student_key",key.getText().toString());
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this,"Wrong key",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
