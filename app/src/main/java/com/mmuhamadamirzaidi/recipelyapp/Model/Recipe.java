package com.mmuhamadamirzaidi.recipelyapp.Model;

public class Recipe {

    private String recipeName, recipeImage, recipeIngredients, recipeServes, recipeSteps, categoryId;

    public Recipe() {
    }

    public Recipe(String recipeName, String recipeImage, String recipeIngredients, String recipeServes, String recipeSteps, String categoryId) {
        this.recipeName = recipeName;
        this.recipeImage = recipeImage;
        this.recipeIngredients = recipeIngredients;
        this.recipeServes = recipeServes;
        this.recipeSteps = recipeSteps;
        this.categoryId = categoryId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(String recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public String getRecipeServes() {
        return recipeServes;
    }

    public void setRecipeServes(String recipeServes) {
        this.recipeServes = recipeServes;
    }

    public String getRecipeSteps() {
        return recipeSteps;
    }

    public void setRecipeSteps(String recipeSteps) {
        this.recipeSteps = recipeSteps;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
