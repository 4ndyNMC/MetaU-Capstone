package com.example.metaucapstone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.metaucapstone.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment {

    public TextView tvNoResults;
    public ProgressBar pbSearchResults;
    public RecyclerView rvRecipes;
    public RecipeAdapter adapter;
    public List<Recipe> recipes;

    public SearchResultFragment() { }

//    public static SearchResultFragment newInstance(String param1, String param2) {
//        SearchResultFragment fragment = new SearchResultFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNoResults = view.findViewById(R.id.tvNoResults);
        pbSearchResults = view.findViewById(R.id.pbSearchResults);
        rvRecipes = view.findViewById(R.id.rvRecipes);

        tvNoResults.setVisibility(View.GONE);

        recipes = new ArrayList<>();
        adapter = new RecipeAdapter(getContext(), recipes);
        rvRecipes.setAdapter(adapter);
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));

        pbSearchResults.setVisibility(ProgressBar.VISIBLE);
    }
}