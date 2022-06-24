package com.example.metaucapstone.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PrimitiveIterator;

@Parcel
public class Recipe {

    public enum Cuisine {
        AFRICAN, AMERICAN, BRITISH, CAJUN, CARIBBEAN, CHINESE, EASTERN_EUROPEAN,
        EUROPEAN, FRENCH, GERMAN, GREEK, INDIAN, IRISH, ITALIAN, JAPANESE, JEWISH,
        KOREAN, LATIN_AMERICAN, MEDITERRANEAN, MEXICAN, MIDDLE_EASTERN, NORDIC,
        SOUTHERN, SPANISH, THAI, VIETNAMESE
    }

    public enum MealType {
        MAIN_COURSE, SIDE_DISH, DESSERT, APPETIZER, SALAD, BREAD, BREAKFAST,
        SOUP, BEVERAGE, SAUCE, MARINADE, FINGERFOOD, SNACK, DRINK
    }

    public enum Diet {
        GLUTEN_FREE, KETOGENIC, VEGETARIAN, LACTO_VEGETARIAN, OVO_VEGETARIAN,
        VEGAN, PESCETARIAN, PALEO, PRIMAL, LOW_FODMAP, WHOLE30
    }

    public enum Intolerance {
        DAIRY, EGG, GLUTEN, GRAIN, PEANUT, SEAFOOD, SESAME, SHELLFISH, SOY,
        SULFITE, TREE_NUT, WHEAT
    }

    public enum Tag {
        VERY_HEALTHY, CHEAP, VERY_POPULAR, SUSTAINABLE
    }

    public static final HashMap<Cuisine, String> CuisinesMap = new HashMap<Cuisine, String>() {{
        put(Cuisine.AFRICAN, "African"); put(Cuisine.AMERICAN, "American");
        put(Cuisine.BRITISH, "British"); put(Cuisine.CAJUN, "Cajun");
        put(Cuisine.CARIBBEAN, "Caribbean"); put(Cuisine.CHINESE, "Chinese");
        put(Cuisine.EASTERN_EUROPEAN, "Eastern European"); put(Cuisine.EUROPEAN, "European");
        put(Cuisine.FRENCH, "French"); put(Cuisine.GERMAN, "German");
        put(Cuisine.GREEK, "Greek"); put(Cuisine.ITALIAN, "Italian");
        put(Cuisine.INDIAN, "Indian"); put(Cuisine.IRISH, "Irish");
        put(Cuisine.JAPANESE, "Japanese"); put(Cuisine.JEWISH, "Jewish");
        put(Cuisine.KOREAN, "Korean"); put(Cuisine.LATIN_AMERICAN, "Latin American");
        put(Cuisine.MEDITERRANEAN, "Mediterranean"); put(Cuisine.MEXICAN, "Mexican");
        put(Cuisine.MIDDLE_EASTERN, "Middle Eastern"); put(Cuisine.NORDIC, "Nordic");
        put(Cuisine.SOUTHERN, "Southern"); put(Cuisine.SPANISH, "Spanish");
        put(Cuisine.THAI, "Thai"); put(Cuisine.VIETNAMESE, "Vietnamese");
    }};

    public static final HashMap<Diet, String> DietMap = new HashMap<Diet, String>() {{
        put(Diet.GLUTEN_FREE, "Gluten-free"); put(Diet.KETOGENIC, "Ketogenic");
        put(Diet.VEGETARIAN, "Vegetarian"); put(Diet.LACTO_VEGETARIAN, "Lacto-Vegetarian");
        put(Diet.OVO_VEGETARIAN, "Ovo-Vegetarian"); put(Diet.VEGAN, "Vegan");
        put(Diet.PESCETARIAN, "Pescetarian"); put(Diet.PALEO, "Paleo");
        put(Diet.PRIMAL, "Primal"); put(Diet.LOW_FODMAP, "Low Fodmap");
        put(Diet.WHOLE30, "Whole 30");
    }};

    private String name;
    private String id;
    private String imageUrl;
    private String summary;
    private Cuisine cuisine;
    private MealType mealType;
    private List<Diet> diets;
    private List<Intolerance> intoleranceFree;
    private List<Tag> tags;

    public Recipe() { }

    public Recipe(JSONObject jsonObject) throws JSONException {
        diets = new ArrayList<>();
        intoleranceFree = new ArrayList<>();
        tags = new ArrayList<>();

        name = jsonObject.getString("title");
        id = jsonObject.getString("id");
        imageUrl = jsonObject.getString("image");
    }

    public Recipe(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSummary() {
        return summary;
    }

    public Cuisine getCuisine() {
        return cuisine;
    }

    public MealType getMealType() {
        return mealType;
    }

    public List<Diet> getDiets() {
        return diets;
    }

    public List<Intolerance> getIntoleranceFree() {
        return intoleranceFree;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void loadData(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getBoolean("vegetarian")) diets.add(Diet.VEGETARIAN);
        if (jsonObject.getBoolean("vegan")) diets.add(Diet.VEGAN);
        if (jsonObject.getBoolean("glutenFree")) intoleranceFree.add(Intolerance.GLUTEN);
        if (jsonObject.getBoolean("dairyFree")) intoleranceFree.add(Intolerance.DAIRY);
        if (jsonObject.getBoolean("veryHealthy")) tags.add(Tag.VERY_HEALTHY);
        summary = jsonObject.getString("summary");
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i)));
        }
        return recipes;
    }
}
