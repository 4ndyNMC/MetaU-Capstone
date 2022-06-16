Original App Design Project - README Template
===

# Meta University Capstone Project

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Users are able to find ingredients for certain recipes in nearby stores and optimize for certain criteria, ie price, "healthiness", etc.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Utility, food
- **Mobile:** Yes
- **Story:** Users are able to find ingredients for certain recipes in nearby stores and optimize for certain criteria, ie price, “healthiness”, etc. Show totals for ingredients at different stores, shortest path between stores? 
- **Market:** Anyone who cooks, perhaps people who like to share what they cook?
- **Habit:** Users may be able to share with others what they've made and where they got their ingredients
- **Scope:** 
    - V1 would have a basic set of recipes and show where ingredients are at different stores on a map. 
    - V2 would have the prices of everything at each store and some heuristic for "healthiness" of ingredients and recipes, and display a sorted list of stores to buy ingredients with those metrics in mind. 
    - V3 might have some feature to share with others what meals you've made and the details about it.
## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User logs on and searches "spaghetti", and can choose between a number of different recipes. Once one is selected, the user sees a map of stores around them and how much it would cost to buy the ingredients to that recipe at that store.
* User can save recipes

**Optional Nice-to-have Stories**

* User posts recipe they made and can see recipes their friends made, pictures and other details

### 2. Screen Archetypes

* [list first screen here]
   * [list associated required story here]
   * ...
* [list second screen here]
   * [list associated required story here]
   * ...

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Saved (scroll, leftmost) - go to area where user can see saved recipes and history (cooked recipes)
* Recipes (chicken leg, right of leftmost) - go to searchbar where user can search for recipes
* Feed (person, left of rightmost) - go to screen where user can see recipes that their friends have made and posted
* Chat (speech bubble, rightmost) - go to screen where users can chat with friends about recipes and food

**Flow Navigation** (Screen to Screen)

* Login screen
   * Sign up screen
   * Feed
* Saved, Recipes, Feed, Chat (all of these can go to each other via the toolbar)

## Wireframes
![wireframe](https://user-images.githubusercontent.com/36943811/173662715-0f5ea563-9fd9-4a90-9009-483de79d936b.jpeg)

## Schema 

### Models
Model: User
| Property    | Type        | Description
| ----------- | ----------- | -----------
| username    | String      | username to log in and display
| password    | String      | password to log in
| saved       | List\<Recipe> | list of recipes that user saved
| history     | List\<Recipe> | list of recipes that user has cooked

Model: Post
| Property    | Type        | Description
|-------------|-------------|------------
| user        | User        | user who created post
| recipes     | List\<Recipe> | recipes user cooked in post
| description | String      | commentary about post by user

Model: Recipe
| Property    | Type        | Description
|-------------|-------------|------------
| name        | String      | name of recipe
| description | String      | description of recipe
| cuisine     | Cuisine     | cuisine of recipe
| diet        | List\<Diet> | diets that this recipe can support
| intolerances| List\<Intolerance> | ingredients that many people cannot eat
| meal type   | MealType    | type of meal

Enum: Cuisine - [values](https://spoonacular.com/food-api/docs#Cuisines)
 | Enum: Diet - [values](https://spoonacular.com/food-api/docs#Diets)
 | Enum: Intolerance - [values](https://spoonacular.com/food-api/docs#Intolerances)
 | Enum: Meal Type - [values](https://spoonacular.com/food-api/docs#Meal-Types)

