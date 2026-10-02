package com.aidan.recipemanager.service;

import com.aidan.recipemanager.model.Category;
import com.aidan.recipemanager.repository.CategoryRepositoryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeCategoryRepository implements CategoryRepositoryInterface {
    private final List<Category> categories = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Category save(Category category) {
        category.setId(nextId++);
        categories.add(category);
        return category;
    }

    @Override
    public Optional<Category> findById(int id) {
        return categories.stream()
                .filter(category -> category.getId() == id)
                .findFirst();
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categories);
    }

    @Override
    public Category update(Category category) {
        Optional<Category> existing = findById(category.getId());

        if (existing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Category with ID " + category.getId() + " does not exist."
            );
        }

        existing.get().setName(category.getName());

        return existing.get();
    }

    @Override
    public void deleteById(int id) {
        boolean removed = categories.removeIf(
                category -> category.getId() == id
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Category with ID " + id + " does not exist."
            );
        }
    }
}
