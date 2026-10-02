package com.aidan.recipemanager.repository;

import com.aidan.recipemanager.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryInterface {
    Category save(Category category);

    Optional<Category> findById(int id);

    List<Category> findAll();

    Category update(Category category);

    void deleteById(int id);
}
