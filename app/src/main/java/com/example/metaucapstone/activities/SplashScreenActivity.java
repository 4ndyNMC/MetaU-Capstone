package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = "SplashScreenActivity";

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashScreenActivity.this, com.example.metaucapstone.LoginActivity.class));

        db = new DatabaseHelper(this);

        FirebaseDatabase.getInstance().getReference().child("Usernames")
                .addListenerForSingleValueEvent(getNetworkData);
        finish();
    }

    private final ValueEventListener getNetworkData = new ValueEventListener() {
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
}