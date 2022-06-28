package com.example.metaucapstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SavedFragment extends SearchResultFragment {

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

        queryData();
    }

    private void queryData() {
        DatabaseReference savedReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Recipes");
        savedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}