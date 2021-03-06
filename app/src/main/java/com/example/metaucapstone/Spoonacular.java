package com.example.metaucapstone;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metaucapstone.models.Recipe;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Spoonacular {

    public static String TAG = "SPOONACULAR";
    public static String RECIPE_INFO_URL = "https://api.spoonacular.com/recipes/";
    public static String API_KEY;
    public static String COMPLEX_SEARCH_URL;
    public static long RETRY_TIME = 3000L;

    public Spoonacular(Context context) {
        API_KEY = context.getString(R.string.SPOONACULAR_API_KEY);
        COMPLEX_SEARCH_URL = "https://api.spoonacular.com/recipes/complexSearch/?apiKey=" + API_KEY;
    }

    public static void SearchRecipes(Fragment searchResultFragment, Map<String, String> args) throws IOException {
        HttpUrl.Builder requestUrl = HttpUrl.parse(COMPLEX_SEARCH_URL).newBuilder();
        for (String key : args.keySet()) {
            requestUrl.addQueryParameter(key, args.get(key));
        }
        Log.i(TAG, requestUrl.toString());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestUrl.toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "HTTP request failed", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    ((MainActivity) searchResultFragment.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject json = new JSONObject(responseData);
                                JSONArray jsonArray = json.getJSONArray("results");
                                List<Recipe> recipes = Recipe.fromJsonArray(jsonArray);
                                if (recipes.size() == 0) {
                                    searchResultFragment.getView().findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
                                }
                                RecyclerView rvRecipes = searchResultFragment.getView().findViewById(R.id.rvRecipes);
                                RecipeAdapter adapter = (RecipeAdapter) rvRecipes.getAdapter();
                                adapter.recipes.addAll(recipes);

                                searchResultFragment.getView().findViewById(R.id.pbSearchResults)
                                        .setVisibility(ProgressBar.GONE);
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public static void GetRecipeInfo(Recipe recipe, RecipeInformationActivity activity) {
        String requestUrl = RECIPE_INFO_URL + recipe.getId() + "/information?includeNutrition=true&apiKey=" + API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestUrl)
                .build();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "Getting more information on recipes failed", e);
                retryRecipeInfo(client, request, this);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.i(TAG, responseData);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                recipe.loadData(new JSONObject(responseData));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            activity.loadDataIntoUI(recipe);
                        }
                    });
                }
            }
        };
        client.setRetryOnConnectionFailure(true);
        client.newCall(request).enqueue(callback);
    }

    // TODO: find better way to space out retries
    private static void retryRecipeInfo(OkHttpClient client, Request request, Callback callback) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                client.newCall(request).enqueue(callback);
            }
        }, RETRY_TIME);
    }
}
