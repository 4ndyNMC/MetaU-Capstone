package com.example.metaucapstone;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.metaucapstone.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Script {

    public static final String TAG = "Script";
    public static String RECIPE_INFO_URL = "https://api.spoonacular.com/recipes/";
    public static String API_KEY = "c6717ec61d2641118f97f5849ecb2c0e";
    public static String COMPLEX_SEARCH_URL;

    public static void populate() {
        DatabaseReference recipeReference = FirebaseDatabase.getInstance().getReference().child("Recipes");
        recipeReference.addListenerForSingleValueEvent(updateRecipesWithSteps);
    }

    private static ValueEventListener updateRecipesWithCuisines = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot recipeChild : snapshot.getChildren()) {
                String requestUrl = RECIPE_INFO_URL + recipeChild.getKey() + "/information?includeNutrition=true&apiKey=" + API_KEY;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) { }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            Recipe recipeFromFirebase = recipeChild.child("Object").getValue(Recipe.class);
                            JSONObject json = new JSONObject(responseData);
                            Recipe recipeFromSpoonacular = new Recipe(json);
                            recipeFromSpoonacular.loadData(new JSONObject(responseData));
                            recipeFromFirebase.setCuisines(new ArrayList<>());
                            recipeFromFirebase.getCuisines().addAll(recipeFromSpoonacular.getCuisines());
                            Log.i(TAG, recipeFromFirebase.getName() + " has:");
                            for (Recipe.Cuisine cuisine : recipeFromFirebase.getCuisines()) {
                                Log.i(TAG, "              " + cuisine.name());
                            }
                            FirebaseDatabase.getInstance().getReference().child("Recipes")
                                    .child(recipeFromFirebase.getId()).child("Object")
                                    .setValue(recipeFromFirebase);
                        } catch (JSONException e) { }
                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };

    private static ValueEventListener updateRecipesWithSteps = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot recipeChild : snapshot.getChildren()) {
                String requestUrl = RECIPE_INFO_URL + recipeChild.getKey() + "/information?includeNutrition=true&apiKey=" + API_KEY;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) { }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            Recipe recipeFromFirebase = recipeChild.child("Object").getValue(Recipe.class);
                            JSONObject json = new JSONObject(responseData);
                            Recipe recipeFromSpoonacular = new Recipe(json);
                            recipeFromSpoonacular.loadData(new JSONObject(responseData));
                            recipeFromFirebase.setSteps(new ArrayList<>());
                            recipeFromFirebase.getSteps().addAll(recipeFromSpoonacular.getSteps());
                            Log.i(TAG, recipeFromFirebase.getName() + " has:");
                            for (String step : recipeFromFirebase.getSteps()) {
                                Log.i(TAG, "              " + step);
                            }
                            FirebaseDatabase.getInstance().getReference().child("Recipes")
                                    .child(recipeFromFirebase.getId()).child("Object")
                                    .setValue(recipeFromFirebase);
                        } catch (JSONException e) { }
                    }
                });
//                break;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };
}
