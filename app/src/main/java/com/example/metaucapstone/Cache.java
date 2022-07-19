package com.example.metaucapstone;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.metaucapstone.SplashScreenActivity;
import com.example.metaucapstone.models.Recipe;
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
import java.net.URL;

public class Cache {

    public static final String TAG = "Cache";
    public static final String DEFAULT_PROFILE_PIC = "https://firebasestorage.googleapis.com/v0/b/metau-capstone-145a4.appspot.com/o/ProfilePics%2F34AD2.jpeg?alt=media&token=ef7bea72-5852-44b3-b757-60dfd3989bd4";

    private Context context;
    private DatabaseHelper db;
    private DatabaseReference parent;


    public Cache(Context context) {
        this.context = context;
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

    private final ValueEventListener getRecipes = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot recipeIdSnapshot : snapshot.getChildren()) {
                parent.child("Recipes").child(recipeIdSnapshot.getKey())
                        .addListenerForSingleValueEvent(addRecipeToCache);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };

    private final ValueEventListener addRecipeToCache = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            try {
                db.insertRecipe(snapshot.getKey(), snapshot.child("Object")
                        .getValue(Recipe.class));
            } catch (IOException e) {
                e.printStackTrace();
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

    public void initCache() {
        db = new DatabaseHelper(context);

        try {
            storeDefaultPfp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        parent = FirebaseDatabase.getInstance().getReference();
        parent.child("Usernames").addListenerForSingleValueEvent(getUsernames);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference userRef = parent.child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userRef.child("Following").addListenerForSingleValueEvent(getFriends);
            userRef.child("Recipes").addListenerForSingleValueEvent(getRecipes);
        }
    }

    public void clearCache() {
        db = new DatabaseHelper(context);
        db.clearCache(db.getWritableDatabase());
    }
}
