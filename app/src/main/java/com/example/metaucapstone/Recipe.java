package com.example.metaucapstone;

import java.util.List;

public class Recipe {

    public static final String[] Cuisines = new String[]{"African", "American", "British", "Cajun",
    "Caribbean", "Chinese", "Eastern European", "European", "French", "German", "Greek", "Indian",
    "Irish", "Italian", "Japanese", "Jewish", "Korean", "Latin American", "Mediterranean",
    "Mexican", "Middle Eastern", "Nordic", "Southern", "Spanish", "Thai", "Vietnamese"};

    private String name;
    private String description;
    private Cuisine cuisine;
    private MealType mealType;
    private List<Diet> diets;
    private List<Intolerance> intolerances;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}
