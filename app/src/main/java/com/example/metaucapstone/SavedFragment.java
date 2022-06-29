package com.example.metaucapstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.metaucapstone.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedFragment extends SearchResultFragment {

    public static final String TAG = "SavedFragment";

    TextView tvResults;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvResults = view.findViewById(R.id.tvResults);
        tvNoResults = view.findViewById(R.id.tvNoResults);
        pbSearchResults = view.findViewById(R.id.pbSearchResults);
        rvRecipes = view.findViewById(R.id.rvRecipes);

        tvResults.setText(R.string.action_saved);

        tvNoResults.setText(R.string.no_saved);
        tvNoResults.setVisibility(View.GONE);

        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        pbSearchResults.setVisibility(View.VISIBLE);

//        load();
    }

    @Override
    public void onResume() {
        super.onResume();
//        adapter.recipes.clear();
//        adapter.notifyDataSetChanged();
        load();
    }

    private void load() {
        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Recipes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChildren()) {
                            tvNoResults.setVisibility(View.VISIBLE);
                            pbSearchResults.setVisibility(View.GONE);
                        }
                        else {
                            queryData(SavedFragment.this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void queryData(Fragment fragment) {
        DatabaseReference savedReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Recipes");
        DatabaseReference recipeReference = FirebaseDatabase.getInstance().getReference()
                .child("Recipes");
        savedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RecyclerView rvRecipes = fragment.getView().findViewById(R.id.rvRecipes);
                RecipeAdapter adapter = (RecipeAdapter) rvRecipes.getAdapter();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    recipeReference.child(recipeSnapshot.getKey()).child("Object")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Recipe recipeFromDb = snapshot.getValue(Recipe.class);
                                    adapter.recipes.add(recipeFromDb);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                }
                fragment.getView().findViewById(R.id.pbSearchResults).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}