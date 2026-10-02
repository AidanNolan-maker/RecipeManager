package com.aidan.recipemanager.service;

import com.aidan.recipemanager.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryServiceTest {

    private FakeCategoryRepository repository;
    private CategoryService service;

    @BeforeEach
    void setUp() {
        repository = new FakeCategoryRepository();
        service = new CategoryService(repository);
    }

    @Test
    void createCategory_shouldTrimNameAndSaveCategory() {
        Category category = service.createCategory("  Breakfast  ");

        assertEquals(1, category.getId());
        assertEquals("Breakfast", category.getName());
    }

    @Test
    void createCategory_shouldRejectNullName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createCategory(null)
        );

        assertEquals(
                "Category name cannot be blank.",
                exception.getMessage()
        );
    }

    @Test
    void createCategory_shouldRejectBlankName() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createCategory("   ")
        );

        assertEquals(
                "Category name cannot be blank.",
                exception.getMessage()
        );
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        Category saved = service.createCategory("Breakfast");

        Optional<Category> result =
                service.getCategoryById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Breakfast", result.get().getName());
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        service.createCategory("Breakfast");
        service.createCategory("Dinner");

        List<Category> categories = service.getAllCategories();

        assertEquals(2, categories.size());
    }

    @Test
    void updateCategory_shouldTrimNameAndUpdateCategory() {
        Category category = service.createCategory("Breakfast");

        category.setName("  Brunch  ");

        Category updated = service.updateCategory(category);

        assertEquals("Brunch", updated.getName());
    }

    @Test
    void updateCategory_shouldRejectNullCategory() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateCategory(null)
        );

        assertEquals(
                "Category cannot be null.",
                exception.getMessage()
        );
    }

    @Test
    void updateCategory_shouldRejectBlankName() {
        Category category = service.createCategory("Breakfast");
        category.setName("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateCategory(category)
        );

        assertEquals(
                "Category name cannot be blank.",
                exception.getMessage()
        );
    }

    @Test
    void deleteCategory_shouldDeleteCategory() {
        Category category = service.createCategory("Breakfast");

        service.deleteCategory(category.getId());

        assertTrue(
                service.getCategoryById(category.getId()).isEmpty()
        );
    }
}