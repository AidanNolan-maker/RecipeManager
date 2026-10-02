package com.aidan.recipemanager.service;

import com.aidan.recipemanager.model.Recipe;
import com.aidan.recipemanager.repository.RecipeRepository;
import com.aidan.recipemanager.repository.RecipeRepositoryInterface;

import java.util.List;
import java.util.Optional;

public class RecipeService {
    private final RecipeRepositoryInterface recipeRepository;

    public RecipeService() {
        this(new RecipeRepository());
    }

    public RecipeService(RecipeRepositoryInterface recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe createRecipe(Recipe recipe) {
        validateRecipe(recipe);

        return recipeRepository.save(recipe);
    }

    public Optional<Recipe> getRecipeById(int id) {
        return recipeRepository.findById(id);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe updateRecipe(Recipe recipe) {
        validateRecipe(recipe);

        return recipeRepository.update(recipe);
    }

    public void deleteRecipe(int id) {
        recipeRepository.deleteById(id);
    }

    private void validateRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null.");
        }

        if (recipe.getName() == null || recipe.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Recipe name cannot be blank."
            );
        }

        if (recipe.getPrepTime() < 0) {
            throw new IllegalArgumentException(
                    "Preparation time cannot be negative."
            );
        }

        if (recipe.getCookTime() < 0) {
            throw new IllegalArgumentException(
                    "Cooking time cannot be negative."
            );
        }

        if (recipe.getServings() <= 0) {
            throw new IllegalArgumentException(
                    "Servings must be greater than zero."
            );
        }

        recipe.setName(recipe.getName().trim());
    }
}
