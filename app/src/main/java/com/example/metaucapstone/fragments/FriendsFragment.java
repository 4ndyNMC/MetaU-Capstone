package com.example.metaucapstone;

import android.database.Cursor;
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
import android.widget.TextView;

import com.example.metaucapstone.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";
    public static final long TIMEOUT_LENGTH = 3000L;
    private static final int LAST_ONLINE_WEIGHT = 0;
    private static final int TOTAL_RECIPES_WEIGHT = 100;
    private static final int RELEVANT_RECIPES_WEIGHT = 0;

    FragmentManager fragmentManager;
    RecyclerView rvFriends;
    TextView tvNoFriends;
    FloatingActionButton fabSearch;
    ProgressBar pbFriends;
    DatabaseHelper db;
    com.example.metaucapstone.UserAdapter adapter;

    List<Map<String, Object>> friends;
    boolean[] gotResult = new boolean[1];

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
        tvNoFriends = view.findViewById(R.id.tvNoFriends);
        fabSearch = view.findViewById(R.id.fabSearchFriends);
        pbFriends = view.findViewById(R.id.pbFriends);
        db = new DatabaseHelper(getContext());

        tvNoFriends.setVisibility(View.GONE);

        friends = new ArrayList<>();
        adapter = new com.example.metaucapstone.UserAdapter(fragmentManager, getContext(), friends);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        fabSearch.setOnClickListener(fabSearchClicked);

        getFriends();
    }

    private final View.OnClickListener fabSearchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fragmentManager.beginTransaction().replace(R.id.flContainer,
                    new com.example.metaucapstone.FriendsSearchFragment()).commit();
        }
    };

    private final ValueEventListener getFriendsFromNetwork = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            com.example.metaucapstone.UserAdapter adapter = (com.example.metaucapstone.UserAdapter) rvFriends.getAdapter();
            adapter.users.clear();
            adapter.notifyDataSetChanged();
            if (!snapshot.child("Following").hasChildren()) {
                tvNoFriends.setVisibility(View.VISIBLE);
                pbFriends.setVisibility(View.GONE);
                return;
            }
            for (DataSnapshot friend : snapshot.child("Following").getChildren()) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(friend.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Map<String, Object> friendMap = new HashMap<String, Object>() {{
                                    put("uid", snapshot.getKey());
                                    put("username", snapshot.child("Object/displayName")
                                            .getValue(String.class));
                                    put("imageUrl", snapshot.child("ProfilePic")
                                            .getValue(String.class));
                                    put("score", calculateScore(snapshot));
                                }};
                                adapter.users.add(friendMap);
                                sortUsersPoints(adapter.users);
                                adapter.notifyItemInserted(adapter.users.size() - 1);
                                gotResult[0] = true;
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
            gotResult[0] = true;
        }
    };

    private void getFriends() {
        gotResult[0] = false;
        pbFriends.setVisibility(View.VISIBLE);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference.child(currentUid).addListenerForSingleValueEvent(getFriendsFromNetwork);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (!gotResult[0]) {
                    getFriendsFromDb();
                }
            }
        };
        timer.schedule(timerTask, TIMEOUT_LENGTH);
    }

    private void getFriendsFromDb() {
        Log.i(TAG, "pulling usernames from db");
        Cursor result = db.getFriendsData();
        if (isVisible()) {
            ((com.example.metaucapstone.MainActivity) getContext()).runOnUiThread(() -> {
                while (result.moveToNext()) {
                    adapter.users.add(new HashMap<String, Object>() {{
                        try {
                            byte[] serializedUser = result.getBlob(2);
                            ByteArrayInputStream bis = new ByteArrayInputStream(serializedUser);
                            ObjectInput in = new ObjectInputStream(bis);
                            put("username", ((User) in.readObject()).getDisplayName());
                            put("uid", result.getString(0));
                            put("imageUrl", db.getDefaultPfp());
                        } catch (IOException | ClassNotFoundException e) {
                            Log.e(TAG, e.toString());
                        }
                    }});
                    sortUsersAlphabetically(adapter.users);
                    adapter.notifyItemInserted(adapter.users.size() - 1);
                    pbFriends.setVisibility(View.GONE);
                }
            });
        }
    }

    private void sortUsersAlphabetically(List<Map<String, Object>> list) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Log.i(TAG, "comparison: " + ((String) o1.get("username")).compareToIgnoreCase((String) o2.get("username")));
                return ((String) o1.get("username")).compareToIgnoreCase((String) o2.get("username"));
            }
        });
    }

    private void sortUsersPoints(List<Map<String, Object>> list) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return ((Integer) o2.get("score")) - ((Integer) o1.get("score"));
            }
        });
    }

    private int calculateScore(DataSnapshot friendSnapshot) {
        int lastOnline = 1;
        int totalRecipes = (int) friendSnapshot.child("Recipes").getChildrenCount();
        int relevantRecipes = 0;
        return LAST_ONLINE_WEIGHT * lastOnline + TOTAL_RECIPES_WEIGHT * totalRecipes + RELEVANT_RECIPES_WEIGHT * relevantRecipes;
    }
}