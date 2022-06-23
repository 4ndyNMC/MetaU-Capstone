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
Users are able to find recipes for dishes based on a number of criteria, able to optimize for any given nutritional metrics of their choice. Users can save recipes and view their friends' saved recipes.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Utility, food
- **Mobile:** Yes
- **Story:** Users are able to find recipes for dishes based on a number of criteria, able to optimize for any given nutritional metrics of their choice. Users can save recipes and view their friends' saved recipes.
- **Market:** Anyone who cooks, perhaps people who like to share what they cook?
- **Habit:** Users may be able to share with others what they've made and where they got their ingredients
- **Scope:** 
    - V1 would have the ability to search for different recipes based on certain criteria 
    - V2 would have the ability to save posts, follow different users, and view what their saved recipes are 
    - V3 would have the ability to search for different users based on what cuisines they've mostly made
    - V4 would have stretch goals
## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can log in & sign up
* User can search for recipes with variable criteria
* User can search for other users with variable criteria & follow them to see their saved recipes
* User can save recipes

**Optional Nice-to-have Stories**

* User can view recipe history

**Stretch goals**

* have app read from API to set constants instead of hardcoding them

### 2. Navigation

**Tab Navigation** (Tab to Screen)

* Saved (scroll, leftmost) - go to area where user can see saved recipes
* Recipes (chicken leg, center) - go to search where user can search for recipes
* Following (person, rightmost) - go to screen where user can see the other users they are following 

**Flow Navigation** (Screen to Screen)

* Splash screen -> Log in or Sign up ->
    * Search recipe -> search results -> recipe info page
    * Saved recipes -> recipe info page
    * Users following ->
        * User's saved recipes -> recipe info page
        * Search users -> user's saved recipes -> recipe info page

## Wireframes
![image](https://user-images.githubusercontent.com/36943811/174688577-9698fcc8-1c32-4f0c-82ad-70e427e94ed0.png)

## Schema 

### Models
Model: User
| Property    | Type        | Description
| ----------- | ----------- | -----------
| username    | String      | username to log in and display
| password    | String      | password to log in
| saved       | List\<Recipe> | list of recipes that user saved

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

### Networking
* **Log in** will query to database and find the inputted username & password
* **Sign up** will query to the database to find the inputted username or insert a new user with the inputted username & password
* **Search recipe** will query to the Spoonacular API with the criteria selected by the user
* **User saved** will query to the database for the saved recipes of a given user, current user or following
* **Recipe info** should have recipe objects passed into it from previous queries so it shouldn't need to query for more data
* **Following** will query the database for all users that the current user is following
* **Search users** will query the database for users with a high enough proportion of the given cuisine type saved and return them to be displayed in following
