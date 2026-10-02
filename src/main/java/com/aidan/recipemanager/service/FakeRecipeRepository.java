package com.aidan.recipemanager.service;

import com.aidan.recipemanager.model.Recipe;
import com.aidan.recipemanager.repository.RecipeRepositoryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeRecipeRepository implements RecipeRepositoryInterface {
    private final List<Recipe> recipes = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Recipe save(Recipe recipe) {
        recipe.setId(nextId++);
        recipes.add(recipe);
        return recipe;
    }

    @Override
    public Optional<Recipe> findById(int id) {
        return recipes.stream()
                .filter(recipe -> recipe.getId() == id)
                .findFirst();
    }

    @Override
    public List<Recipe> findAll() {
        return new ArrayList<>(recipes);
    }

    @Override
    public Recipe update(Recipe recipe) {
        Optional<Recipe> existing = findById(recipe.getId());

        if (existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Recipe with ID " + recipe.getId() + " does not exist."
            );
        }

        Recipe existingRecipe = existing.get();

        existingRecipe.setName(recipe.getName());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setCategory(recipe.getCategory());
        existingRecipe.setPrepTime(recipe.getPrepTime());
        existingRecipe.setCookTime(recipe.getCookTime());
        existingRecipe.setServings(recipe.getServings());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setFavorite(recipe.isFavorite());

        return existingRecipe;
    }

    @Override
    public void deleteById(int id) {
        boolean removed = recipes.removeIf(
                recipe -> recipe.getId() == id
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Recipe with ID " + id + " does not exist."
            );
        }
    }
}
