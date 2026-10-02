package com.aidan.recipemanager.repository;

import com.aidan.recipemanager.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeRepositoryInterface {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(int id);

    List<Recipe> findAll();

    Recipe update(Recipe recipe);

    void deleteById(int id);
}
