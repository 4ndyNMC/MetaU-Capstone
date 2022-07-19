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

import com.example.metaucapstone.models.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationClicked);
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
                Cache cache = new Cache(this);
                cache.clearCache();
                startActivity(new Intent(MainActivity.this, com.example.metaucapstone.LoginActivity.class));
                finish();
                break;
            case R.id.btnSettings:
                startActivity(new Intent(MainActivity.this, com.example.metaucapstone.SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationClicked = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_saved:
                    fragment = new com.example.metaucapstone.SavedFragment();
                    break;
                case R.id.action_search:
                    fragment = new com.example.metaucapstone.SearchFragment(fragmentManager);
                    break;
                case R.id.action_friends:
                    fragment = new com.example.metaucapstone.FriendsFragment(fragmentManager);
                    break;
                case R.id.action_profile:
                    fragment = new com.example.metaucapstone.ProfileFragment();
                    break;
                default: break;
            }
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.flContainer);
            if (currentFragment != null && currentFragment.getClass() == fragment.getClass()) {
                return true;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        }
    };
}