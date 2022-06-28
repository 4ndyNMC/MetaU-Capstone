package com.example.metaucapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private ConstraintLayout clMain;
    private BottomNavigationView bottomNavigationView;
    BottomNavigationItemView btnSaved;
    BottomNavigationItemView btnSearch;
    BottomNavigationItemView btnFriends;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clMain = findViewById(R.id.clMain);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        btnSaved = findViewById(R.id.action_saved);
        btnSearch = findViewById(R.id.action_search);
        btnFriends = findViewById(R.id.action_friends);

        new Spoonacular(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_image_24);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_saved:
                        Log.i(TAG, "saved");
                        fragment = new SavedFragment();
                        break;
                    case R.id.action_search:
                        Log.i(TAG, "search");
                        fragment = new SearchFragment(fragmentManager);
                        break;
                    case R.id.action_friends:
                        Log.i(TAG, "friends");
                        fragment = new SearchFragment();
                        break;
                    default: break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        btnSearch.performClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}