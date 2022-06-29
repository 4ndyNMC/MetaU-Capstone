package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = "SettingsActivity";
    public static final String PHOTO_FILE_NAME = "photo.jpg";
    public static final int RESULT_LOAD_IMAGE = 42;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 52;

    Button btnGallery;
    Button btnCamera;
    ImageView ivProfilePic;

    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        ivProfilePic = findViewById(R.id.ivSettingsProfilePic);

        setUpProfilePic();

        btnGallery.setOnClickListener(btnGalleryClicked);
        btnCamera.setOnClickListener(btnCameraClicked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOAD_IMAGE:
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                        ivProfilePic.setImageBitmap(selectedImage);
                        displayImage(selectedImage);
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                    break;
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                    ivProfilePic.setImageBitmap(takenImage);
                    displayImage(takenImage);
                    break;
                default:
                    break;
            }
                Log.i(TAG, "image not selected");
        } else {
            Log.i(TAG, "something went wrong");
        }
    }

    private void displayImage(Bitmap selectedImage) {
        Glide.with(this).load(selectedImage).circleCrop().into(ivProfilePic);
    }

    private void displayImage(Drawable drawable) {
        Glide.with(this).load(drawable).circleCrop().into(ivProfilePic);
    }

    private void displayImage(String imageUrl) {
        Glide.with(this).load(imageUrl).circleCrop().into(ivProfilePic);
    }

    private void setUpProfilePic() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("imageUrl")) {
                            displayImage(snapshot.child("imageUrl").getValue(String.class));
                        } else {
                            displayImage(getResources().getDrawable(R.drawable.ic_baseline_person_24));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private final View.OnClickListener btnGalleryClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
        }
    };

    private final View.OnClickListener btnCameraClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = getPhotoFileUri(PHOTO_FILE_NAME);

            Uri fileProvider = FileProvider.getUriForFile(SettingsActivity.this,
                    "com.google.firebase.provider.FirebaseInitProvider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    };

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.i(TAG, "failed to create directory");
        }
        return new File (mediaStorageDir.getPath() + File.separator + PHOTO_FILE_NAME);
    }
}