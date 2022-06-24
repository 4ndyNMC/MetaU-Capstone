package com.example.metaucapstone;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private List<User> following;
    private List<Recipe> saved;

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

    public List<Recipe> getSaved() {
        return saved;
    }
}
