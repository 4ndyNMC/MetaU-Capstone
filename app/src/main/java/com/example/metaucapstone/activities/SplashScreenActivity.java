package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.metaucapstone.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = "SplashScreenActivity";

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
        parent.child("Users").addListenerForSingleValueEvent(getFriends);
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
                            db.insertFriend(friend.getKey(), "test.com", friendObject);
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
}