package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.metaucapstone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = "SplashScreenActivity";
    public static final String DEFAULT_PROFILE_PIC = "https://firebasestorage.googleapis.com/v0/b/metau-capstone-145a4.appspot.com/o/ProfilePics%2F34AD2.jpeg?alt=media&token=ef7bea72-5852-44b3-b757-60dfd3989bd4";

    DatabaseHelper db;
    DatabaseReference parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashScreenActivity.this,
                com.example.metaucapstone.LoginActivity.class));

        db = new DatabaseHelper(this);

        parent = FirebaseDatabase.getInstance().getReference();
        parent.child("Usernames").addListenerForSingleValueEvent(getUsernames);
        parent.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following").addListenerForSingleValueEvent(getFriends);
        try {
            storeDefaultPfp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    private final ValueEventListener getUsernames = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.i(TAG, "retrieved usernames");
            for (DataSnapshot usernameSnapshot : snapshot.getChildren()) {
                String username = usernameSnapshot.getValue(String.class);
                String uid = usernameSnapshot.getKey();
                try {
                    db.insertUsername(username, uid);
                } catch (SQLiteConstraintException e) {
                    Log.i(TAG, uid + username + " not inserted");
                }
            }
            finish();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };

    private final ValueEventListener getFriends = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (!snapshot.hasChildren()) {
                return;
            }
            for (DataSnapshot friend : snapshot.getChildren()) {
                parent.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User friendObject = snapshot.child(friend.getKey()).child("Object").getValue(User.class);
                        Log.i(TAG, friendObject.getDisplayName());
                        try {
                            db.insertFriend(friend.getKey(),
                                    friend.child("ProfilePicUrl").getValue(String.class),
                                    friendObject);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };

    private void storeDefaultPfp() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(DEFAULT_PROFILE_PIC).openConnection();
        Thread thread = new Thread(() -> {
            try {
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap x = BitmapFactory.decodeStream(in);
                db.storePfp(x);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}