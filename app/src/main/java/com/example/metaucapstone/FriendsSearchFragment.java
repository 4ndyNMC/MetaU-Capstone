package com.example.metaucapstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FriendsSearchFragment extends Fragment {

    public static final String TAG = "FriendsSearchFragment";

    AutoCompleteTextView etSearch;

    Map<String, String> usernames;

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
        etSearch.setEnabled(false);

        getUsernames();
    }

    private void getUsernames() {
        FirebaseDatabase.getInstance().getReference().child("Usernames")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usernames = new HashMap<>();
                        for (DataSnapshot usernameSnapshot : snapshot.getChildren()) {
                            usernames.put(usernameSnapshot.getValue(String.class), usernameSnapshot.getKey());
                        }
                        String[] names = new String[usernames.size()];
                        usernames.keySet().toArray(names);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                R.layout.searchtextview, names);
                        etSearch.setAdapter(adapter);
                        etSearch.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}