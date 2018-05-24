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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.muhammadkhan.appointmentproject.R;
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

public class StudentSingUp extends AppCompatActivity {
    //monitor image upload process
    UploadTask uploadTask;
    private static final int RESULT_LOAD_IMAGE = 1;
    //views objects
    private ImageView profileImage;
    private EditText name;
    private EditText email;
    private EditText password;
    private String studentKey;
    //years radio group
    private RadioGroup yearsGroup;
    private RadioButton year;
    private int yearGroupChild;
    //courses radio group
    private RadioGroup majorsGroup;
    private RadioButton major;
    private int majorGroupChild;
    //image uri
    Uri imageUri=null;
    private ProgressDialog progressDialog;
    //Firebase instances
    private DatabaseReference reference;
    //firebase authentication instance
    private FirebaseAuth auth;
    //firebase storage reference
    FirebaseStorage storage;
    StorageReference storageRef, imageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sing_up);
        //getting ID's of different views from the XML
        name = (EditText) findViewById(R.id.teacher_name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        yearsGroup=(RadioGroup) findViewById(R.id.year_group);
        majorsGroup=(RadioGroup) findViewById(R.id.major_group);

        //getting teacher key from previous activity
        Bundle bundle = getIntent().getExtras();
        studentKey = bundle.getString("Student_key");
        //profile image listener
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //method call from getting image from gallery
                uploadImageFromGallery();
            }
        });
    }
    //---------------------------------------method for user registration-----------------------------------------------------//
    public void registerUser(View view) {
        //method for checking users inputs
        boolean bool = checkAllUserInputs(name.getText().toString().trim(), email.getText().toString().trim(),
                password.getText().toString().toString());
        //if users fields are correctly filled
        if (bool == true) {
            new UserCreation().execute();
        }


    }
    //-------------------user creation on firebase----------------------------------------------------------------//
        public void createUserOnFirebase() {
            //getting Firebase Instance
            auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (imageUri == null) {
                            putInDatabase("no image");
                        } else {
                            //method call for image uploading
                            uploadImageOnServer();
                        }
                    } else {
                        Toast.makeText(StudentSingUp.this, "Sorry..Your could not register", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    //----------------------------------put all information in database-------------------------------------------------------//
    private void putInDatabase(String imageAddress) {
        //user info
        String uid = auth.getCurrentUser().getUid();
        Users users=new Users(name.getText().toString().trim(),year.getText().toString().trim(),
                major.getText().toString().trim(),imageAddress,studentKey,uid);
        //firebase database
        reference = FirebaseDatabase.getInstance().getReference("student");
        reference.child(uid).setValue(users);
        //saving user key
        reference= FirebaseDatabase.getInstance().getReference("keys").child(uid);
        reference.setValue(new String(studentKey));
        progressDialog.dismiss();
        Toast.makeText(StudentSingUp.this,"Welcome Student",Toast.LENGTH_SHORT).show();
        //method call for showing user main screen activity
        mainActivityIntent();
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
        // Toast.makeText(StudentSingUp.this,"lala",Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode== RESULT_OK && data!=null){
            imageUri = data.getData();
           profileImage.setImageURI(imageUri);
        }
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
        imageRef = storageRef.child("Photos/"+imageUri.getLastPathSegment());
        uploadTask=imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageUri = taskSnapshot.getDownloadUrl();
                                               //method call,put all data in Firebase database
                                                putInDatabase(imageUri.toString());
                                            }
                                        }
        );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentSingUp.this,"Sorry could not upload the image",Toast.LENGTH_LONG).show();
            }
        });
        return;
    }
    //--------------------------------------Method for user user input fields-----------------------------------------------------//1
    private boolean checkAllUserInputs(String name, String email,String password) {
        majorGroupChild =majorsGroup.getCheckedRadioButtonId();
        yearGroupChild=yearsGroup.getCheckedRadioButtonId();
        major=(RadioButton) findViewById(majorGroupChild);
        year=(RadioButton) findViewById(yearGroupChild);

        if (majorGroupChild==-1) {
            Toast.makeText(StudentSingUp.this,"choose your major..!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (yearGroupChild==-1) {
            Toast.makeText(StudentSingUp.this,"choose your course..!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(name)){
            //showing  empty email message
            Toast.makeText(this, "Name field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(email)){
            //showing  empty major message
            Toast.makeText(this, "email field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)){
            //showing  empty major message
            Toast.makeText(this, "password field is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //-------------------------------------main activity intent--------------------------------------------------//
    public void mainActivityIntent(){
        Intent intent=new Intent(StudentSingUp.this,MainActivity.class);
        startActivity(intent);
    }
    //------------------inner class for doing all user creation in background thread------------------------------//
    public class UserCreation extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(StudentSingUp.this);
            progressDialog.setMessage("User registration....");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            //method call
            createUserOnFirebase();
            return null;
        }
    }
}
