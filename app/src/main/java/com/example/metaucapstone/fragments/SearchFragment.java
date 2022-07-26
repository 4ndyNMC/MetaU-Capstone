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
import java.util.Set;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private FragmentManager fragmentManager;
    private InputMethodManager imm;
    private ConstraintLayout clSearch;
    private EditText etSearch;
    private EditText etMinCarbs;
    private EditText etMaxCarbs;
    private EditText etMinProtein;
    private EditText etMaxProtein;
    private EditText etMinFat;
    private EditText etMaxFat;
    private EditText etMinSugar;
    private EditText etMaxSugar;
    private Spinner spnCuisine;
    private Spinner spnDiet;
    private Spinner spnIntolerance;
    private Spinner spnMealType;
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        clSearch = view.findViewById(R.id.clSearch);
        etSearch = view.findViewById(R.id.etSearch);
        etMinCarbs = view.findViewById(R.id.etMinCarbs);
        etMaxCarbs = view.findViewById(R.id.etMaxCarbs);
        etMinProtein = view.findViewById(R.id.etMinProtein);
        etMaxProtein = view.findViewById(R.id.etMaxProtein);
        etMinFat = view.findViewById(R.id.etMinFat);
        etMaxFat = view.findViewById(R.id.etMaxFat);
        etMinSugar = view.findViewById(R.id.etMinSugar);
        etMaxSugar = view.findViewById(R.id.etMaxSugar);
        fabSearch = view.findViewById(R.id.fabSearch);
        spnCuisine = view.findViewById(R.id.spnCuisine);
        spnDiet = view.findViewById(R.id.spnDiet);
        spnIntolerance = view.findViewById(R.id.spnIntolerance);
        spnMealType = view.findViewById(R.id.spnMealType);

        populateSpinner(spnCuisine, Recipe.CuisinesMap.keySet());
        populateSpinner(spnDiet, Recipe.DietMap.keySet());
        populateSpinner(spnIntolerance, Recipe.INTOLERANCE_MAP.keySet());
        populateSpinner(spnMealType, Recipe.TYPE_MAP.keySet());

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

    private void populateSpinner(Spinner spinner, Set<String> set) {
        List<String> args = new ArrayList<>(set);
        args.sort(String::compareToIgnoreCase);
        args.add(0, "");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.basic_dropdown_item, args);
        spinner.setAdapter(spinnerAdapter);
    }

    private void populateArgs(HashMap<String, String> args) {
        if (!etSearch.getText().toString().isEmpty()) {
            args.put("query", "\"" + etSearch.getText().toString() + "\"");
        }
        if (!spnCuisine.getSelectedItem().toString().equals("")) {
            args.put("cuisine", spnCuisine.getSelectedItem().toString());
        }
        if (!spnDiet.getSelectedItem().toString().equals("")) {
            args.put("diet", spnDiet.getSelectedItem().toString());
        }
        if (!spnIntolerance.getSelectedItem().toString().equals("")) {
            args.put("intolerances", spnIntolerance.getSelectedItem().toString());
        }
        if (!spnMealType.getSelectedItem().toString().equals("")) {
            args.put("type", spnMealType.getSelectedItem().toString());
        }
        if (!etMinCarbs.getText().toString().equals("")) {
            args.put("minCarbs", etMinCarbs.getText().toString());
        }
        if (!etMaxCarbs.getText().toString().equals("")) {
            args.put("maxCarbs", etMaxCarbs.getText().toString());
        }
        if (!etMinProtein.getText().toString().equals("")) {
            args.put("minProtein", etMinProtein.getText().toString());
        }
        if (!etMaxProtein.getText().toString().equals("")) {
            args.put("maxProtein", etMaxProtein.getText().toString());
        }
        if (!etMinFat.getText().toString().equals("")) {
            args.put("minFat", etMinFat.getText().toString());
        }
        if (!etMaxFat.getText().toString().equals("")) {
            args.put("maxFat", etMaxFat.getText().toString());
        }
        if (!etMinSugar.getText().toString().equals("")) {
            args.put("minFat", etMinSugar.getText().toString());
        }
        if (!etMaxSugar.getText().toString().equals("")) {
            args.put("maxFat", etMaxSugar.getText().toString());
        }
    }
}