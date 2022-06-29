package com.example.metaucapstone.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private String displayName;
    private String bio;
    private List<User> following;
    private List<String> saved;

    public User() { }

    public User(String name) {
        following = new ArrayList<>();
        saved = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<User> getFollowing() {
        return following;
    }

    public List<String> getSaved() {
        return saved;
    }

    public void saveRecipe(Recipe recipe) {
        if (saved == null) {
            saved = new ArrayList<>();
        }
        saved.add(recipe.getId());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void removeRecipe(Recipe recipe) {
        saved.remove(recipe.getId());
    }
}
