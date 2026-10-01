package com.aidan.recipemanager.repository;

import com.aidan.recipemanager.database.TestDatabase;
import com.aidan.recipemanager.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CategoryRepositoryTest {
    private Connection connection;
    private CategoryRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabase.createConnection();
        TestDatabase.initializeSchema(connection);

        repository = new CategoryRepository(TestDatabase::createConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void save_shouldPersistCategoryAndAssignId() {
        Category category = new Category(0, "Dessert");

        Category savedCategory = repository.save(category);

        assertTrue(savedCategory.getId() > 0);
        assertEquals("Dessert", savedCategory.getName());
    }

    @Test
    void findById_shouldReturnCategoryWhenItExists() {
        Category category = new Category(0, "Dessert");
        Category savedCategory = repository.save(category);

        Optional<Category> result = repository.findById(savedCategory.getId());

        assertTrue(result.isPresent());
        assertEquals(savedCategory.getId(), result.get().getId());
        assertEquals("Dessert", result.get().getName());
    }

    @Test
    void findById_shouldReturnEmptyWhenCategoryDoesNotExist() {
        Optional<Category> result = repository.findById(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnCategoriesOrderedByName() {
        repository.save(new Category(0, "Dessert"));
        repository.save(new Category(0, "Breakfast"));
        repository.save(new Category(0, "Dinner"));

        List<Category> categories = repository.findAll();

        assertEquals(3, categories.size());
        assertEquals("Breakfast", categories.get(0).getName());
        assertEquals("Dessert", categories.get(1).getName());
        assertEquals("Dinner", categories.get(2).getName());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoCategoriesExist() {
        List<Category> categories = repository.findAll();

        assertTrue(categories.isEmpty());
    }

    @Test
    void update_shouldChangeCategoryName() {
        Category category = repository.save(new Category(0, "Dessert"));

        category.setName("Baking");

        Category updatedCategory = repository.update(category);

        assertEquals(category.getId(), updatedCategory.getId());
        assertEquals("Baking", updatedCategory.getName());

        Optional<Category> result = repository.findById(category.getId());

        assertTrue(result.isPresent());
        assertEquals("Baking", result.get().getName());
    }

    @Test
    void update_shouldThrowExceptionWhenCategoryDoesNotExist() {
        Category category = new Category(999, "Nonexistent");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.update(category)
        );

        assertEquals(
                "Category with ID 999 does not exist.",
                exception.getMessage()
        );
    }

    @Test
    void deleteById_shouldDeleteExistingCategory() {
        Category category  = repository.save(new Category(0, "Dessert"));

        repository.deleteById(category.getId());

        Optional<Category> result = repository.findById(category.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_shouldThrowExceptionWhenCategoryDoesNotExist() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.deleteById(999)
        );

        assertEquals(
                "Category with ID 999 does not exist.",
                exception.getMessage()
        );
    }
}
