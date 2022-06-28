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
import android.widget.EditText;
import android.widget.Spinner;

import com.example.metaucapstone.models.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private FragmentManager fragmentManager;
    private InputMethodManager imm;
    private ConstraintLayout clSearch;
    private EditText etSearch;
    private Spinner spnCuisine;
    private FloatingActionButton fabSearch;

    public SearchFragment() { }

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
        etSearch = view.findViewById(R.id.etSearch);
        fabSearch = view.findViewById(R.id.fabSearch);
        spnCuisine = view.findViewById(R.id.spnCuisine);

        populateSpinner();

        clSearch.setOnClickListener(clSearchClicked);
        fabSearch.setOnClickListener(fabSearchClicked);
    }

    private View.OnClickListener clSearchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imm.hideSoftInputFromWindow(clSearch.getWindowToken(), 0);
        }
    };

    private View.OnClickListener fabSearchClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment searchResultFragment = new SearchResultFragment();
            HashMap<String, String> args = new HashMap<>();
            populateArgs(args);
            try {
                Spoonacular.SearchRecipes(searchResultFragment, args);
            } catch (IOException e) {
                Log.e(TAG, "Search error: ", e);
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer,
                    searchResultFragment).commit();

        }
    };

    private void populateArgs(HashMap<String, String> args) {
        if (!etSearch.getText().toString().isEmpty()) {
            Log.i(TAG, "query: " + etSearch.getText().toString());
            args.put("query", "\"" + etSearch.getText().toString() + "\"");
        }
        if (!spnCuisine.getSelectedItem().toString().equals("")) {
            Log.i(TAG, "cuisine: " + spnCuisine.getSelectedItem().toString());
            args.put("cuisine", spnCuisine.getSelectedItem().toString());
        }
    }

    private void populateSpinner() {
        List<String> cuisines = new ArrayList<>(Recipe.CuisinesMap.values());
        cuisines.sort(String::compareToIgnoreCase);
        cuisines.add(0, "");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.basic_dropdown_item, cuisines);
        spnCuisine.setAdapter(spinnerAdapter);
    }
}