package com.example.metaucapstone;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

public class Spoonacular {

    public static String TAG = "SPOONACULAR";
    public static String API_KEY;
    public static String SPOONACULAR_URL;

    public Spoonacular(Context context) {
        API_KEY = context.getString(R.string.SPOONACULAR_API_KEY);
        SPOONACULAR_URL = "https://api.spoonacular.com/recipes/complexSearch/?apiKey=" +
                API_KEY;
    }

    public static String SearchRecipes(Fragment searchResultFragment) throws IOException {

//        StringBuilder requestUrl = new StringBuilder(SPOONACULAR_URL);
//        requestUrl.append("&number=5");
//        requestUrl.append("&query=pasta");
        HttpUrl.Builder requestUrl = HttpUrl.parse(SPOONACULAR_URL).newBuilder();
        requestUrl.addQueryParameter("query", "burger");
        requestUrl.addQueryParameter("maxFat", "25");
        requestUrl.addQueryParameter("number", "2");
        Log.i(TAG, requestUrl.toString());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(requestUrl.toString())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "HTTP request failed");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    Log.i(TAG, myResponse);

                }
            }
        });
        return null;
    }

}
