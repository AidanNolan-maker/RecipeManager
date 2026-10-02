package com.aidan.recipemanager.service;

import com.aidan.recipemanager.model.Category;
import com.aidan.recipemanager.repository.CategoryRepository;
import com.aidan.recipemanager.repository.CategoryRepositoryInterface;

import java.util.List;
import java.util.Optional;

public class CategoryService {
    private final CategoryRepositoryInterface categoryRepository;

    public CategoryService() {
        this(new CategoryRepository());
    }

    public CategoryService(CategoryRepositoryInterface categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name) {
        validateName(name);

        Category category = new Category();
        category.setName(name.trim());

        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }

        validateName(category.getName());

        category.setName(category.getName().trim());

        return categoryRepository.update(category);
    }

    public void deleteCategory(int id) {
        categoryRepository.deleteById(id);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Category name cannot be blank."
            );
        }
    }
}
