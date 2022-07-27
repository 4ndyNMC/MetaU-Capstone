package com.example.metaucapstone.models;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.ParcelConverter;
import org.parceler.Parcels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PrimitiveIterator;

@Parcel
public class Recipe implements Serializable {

    public enum Cuisine {
        AFRICAN, AMERICAN, BRITISH, CAJUN, CARIBBEAN, CHINESE, CREOLE, EASTERN_EUROPEAN,
        EUROPEAN, FRENCH, GERMAN, GREEK, INDIAN, IRISH, ITALIAN, JAPANESE, JEWISH, KOREAN,
        LATIN_AMERICAN, MEDITERRANEAN, MEXICAN, MIDDLE_EASTERN, NORDIC, OTHER, SOUTHERN,
        SPANISH, THAI, VIETNAMESE
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

    public static final String TAG = "Recipe";

    public static final HashMap<String, Cuisine> CuisinesMap = new HashMap<String, Cuisine>() {{
        put("African", Cuisine.AFRICAN);
        put("American", Cuisine.AMERICAN);
        put("British", Cuisine.BRITISH);
        put("Cajun", Cuisine.CAJUN);
        put("Caribbean", Cuisine.CARIBBEAN);
        put("Chinese", Cuisine.CHINESE);
        put("Creole", Cuisine.CREOLE);
        put("Eastern European", Cuisine.EASTERN_EUROPEAN);
        put("European", Cuisine.EUROPEAN);
        put("French", Cuisine.FRENCH);
        put("German", Cuisine.GERMAN);
        put("Greek", Cuisine.GREEK);
        put("Italian", Cuisine.ITALIAN);
        put("Indian", Cuisine.INDIAN);
        put("Irish", Cuisine.IRISH);
        put("Japanese", Cuisine.JAPANESE);
        put("Jewish", Cuisine.JEWISH);
        put("Korean", Cuisine.KOREAN);
        put("Latin American", Cuisine.LATIN_AMERICAN);
        put("Mediterranean", Cuisine.MEDITERRANEAN);
        put("Mexican", Cuisine.MEXICAN);
        put("Middle Eastern", Cuisine.MIDDLE_EASTERN);
        put("Nordic", Cuisine.NORDIC);
        put("Other", Cuisine.OTHER);
        put("Southern", Cuisine.SOUTHERN);
        put("Spanish", Cuisine.SPANISH);
        put("Thai", Cuisine.THAI);
        put("Vietnamese", Cuisine.VIETNAMESE);
    }};

    public static final HashMap<String, Diet> DietMap = new HashMap<String, Diet>() {{
        put("Gluten-free", Diet.GLUTEN_FREE);
        put("Ketogenic", Diet.KETOGENIC);
        put("Vegetarian", Diet.VEGETARIAN);
        put("Lacto-Vegetarian", Diet.LACTO_VEGETARIAN);
        put("Ovo-Vegetarian", Diet.OVO_VEGETARIAN);
        put("Vegan", Diet.VEGAN);
        put("Pescetarian", Diet.PESCETARIAN);
        put("Paleo", Diet.PALEO);
        put("Primal", Diet.PRIMAL);
        put("Low Fodmap", Diet.PRIMAL);
        put("Whole 30", Diet.WHOLE30);
    }};

    public static final HashMap<String, Intolerance> INTOLERANCE_MAP = new HashMap<String, Intolerance>() {{
        put("Dairy", Intolerance.DAIRY);
        put("Egg", Intolerance.EGG);
        put("Gluten", Intolerance.GLUTEN);
        put("Grain", Intolerance.GRAIN);
        put("Peanut", Intolerance.PEANUT);
        put("Seafood", Intolerance.SEAFOOD);
        put("Sesame", Intolerance.SESAME);
        put("Shellfish", Intolerance.SHELLFISH);
        put("Soy", Intolerance.SOY);
        put("Sulfite", Intolerance.SULFITE);
        put("Tree_Nut", Intolerance.TREE_NUT);
        put("Wheat", Intolerance.WHEAT);
    }};

    public static final HashMap<String, MealType> TYPE_MAP = new HashMap<String, MealType>() {{
        put("Main_Course", MealType.MAIN_COURSE);
        put("Side_Dish", MealType.SIDE_DISH);
        put("Dessert", MealType.DESSERT);
        put("Appetizer", MealType.APPETIZER);
        put("Salad", MealType.SALAD);
        put("Bread", MealType.BREAD);
        put("Breakfast", MealType.BREAKFAST);
        put("Soup", MealType.SOUP);
        put("Beverage", MealType.BEVERAGE);
        put("Sauce", MealType.SAUCE);
        put("Marinade", MealType.MARINADE);
        put("Finger_Food", MealType.FINGERFOOD);
        put("Snack", MealType.SNACK);
        put("Drink", MealType.DRINK);
    }};

    private String name;
    private String id;
    private String imageUrl;
    private String summary;
    private MealType mealType;
    private List<Cuisine> cuisines;
    private List<Diet> diets;
    private List<Intolerance> intoleranceFree;
    private List<Tag> tags;
    private List<String> users;
    private List<String> steps;

    public Recipe() { }

    public Recipe(JSONObject jsonObject) throws JSONException {
        diets = new ArrayList<>();
        intoleranceFree = new ArrayList<>();
        tags = new ArrayList<>();
        cuisines = new ArrayList<>();
        diets = new ArrayList<>();
        intoleranceFree = new ArrayList<>();
        tags = new ArrayList<>();
        users = new ArrayList<>();
        steps = new ArrayList<>();

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

    public List<Cuisine> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<Cuisine> cuisines) {
        this.cuisines =  cuisines;
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

    public void addUser(String user) {
        users.add(user);
    }

    public List<String> getSteps() {
        return steps;
    }

    public List<String> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void loadData(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getBoolean("vegetarian")) diets.add(Diet.VEGETARIAN);
        if (jsonObject.getBoolean("vegan")) diets.add(Diet.VEGAN);
        if (jsonObject.getBoolean("glutenFree")) intoleranceFree.add(Intolerance.GLUTEN);
        if (jsonObject.getBoolean("dairyFree")) intoleranceFree.add(Intolerance.DAIRY);
        if (jsonObject.getBoolean("veryHealthy")) tags.add(Tag.VERY_HEALTHY);
        if (cuisines == null) cuisines = new ArrayList<>();
        if (steps == null) steps = new ArrayList<>();
        JSONArray jsonCuisines = jsonObject.getJSONArray("cuisines");
        if (jsonCuisines.length() == 0) cuisines.add(Cuisine.OTHER);
        else {
            for (int i = 0; i < jsonCuisines.length(); i++) {
                String cuisineName = jsonCuisines.getString(i);
                if (CuisinesMap.containsKey(cuisineName)) cuisines.add(CuisinesMap.get(cuisineName));
            }
        }
        JSONArray analyzedInstructions = jsonObject.getJSONArray("analyzedInstructions");
        JSONObject stepsObject = analyzedInstructions.getJSONObject(0);
        JSONArray stepsArray = stepsObject.getJSONArray("steps");
        for (int i = 0; i < stepsArray.length(); i++) {
            JSONObject stepObject = stepsArray.getJSONObject(i);
            steps.add(stepObject.getString("step"));
        }
        if (cuisines.size() == 0) cuisines.add(Cuisine.OTHER);
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
