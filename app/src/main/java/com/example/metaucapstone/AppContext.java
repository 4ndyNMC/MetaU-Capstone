package com.example.metaucapstone;

import android.app.Application;

=import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class AppContext extends Application {
    public boolean isAppRunning = true;

    public void setIsAppRunning(boolean v, String uid) {
        isAppRunning = v;
        DatabaseReference timeRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid).child("LastOnline");
        if (isAppRunning) {
            timeRef.setValue(0);
        } else {
            timeRef.setValue(new Date().getTime());
        }
    }

    public boolean isAppRunning() {
        return isAppRunning;
    }
}
