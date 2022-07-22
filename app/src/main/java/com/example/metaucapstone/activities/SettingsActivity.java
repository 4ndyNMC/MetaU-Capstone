package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.metaucapstone.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {

    public static final String TAG = "SettingsActivity";
    public static final String PHOTO_FILE_NAME = "photo.jpg";
    public static final int RESULT_LOAD_IMAGE = 42;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 52;

    ConstraintLayout clSettings;
    EditText etDisplayName;
    EditText etBio;
    FloatingActionButton fabSave;
    Button btnGallery;
    Button btnCamera;
    ImageView ivProfilePic;
    ProgressBar pbSettings;

    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private User user;
    private File photoFile;
    private Uri imageUri;
    private boolean uploadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        clSettings = findViewById(R.id.clSettings);
        etDisplayName = findViewById(R.id.etDisplayName);
        etBio = findViewById(R.id.etBio);
        fabSave = findViewById(R.id.fabSettingsSave);
        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        ivProfilePic = findViewById(R.id.ivSettingsProfilePic);
        pbSettings = findViewById(R.id.pbSettings);
        uploadedImage = false;

        setUpProfilePic();

        fabSave.setOnClickListener(fabSaveClicked);
        btnGallery.setOnClickListener(btnGalleryClicked);
        btnCamera.setOnClickListener(btnCameraClicked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((AppContext)getApplication()).setIsAppRunning(true, currentUid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((AppContext)getApplication()).setIsAppRunning(false, currentUid);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_LOAD_IMAGE:
                    try {
                        imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        Glide.with(this).load(selectedImage).circleCrop().into(ivProfilePic);
                        uploadedImage = true;
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                    break;
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    Bitmap correctImage = rotateBitmap(takenImage);
                    Glide.with(this).load(correctImage).circleCrop().into(ivProfilePic);
                    uploadedImage = true;
                    break;
                default:
                    break;
            }
            Log.i(TAG, "image not selected");
        } else {
            Snackbar.make(clSettings, "Something went wrong", Snackbar.LENGTH_LONG);
            Log.i(TAG, "something went wrong");
        }
    }

    private final View.OnClickListener fabSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pbSettings.setVisibility(View.VISIBLE);
            fabSave.setEnabled(false);
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            if ((!etDisplayName.getText().toString().isEmpty() || !etBio.getText().toString().isEmpty()) &&
                    !(etDisplayName.getText().toString().equals(user.getDisplayName()) &&
                            (etBio.getText().toString().equals(user.getBio())))) {
                Log.i(TAG, "uploading words...");
                userReference.child("Object").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userFromDb = snapshot.getValue(User.class);
                        if (!etDisplayName.getText().toString().isEmpty()) {
                            userFromDb.setDisplayName(etDisplayName.getText().toString());
                            uploadUsername(etDisplayName.getText().toString());
                        }
                        if (!etBio.getText().toString().isEmpty()) userFromDb.setBio(etBio.getText().toString());
                        userReference.child("Object").setValue(userFromDb).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (!uploadedImage) {
                                    pbSettings.setVisibility(View.GONE);
                                    fabSave.setEnabled(true);
                                }
                            }
                        });
                        user = userFromDb;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }

            if (uploadedImage) {
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        uploadImage(userReference);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            } else {
                pbSettings.setVisibility(View.GONE);
                fabSave.setEnabled(true);
            }
        }
    };

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

            imageUri = FileProvider.getUriForFile(SettingsActivity.this,
                    "com.google.firebase.provider.FirebaseInitProvider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    };

    private Bitmap rotateBitmap(Bitmap bitmap) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
//        Bitmap bm = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), opts);

        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFile.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    private void setUpProfilePic() {
        pbSettings.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.child("Object").getValue(User.class);
//                        setUpHints();
                        if (snapshot.hasChild("ProfilePic")) {
                            Glide.with(SettingsActivity.this)
                                    .load(snapshot.child("ProfilePic").getValue(String.class))
                                    .circleCrop()
                                    .into(ivProfilePic);
                        } else {
                            Glide.with(SettingsActivity.this)
                                    .load(getResources().getDrawable(R.drawable.ic_baseline_person_24))
                                    .circleCrop()
                                    .into(ivProfilePic);
                        }
                        pbSettings.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

//    // TODO: set up hints for username
//    private setUpHints() {
//        if (user.getDisplayName() == null) {
//            etDisplayName.setHint();
//        }
//    }

    private void uploadUsername(String username) {
        FirebaseDatabase.getInstance().getReference()
                .child("Usernames").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(username);
    }

    private void uploadImage(DatabaseReference userReference) {
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("ProfilePics")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid() + getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.i(TAG, "url: " + url);
                            userReference.child("ProfilePic").setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pbSettings.setVisibility(View.GONE);
                                    fabSave.setEnabled(true);
                                }
                            });
                            uploadedImage = false;
                        }
                    });
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.i(TAG, "failed to create directory");
        }
        return new File (mediaStorageDir.getPath() + File.separator + PHOTO_FILE_NAME);
    }
}