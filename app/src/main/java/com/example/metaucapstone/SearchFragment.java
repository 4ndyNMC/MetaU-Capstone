package com.example.metaucapstone;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private FragmentManager fragmentManager;
    private InputMethodManager imm;
    private ConstraintLayout clSearch;
    private Spinner spnCuisine;
    private FloatingActionButton fabSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    public SearchFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        clSearch = view.findViewById(R.id.clSearch);
        fabSearch = view.findViewById(R.id.fabSearch);
        spnCuisine = view.findViewById(R.id.spnCuisine);
        populateSpinner();

        clSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(clSearch.getWindowToken(), 0);
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment searchResultFragment = new SearchResultFragment();
                try {
                    Spoonacular.SearchRecipes(searchResultFragment);
                } catch (IOException e) {
                    Log.e(TAG, "Search error: ", e);
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer,
                        searchResultFragment).commit();
            }
        });
    }

    private void populateSpinner() {
        List<String> cuisines = new ArrayList<>(Arrays.asList(Recipe.Cuisines));
        cuisines.add(0, "");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.basic_dropdown_item, cuisines);
        spnCuisine.setAdapter(spinnerAdapter);

    }
}