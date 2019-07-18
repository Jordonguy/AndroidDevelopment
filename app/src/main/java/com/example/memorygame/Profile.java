package com.example.memorygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;
    Uri uriprofileimage;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private TextView textViewCurrentUser;
    private EditText editTextName;
    private Button btnSave, btnLogout;
    private ImageView imageViewUserPicture;
    private ProgressBar progressBar;
    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        textViewCurrentUser = (TextView) findViewById(R.id.textViewCurrentUser);

        editTextName = (EditText) findViewById(R.id.editTextName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        imageViewUserPicture = (ImageView) findViewById(R.id.imageViewUserPicture);

        btnSave.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        imageViewUserPicture.setOnClickListener(this);

        loadUserInformation();

        //displaying Current users email
        textViewCurrentUser.setText(user.getEmail());
    }

    private void loadUserInformation(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl().toString()).into(imageViewUserPicture);
            }
            if (user.getDisplayName() != null) {
                editTextName.setText(user.getDisplayName());
            }
        }
    }

    private void saveUserInformation(){
        String name = editTextName.getText().toString().trim();


        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(name.isEmpty()){
            editTextName.setError("Name Required");
            editTextName.requestFocus();
            return;
        }


        if(user!=null && name !=null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Profile.this, "User Information Saved", Toast.LENGTH_LONG).show();

                }
            });
        }




    }

    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a profile image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null){
            uriprofileimage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriprofileimage);
                imageViewUserPicture.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" +System.currentTimeMillis() + ".jpg");

        if(uriprofileimage != null){
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriprofileimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri downloadUrl = uriprofileimage;
                        }
                    });
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Image Uploaded!", Toast.LENGTH_SHORT).show();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "An Error Occurred!", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnLogout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnSave:
                saveUserInformation();
                break;
            case R.id.imageViewUserPicture:
                showImageChooser();
                break;
        }
    }
}
