package com.example.metaucapstone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private String name;
    private String id;
    private String imageUrl;
    private Cuisine cuisine;
    private MealType mealType;
    private List<Diet> diets;
    private List<Intolerance> intolerances;

    public Recipe() { }

    public Recipe(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("title");
        id = jsonObject.getString("id");
    }

    public Recipe(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Cuisine getCuisine() {
        return cuisine;
    }

    public void setCuisine(Cuisine cuisine) {
        this.cuisine = cuisine;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public List<Diet> getDiets() {
        return diets;
    }

    public void setDiets(List<Diet> diets) {
        this.diets = diets;
    }

    public List<Intolerance> getIntolerances() {
        return intolerances;
    }

    public void setIntolerances(List<Intolerance> intolerances) {
        this.intolerances = intolerances;
    }

    public static List<Recipe> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            recipes.add(new Recipe(jsonArray.getJSONObject(i)));
        }
        return recipes;
    }

}
