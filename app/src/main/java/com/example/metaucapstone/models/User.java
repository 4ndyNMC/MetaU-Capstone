package com.example.metaucapstone.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private List<User> following;
    private List<String> savedRecipes;

    public User() { }

    public User(String name) {
        following = new ArrayList<>();
        savedRecipes = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<User> getFollowing() {
        return following;
    }

    public List<String> getSaved() {
        return savedRecipes;
    }

    public void saveRecipe(Recipe recipe) {
        if (savedRecipes == null) {
            savedRecipes = new ArrayList<>();
        }
        savedRecipes.add(recipe.getId());
    }
}
