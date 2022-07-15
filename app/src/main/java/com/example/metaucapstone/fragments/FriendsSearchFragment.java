package com.example.metaucapstone;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsSearchFragment extends Fragment {

    public static final String TAG = "FriendsSearchFragment";
    public static final long TIMEOUT_LENGTH = 3000L;

    AutoCompleteTextView etSearch;
    ProgressBar pbFriendsSearch;
    DatabaseHelper db;

    Map<String, String> usernames;
    boolean[] gotResult = new boolean[1];

    public FriendsSearchFragment() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etFriendSearch);
        pbFriendsSearch = view.findViewById(R.id.pbFriendsSearch);
        db = new DatabaseHelper(getContext());

        etSearch.setEnabled(false);
        etSearch.setOnItemClickListener(dropdownClicked);

        getUsernames();
    }

    private final AdapterView.OnItemClickListener dropdownClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String uid = usernames.get(parent.getAdapter().getItem(position));
            Fragment fragment = new com.example.metaucapstone.ProfileFragment(uid);
            getParentFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        }
    };

    private final ValueEventListener getUsernames = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.i(TAG, "pulling usernames from network");
            gotResult[0] = true;
            usernames = new HashMap<>();
            for (DataSnapshot usernameSnapshot : snapshot.getChildren()) {
                usernames.put(usernameSnapshot.getValue(String.class), usernameSnapshot.getKey());
                db.insertUsername(usernameSnapshot.getKey(), usernameSnapshot.getValue(String.class));
            }
            String[] names = new String[usernames.size()];
            usernames.keySet().toArray(names);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.searchtextview, names);
            etSearch.setAdapter(adapter);
            etSearch.setEnabled(true);
            pbFriendsSearch.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            gotResult[0] = true;
        }
    };

    private void getUsernames() {
        gotResult[0] = false;
        pbFriendsSearch.setVisibility(View.VISIBLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Usernames");
        ref.addListenerForSingleValueEvent(getUsernames);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (!gotResult[0]) getUsernamesFromDb();
            }
        };
        timer.schedule(timerTask, TIMEOUT_LENGTH);
    }

    private void getUsernamesFromDb() {
        Log.i(TAG, "pulling usernames from db");
        Cursor result = db.getUsernameData();
        usernames = new HashMap<>();

        while (result.moveToNext()) {
            usernames.put(result.getString(0), result.getString(1));
            Log.i(TAG, "added " + result.getString(0));
        }
        ((com.example.metaucapstone.MainActivity) getContext()).runOnUiThread(() -> {
            String[] names = new String[usernames.size()];
            usernames.keySet().toArray(names);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.searchtextview, names);
            etSearch.setAdapter(adapter);
            etSearch.setEnabled(true);
            pbFriendsSearch.setVisibility(View.GONE);
        });
    }
}