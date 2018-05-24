package com.example.muhammadkhan.appointmentproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.muhammadkhan.appointmentproject.DataModel.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static com.example.muhammadkhan.appointmentproject.R.*;

public class TeachersSignUp extends AppCompatActivity {
    //monitor image upload process
    UploadTask uploadTask;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView profileImage;
    private EditText email;
    private EditText password;
    private EditText name;
    private ProgressDialog progressDialog;
    private String teacherKey;
    //uri of selected image for gallery;
     Uri imageUri=null;
    //firebase authentication instance
     private FirebaseAuth auth;
    //firebase Database instance
    private DatabaseReference reference;
    //firebase storage reference
    FirebaseStorage storage;
    StorageReference storageRef,imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.teacher_signup);

        progressDialog= new ProgressDialog(this);
        email=(EditText) findViewById(id.signUp_email);
        password=(EditText) findViewById(id.signUp_password);
        name=(EditText) findViewById(id.teacher_name);
        profileImage=(ImageView) findViewById(id.profile_image);

        //getting teacher key from previous activity
        Bundle bundle = getIntent().getExtras();
        teacherKey=bundle.getString("Teacher_key");
        //profile image listener
        profileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                uploadImageFromGallery();
            }

        });
    }

//-------------------------------------------------Method for user registration-----------------------------------------------------//
    public void userRegister(View view) {
        //checking empty email or password fields
        boolean bool = checkUserInputs(name.getText().toString(),email.getText().toString(),password.getText().toString());
        //if fields are filled properly then register the user
        if (bool == true) {
            new UserCreation().execute();
        }
    }
    public void teacherUserCreation() {
        //user Registration on fireBase console
        auth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if(imageUri==null) {
                        putInDatabase(name.getText().toString().trim(),teacherKey,"no image");
                    }
                    else {
                        //upload image on Firebase Storage
                        uploadImageOnServer();
                    }
                }
                else {
                    Toast.makeText(TeachersSignUp.this, "Sorry..Your could not register", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
    //-------------------------------------------Method for uploading user picture on firebase server----------------------------------//
    private void uploadImageOnServer() {
        //image compressing
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap=profileImage.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        //image uploading
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imageRef = storageRef.child("Photos").child(imageUri.getLastPathSegment());
        uploadTask=imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageUri = taskSnapshot.getDownloadUrl();
                //method call,put all data in Firebase database
                putInDatabase(name.getText().toString().trim(), teacherKey, imageUri.toString());
            }
        }
        );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeachersSignUp.this,"Sorry could not upload the image",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }
    //----------------------------------put all information in database-------------------------------------------------------//
    private void putInDatabase(String name,String teacherKey,String imageUri) {
        String uid = auth.getCurrentUser().getUid();
        //user info Object
        Users users=new Users(name,teacherKey,imageUri,uid);
        //firebase database
        reference = FirebaseDatabase.getInstance().getReference("teacher");
        reference.child(uid).setValue(users);
       //saving the user key
        reference=FirebaseDatabase.getInstance().getReference("keys").child(uid);
        reference.setValue(new String(teacherKey));

        progressDialog.dismiss();
        Toast.makeText(TeachersSignUp.this,"Welcome Sir",Toast.LENGTH_SHORT).show();
        //method call for showing user main screen activity
         mainActivityIntent();
    }
//--------------------------------------Method for user user input fields-----------------------------------------------------//
    private boolean checkUserInputs(String name,String email,String password) {
        if(TextUtils.isEmpty(name)){
            //showing  empty email message
            Toast.makeText(this,"Please enter name",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(email)){
            //showing  empty email message
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(password)){
            //showing empty password message
            Toast.makeText(this,"Please enter the passord",Toast.LENGTH_SHORT).show();
            return false;
        }
       return true;
    }
    //----------------------------------------method for uploading student image from the gallery--------------------------------------//
    public void uploadImageFromGallery() {
        //image gallery intent
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }
    //-------------------------------------------------call back Method--------------------------------------------------------//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode== RESULT_OK && data!=null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }
    //-------------------------------------main activity intent--------------------------------------------------//
    public void mainActivityIntent(){
        Intent intent=new Intent(TeachersSignUp.this,MainActivity.class);
        startActivity(intent);
    }
    //------------------inner class for doing all user creation in background thread------------------------------//
    public class UserCreation extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //getting auth reference
            auth=FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog(TeachersSignUp.this);
            progressDialog.setMessage("User registration....");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            //method call
            teacherUserCreation();
            return null;
        }
    }
}
