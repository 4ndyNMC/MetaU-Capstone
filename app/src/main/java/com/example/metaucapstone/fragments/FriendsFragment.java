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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsFragment extends Fragment {

    public static final String TAG = "FriendsFragment";
    public static final long TIMEOUT_LENGTH = 3000L;
    private static final long TOTAL_RECIPES_WEIGHT = 50000000L;
    private static final long RELEVANT_RECIPES_WEIGHT = 10000000L;
    private static final long LAST_ONLINE_WEIGHT = -100L;

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
            int numFriends = (int) snapshot.child("Following").getChildrenCount();
            int[] inserted = new int[] {0};
            for (DataSnapshot friend : snapshot.child("Following").getChildren()) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(friend.getKey())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot friendSnapshot) {
                                Map<String, Object> friendMap = new HashMap<String, Object>() {{
                                    put("uid", friendSnapshot.getKey());
                                    put("username", friendSnapshot.child("Object/displayName")
                                            .getValue(String.class));
                                    put("imageUrl", friendSnapshot.child("ProfilePic")
                                            .getValue(String.class));
                                    put("score", calculateScore(friendSnapshot, getRelevantRecipes(snapshot, friendSnapshot)));
                                }};
                                adapter.users.add(friendMap);
                                gotResult[0] = true;
                                inserted[0]++;
                                if (inserted[0] == numFriends) {
                                    sortUsersPoints(adapter.users, 0, adapter.users.size() - 1);
                                    adapter.notifyItemInserted(adapter.users.size() - 1);
                                }
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

    private long calculateScore(DataSnapshot friendSnapshot, int relevantRecipes) {
        long totalRecipes = friendSnapshot.child("Recipes").getChildrenCount();
        long onlineAgo =  friendSnapshot.child("LastOnline").getValue(Long.class) == 0 ?
                0 : (new Date()).getTime() - friendSnapshot.child("LastOnline").getValue(Long.class);
        Log.i(TAG, friendSnapshot.getKey() + " has: lastOnline - " + onlineAgo
                + ", totalRecipes - " + totalRecipes
                + ", relevantRecipes - " + relevantRecipes);
        long score = TOTAL_RECIPES_WEIGHT * totalRecipes + RELEVANT_RECIPES_WEIGHT * relevantRecipes + LAST_ONLINE_WEIGHT * onlineAgo;
        Log.i(TAG, friendSnapshot.child("Object").getValue(User.class).getDisplayName() + " has score " + score + " ... " + friendSnapshot.getKey());
        return score;
    }

    private int getRelevantRecipes(DataSnapshot snapshot, DataSnapshot friendSnapshot) {
        DataSnapshot userRecipes = snapshot.child("Recipes");
        DataSnapshot friendRecipes = friendSnapshot.child("Recipes");
        int relevantRecipes = 0;
        for (DataSnapshot recipe : userRecipes.getChildren()) {
            if (friendRecipes.hasChild(recipe.getKey())) relevantRecipes++;
        }
        return relevantRecipes;
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

    private void sortUsersPoints(List<Map<String, Object>> list, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(list, begin, end);

            sortUsersPoints(list, begin, partitionIndex-1);
            sortUsersPoints(list, partitionIndex+1, end);
        }
    }

    private int partition(List<Map<String, Object>> list, int begin, int end) {
        long pivot = score(list.get(end));
        int i = (begin-1);

        for (int j = begin; j < end; j++) {
            if (score(list.get(j)) > pivot) {
                i++;

                Map<String, Object> swapTemp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, swapTemp);
            }
        }

        Map<String, Object> swapTemp = list.get(i + 1);
        list.set(i + 1, list.get(end));
        list.set(end, swapTemp);

        return i+1;
    }

    private long score(Map<String, Object> user) {
        return (long) user.get("score");
    }
}