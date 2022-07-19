package com.example.metaucapstone;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.metaucapstone.MainActivity;
import com.example.metaucapstone.models.Recipe;
import com.example.metaucapstone.models.User;
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
import java.util.Timer;
import java.util.TimerTask;

public class SavedFragment extends com.example.metaucapstone.SearchResultFragment {

    public static final String TAG = "SavedFragment";
    public static final long TIMEOUT_LENGTH = 3000L;

    TextView tvResults;
    DatabaseHelper db;

    Timer timer;
    String uid;
    String currentUid;
    String key;
    boolean otherProfile;
    boolean[] gotResult = new boolean[1];

    public SavedFragment() {
        super();
        this.uid = null;
    }

    public SavedFragment(String uid) {
        super();
        this.uid = uid;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvResults = view.findViewById(R.id.tvResults);
        tvNoResults = view.findViewById(R.id.tvNoResults);
        pbSearchResults = view.findViewById(R.id.pbSearchResults);
        rvRecipes = view.findViewById(R.id.rvRecipes);
        db = new DatabaseHelper(getContext());

        tvResults.setText(R.string.action_saved);

        tvNoResults.setText(R.string.no_saved);
        tvNoResults.setVisibility(View.GONE);

        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.recipes.clear();
        adapter.notifyDataSetChanged();
        load();
    }

    private final ValueEventListener initLoad = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (!snapshot.hasChildren()) {
                Log.i(TAG, "NO RESULTS");
                tvNoResults.setVisibility(View.VISIBLE);
                pbSearchResults.setVisibility(View.GONE);
            }
            else {
                Log.i(TAG, snapshot.getChildrenCount() + " children");
                queryData(SavedFragment.this, key);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };

    private void load() {
        gotResult[0] = false;
        otherProfile = uid != null;
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        key = otherProfile ? uid : currentUid;

        pbSearchResults.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(key).child("Recipes")
                .addListenerForSingleValueEvent(initLoad);
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (!gotResult[0]) {
                    try {
                        getRecipesFromDb();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(timerTask, TIMEOUT_LENGTH);
    }

    private void getRecipesFromDb() throws IOException, ClassNotFoundException {
        Cursor savedRecipeData = db.getRecipeData();
        if (isVisible()) {
            ((MainActivity) getContext()).runOnUiThread(() -> {
                while (savedRecipeData.moveToNext()) {
                    try {
                        byte[] serializedRecipe = savedRecipeData.getBlob(1);
                        ByteArrayInputStream bis = new ByteArrayInputStream(serializedRecipe);
                        ObjectInput in = new ObjectInputStream(bis);
                        Recipe recipe = (Recipe) in.readObject();
                        adapter.recipes.add(recipe);
                        adapter.notifyItemInserted(adapter.recipes.size() - 1);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                pbSearchResults.setVisibility(View.GONE);
            });
        }
    }

    private void queryData(Fragment fragment, String key) {
        DatabaseReference savedReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(key).child("Recipes");
        DatabaseReference recipeReference = FirebaseDatabase.getInstance().getReference()
                .child("Recipes");
        Log.i(TAG, "calling query");
        savedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isVisible()) {
                    RecyclerView rvRecipes = fragment.getView().findViewById(R.id.rvRecipes);
                    RecipeAdapter adapter = (RecipeAdapter) rvRecipes.getAdapter();
                    for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                        Log.i(TAG, recipeSnapshot.getKey());
                        recipeReference.child(recipeSnapshot.getKey()).child("Object")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Recipe recipeFromDb = snapshot.getValue(Recipe.class);
                                        adapter.recipes.add(recipeFromDb);
                                        adapter.notifyItemInserted(adapter.recipes.size() - 1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                    }
                    fragment.getView().findViewById(R.id.pbSearchResults).setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}