package com.example.metaucapstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";

    FragmentManager fragmentManager;
    RecyclerView rvFriends;
    FloatingActionButton fabSearch;
    ProgressBar pbFriends;

    List<Map<String, String>> friends;

    public FriendsFragment() { }

    public FriendsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFriends = view.findViewById(R.id.rvFriends);
        fabSearch = view.findViewById(R.id.fabSearchFriends);
        pbFriends = view.findViewById(R.id.pbFriends);

        friends = new ArrayList<>();
        UserAdapter adapter = new UserAdapter(fragmentManager, getContext(), friends);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        fabSearch.setOnClickListener(fabSearchClicked);

        getFriends();
    }

    private final View.OnClickListener fabSearchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fragmentManager.beginTransaction().replace(R.id.flContainer,
                    new FriendsSearchFragment()).commit();
        }
    };

    private void getFriends() {
        pbFriends.setVisibility(View.VISIBLE);
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.child(currentUid).child("Following")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserAdapter adapter = (UserAdapter) rvFriends.getAdapter();
                        for (DataSnapshot friend : snapshot.getChildren()) {
                            userReference.child(friend.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, String> friendMap = new HashMap<String, String>() {{
                                        put("uid", snapshot.getKey());
                                        put("username", snapshot.child("Object/displayName")
                                                .getValue(String.class));
                                        put("imageUrl", snapshot.child("ProfilePic")
                                                .getValue(String.class));
                                    }};
                                    adapter.users.add(friendMap);
                                    adapter.notifyItemInserted(adapter.users.size() - 1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.i(TAG, error.toString());
                                    pbFriends.setVisibility(View.GONE);
                                }
                            });
                        }
                        pbFriends.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, error.toString());
                        pbFriends.setVisibility(View.GONE);
                    }
                });
    }
}