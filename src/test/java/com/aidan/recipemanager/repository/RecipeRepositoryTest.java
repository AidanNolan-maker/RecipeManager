package com.aidan.recipemanager.repository;

import com.aidan.recipemanager.database.TestDatabase;
import com.aidan.recipemanager.model.Category;
import com.aidan.recipemanager.model.Recipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeRepositoryTest {
    private Connection connection;
    private RecipeRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabase.createConnection();
        TestDatabase.initializeSchema(connection);

        repository = new RecipeRepository(TestDatabase::createConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void save_shouldPersistRecipeAndAssignId() {
        Category category = new Category(0, "Breakfast");

        // Insert the category directly so the recipe's foreign key is valid.
        try {
            connection.prepareStatement(
                    "INSERT INTO categories (name) VALUES ('Breakfast')"
            ).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        category.setId(1);

        Recipe recipe = new Recipe(
                0,
                "Pancakes",
                "Fluffy pancakes",
                category,
                10,
                15,
                4,
                "Mix ingredients and cook.",
                false,
                null,
                null
        );

        Recipe savedRecipe = repository.save(recipe);

        assertTrue(savedRecipe.getId() > 0);
        assertEquals("Pancakes", savedRecipe.getName());
        assertEquals("Fluffy pancakes", savedRecipe.getDescription());
        assertEquals(category.getId(), savedRecipe.getCategory().getId());
        assertEquals(10, savedRecipe.getPrepTime());
        assertEquals(15, savedRecipe.getCookTime());
        assertEquals(4, savedRecipe.getServings());
        assertEquals("Mix ingredients and cook.", savedRecipe.getInstructions());
        assertEquals(false, savedRecipe.isFavorite());
    }

    @Test
    void findById_shouldReturnRecipeWithCategory() {
        try {
            connection.prepareStatement(
                    "INSERT INTO categories (name) VALUES ('Breakfast')"
            ).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Category category = new Category(1, "Breakfast");

        Recipe recipe = new Recipe(
                0,
                "Pancakes",
                "Fluffy pancakes",
                category,
                10,
                15,
                4,
                "Mix ingredients and cook.",
                false,
                null,
                null
        );

        Recipe savedRecipe = repository.save(recipe);

        Optional<Recipe> result = repository.findById(savedRecipe.getId());

        assertTrue(result.isPresent());

        Recipe foundRecipe = result.get();

        assertEquals(savedRecipe.getId(), foundRecipe.getId());
        assertEquals("Pancakes", foundRecipe.getName());
        assertEquals("Fluffy pancakes", foundRecipe.getDescription());
        assertEquals(1, foundRecipe.getCategory().getId());
        assertEquals("Breakfast", foundRecipe.getCategory().getName());
        assertEquals(10, foundRecipe.getPrepTime());
        assertEquals(15, foundRecipe.getCookTime());
        assertEquals(4, foundRecipe.getServings());
        assertEquals("Mix ingredients and cook.", foundRecipe.getInstructions());
        assertEquals(false, foundRecipe.isFavorite());
    }

    @Test
    void findById_shouldReturnEmptyWhenRecipeDoesNotExist() {
        Optional<Recipe> result = repository.findById(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_shouldReturnRecipesOrderedByName() {
        try {
            connection.prepareStatement(
                    "INSERT INTO categories (name) VALUES ('Breakfast')"
            ).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Category category = new Category(1, "Breakfast");

        repository.save(new Recipe(
                0,
                "Pancakes",
                "Fluffy pancakes",
                category,
                10,
                15,
                4,
                "Mix ingredients and cook.",
                false,
                null,
                null
        ));

        repository.save(new Recipe(
                0,
                "French Toast",
                "Classic French toast",
                category,
                5,
                10,
                2,
                "Dip bread and cook.",
                false,
                null,
                null
        ));

        repository.save(new Recipe(
                0,
                "Waffles",
                "Crispy waffles",
                category,
                10,
                15,
                4,
                "Mix ingredients and cook in waffle iron.",
                true,
                null,
                null
        ));

        List<Recipe> recipes = repository.findAll();

        assertEquals(3, recipes.size());
        assertEquals("French Toast", recipes.get(0).getName());
        assertEquals("Pancakes", recipes.get(1).getName());
        assertEquals("Waffles", recipes.get(2).getName());

        assertEquals("Breakfast", recipes.get(0).getCategory().getName());
        assertEquals("Breakfast", recipes.get(1).getCategory().getName());
        assertEquals("Breakfast", recipes.get(2).getCategory().getName());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoRecipesExist() {
        List<Recipe> recipes = repository.findAll();

        assertTrue(recipes.isEmpty());
    }

    @Test
    void update_shouldChangeRecipeDetails() {
        try {
            connection.prepareStatement(
                    "INSERT INTO categories (name) VALUES ('Breakfast')"
            ).executeUpdate();

            connection.prepareStatement(
                    "INSERT INTO categories (name) VALUES ('Dessert')"
            ).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Category breakfast = new Category(1, "Breakfast");
        Category dessert = new Category(2, "Dessert");

        Recipe recipe = repository.save(new Recipe(
                0,
                "Pancakes",
                "Fluffy pancakes",
                breakfast,
                10,
                15,
                4,
                "Mix ingredients and cook.",
                false,
                null,
                null
        ));

        recipe.setName("Chocolate Pancakes");
        recipe.setDescription("Chocolate pancakes");
        recipe.setCategory(dessert);
        recipe.setPrepTime(15);
        recipe.setCookTime(20);
        recipe.setServings(6);
        recipe.setInstructions("Mix chocolate into the batter and cook.");
        recipe.setFavorite(true);

        Recipe updatedRecipe = repository.update(recipe);

        assertEquals(recipe.getId(), updatedRecipe.getId());
        assertEquals("Chocolate Pancakes", updatedRecipe.getName());
        assertEquals("Chocolate pancakes", updatedRecipe.getDescription());
        assertEquals(2, updatedRecipe.getCategory().getId());
        assertEquals(15, updatedRecipe.getPrepTime());
        assertEquals(20, updatedRecipe.getCookTime());
        assertEquals(6, updatedRecipe.getServings());
        assertEquals(
                "Mix chocolate into the batter and cook.",
                updatedRecipe.getInstructions()
        );
        assertTrue(updatedRecipe.isFavorite());

        Optional<Recipe> result = repository.findById(recipe.getId());

        assertTrue(result.isPresent());
        assertEquals("Chocolate Pancakes", result.get().getName());
        assertEquals(2, result.get().getCategory().getId());
        assertEquals("Dessert", result.get().getCategory().getName());
    }

    @Test
    void update_shouldThrowExceptionWhenRecipeDoesNotExist() {
        Recipe recipe = new Recipe(
                999,
                "Nonexistent Recipe",
                null,
                null,
                0,
                0,
                1,
                null,
                false,
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.update(recipe)
        );

        assertEquals(
                "Recipe with ID 999 does not exist.",
                exception.getMessage()
        );
    }

    @Test
    void deleteById_shouldDeleteExistingRecipe() {
        Recipe recipe = repository.save(new Recipe(
                0,
                "Pancakes",
                "Fluffy pancakes",
                null,
                10,
                15,
                4,
                "Mix ingredients and cook.",
                false,
                null,
                null
        ));

        repository.deleteById(recipe.getId());

        Optional<Recipe> result = repository.findById(recipe.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_shouldThrowExceptionWhenRecipeDoesNotExist() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.deleteById(999)
        );

        assertEquals(
                "Recipe with ID 999 does not exist.",
                exception.getMessage()
        );
    }
}
