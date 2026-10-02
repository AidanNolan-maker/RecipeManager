package com.aidan.recipemanager.service;

import com.aidan.recipemanager.model.Category;
import com.aidan.recipemanager.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeServiceTest {

    private FakeRecipeRepository repository;
    private RecipeService service;

    @BeforeEach
    void setUp() {
        repository = new FakeRecipeRepository();
        service = new RecipeService(repository);
    }

    @Test
    void createRecipe_shouldTrimNameAndSaveRecipe() {
        Recipe recipe = createValidRecipe();
        recipe.setName("  Pancakes  ");

        Recipe savedRecipe = service.createRecipe(recipe);

        assertEquals(1, savedRecipe.getId());
        assertEquals("Pancakes", savedRecipe.getName());
    }

    @Test
    void createRecipe_shouldAllowRecipeWithoutCategory() {
        Recipe recipe = createValidRecipe();
        recipe.setCategory(null);

        Recipe savedRecipe = service.createRecipe(recipe);

        assertEquals(1, savedRecipe.getId());
        assertTrue(savedRecipe.getCategory() == null);
    }

    @Test
    void createRecipe_shouldRejectNullRecipe() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createRecipe(null)
        );

        assertEquals(
                "Recipe cannot be null.",
                exception.getMessage()
        );
    }

    @Test
    void createRecipe_shouldRejectBlankName() {
        Recipe recipe = createValidRecipe();
        recipe.setName("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createRecipe(recipe)
        );

        assertEquals(
                "Recipe name cannot be blank.",
                exception.getMessage()
        );
    }

    @Test
    void createRecipe_shouldRejectNegativePrepTime() {
        Recipe recipe = createValidRecipe();
        recipe.setPrepTime(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createRecipe(recipe)
        );

        assertEquals(
                "Preparation time cannot be negative.",
                exception.getMessage()
        );
    }

    @Test
    void createRecipe_shouldRejectNegativeCookTime() {
        Recipe recipe = createValidRecipe();
        recipe.setCookTime(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createRecipe(recipe)
        );

        assertEquals(
                "Cooking time cannot be negative.",
                exception.getMessage()
        );
    }

    @Test
    void createRecipe_shouldRejectZeroServings() {
        Recipe recipe = createValidRecipe();
        recipe.setServings(0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createRecipe(recipe)
        );

        assertEquals(
                "Servings must be greater than zero.",
                exception.getMessage()
        );
    }

    @Test
    void createRecipe_shouldRejectNegativeServings() {
        Recipe recipe = createValidRecipe();
        recipe.setServings(-1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createRecipe(recipe)
        );

        assertEquals(
                "Servings must be greater than zero.",
                exception.getMessage()
        );
    }

    @Test
    void getRecipeById_shouldReturnRecipe() {
        Recipe savedRecipe = service.createRecipe(createValidRecipe());

        Optional<Recipe> result =
                service.getRecipeById(savedRecipe.getId());

        assertTrue(result.isPresent());
        assertEquals("Pancakes", result.get().getName());
    }

    @Test
    void getAllRecipes_shouldReturnAllRecipes() {
        service.createRecipe(createValidRecipe());

        Recipe secondRecipe = createValidRecipe();
        secondRecipe.setName("Waffles");

        service.createRecipe(secondRecipe);

        List<Recipe> recipes = service.getAllRecipes();

        assertEquals(2, recipes.size());
    }

    @Test
    void updateRecipe_shouldValidateAndUpdateRecipe() {
        Recipe recipe = service.createRecipe(createValidRecipe());

        recipe.setName("  French Toast  ");
        recipe.setPrepTime(5);
        recipe.setCookTime(10);
        recipe.setServings(2);

        Recipe updatedRecipe = service.updateRecipe(recipe);

        assertEquals("French Toast", updatedRecipe.getName());
        assertEquals(5, updatedRecipe.getPrepTime());
        assertEquals(10, updatedRecipe.getCookTime());
        assertEquals(2, updatedRecipe.getServings());
    }

    @Test
    void updateRecipe_shouldRejectInvalidRecipe() {
        Recipe recipe = createValidRecipe();
        recipe.setName("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateRecipe(recipe)
        );

        assertEquals(
                "Recipe name cannot be blank.",
                exception.getMessage()
        );
    }

    @Test
    void deleteRecipe_shouldDeleteRecipe() {
        Recipe recipe = service.createRecipe(createValidRecipe());

        service.deleteRecipe(recipe.getId());

        assertTrue(
                service.getRecipeById(recipe.getId()).isEmpty()
        );
    }

    private Recipe createValidRecipe() {
        return new Recipe(
                0,
                "Pancakes",
                "Fluffy pancakes",
                new Category(1, "Breakfast"),
                10,
                15,
                4,
                "Mix ingredients and cook.",
                false,
                null,
                null
        );
    }
}