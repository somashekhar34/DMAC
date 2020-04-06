package com.google.dmac;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;



public class profile extends AppCompatActivity {


    ImageView ImgUserPhoto;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    String password,name,f;
    private AutoCompleteTextView userPassword, userName;
    private ProgressBar loadingProgress;
    private Button regBtn;
    TextView t;
    private FirebaseAuth mAuth;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            ImgUserPhoto.setImageURI(pickedImgUri);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //ini views: password is write something!
        ImgUserPhoto=(ImageView)findViewById(R.id.regUserPhoto);
        userPassword = (AutoCompleteTextView)findViewById(R.id.atvEmailReg);
        userName = (AutoCompleteTextView)findViewById(R.id.atvUsernameReg);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            userName.setText(savedInstanceState.getString("name1"));
            userPassword.setText(savedInstanceState.getString("status"));
        } else {
            // Probably initialize members with default values for a new instance
            name= "";
            password="";
        }


        mAuth = FirebaseAuth.getInstance();


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingProgress.setVisibility(View.VISIBLE);
                password = userPassword.getText().toString().trim();
                name = userName.getText().toString().trim();
                CreateUserAccount(name, password);

            }
        });

        ImgUserPhoto = findViewById(R.id.regUserPhoto);

        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }

            }
        });


    }

    private void CreateUserAccount(final String name, String password) {
        updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser(), password);
    }

    // update user photo and name
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser, String passedpassword) {

        // first we need to upload user photo to firebase storage and get url

        StorageReference mStorage = FirebaseStorage.getInstance("gs://dmac-b92af.appspot.com/").getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child("pickedImgUri.getLastPathSegment()");
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // image uploaded succesfully
                // now we can get our image url
                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        f = uri.toString();
                        // uri contain use
                        // r image url

                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                 .setPhotoUri(uri)
                                .build();


                        currentUser.updateProfile(profleUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            // user info updated successfully
                                            loadurl(f);
                                            Intent i2 =new Intent(profile.this,Account.class);
                                            Toast.makeText(profile.this,"Successfully Updated!",Toast.LENGTH_LONG).show();
                                            startActivity(i2);
                                        }
                                        if(!task.isSuccessful()){
                                            Toast.makeText(profile.this,"Failed to Upload!",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }

                    private void loadurl(String f) {
                        Picasso.with(profile.this).load(f).into(ImgUserPhoto);
                    }
                });
            }
        });
    }


    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(profile.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(profile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else
            openGallery();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name1",name);
        outState.putString("status",password);

        // call superclass to save any view hierarchy

    }

   @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        userName.setText(savedInstanceState.getString("name1"));
        userPassword.setText(savedInstanceState.getString("status"));
    }


}

