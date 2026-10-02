package com.aidan.recipemanager.repository;

import com.aidan.recipemanager.database.Database;
import com.aidan.recipemanager.database.DatabaseConnectionProvider;
import com.aidan.recipemanager.model.Category;
import com.aidan.recipemanager.model.Recipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class RecipeRepository implements RecipeRepositoryInterface {
    private static final String BASE_SELECT = """
            SELECT
                r.id,
                r.name,
                r.description,
                r.category_id,
                c.name AS category_name,
                r.prep_time,
                r.cook_time,
                r.servings,
                r.instructions,
                r.favorite,
                r.created_at,
                r.updated_at
            FROM recipes r
            LEFT JOIN categories c ON r.category_id = c.id
            """;

    private final DatabaseConnectionProvider connectionProvider;

    public RecipeRepository() {
        this(Database::getConnection);
    }

    public RecipeRepository(DatabaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Recipe save(Recipe recipe) {
        String sql = """
                INSERT INTO recipes (
                    name,
                    description,
                    category_id,
                    prep_time,
                    cook_time,
                    servings,
                    instructions,
                    favorite
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            statement.setString(1, recipe.getName());
            statement.setString(2, recipe.getDescription());

            if (recipe.getCategory() != null) {
                statement.setInt(3, recipe.getCategory().getId());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }

            statement.setInt(4, recipe.getPrepTime());
            statement.setInt(5, recipe.getCookTime());
            statement.setInt(6, recipe.getServings());
            statement.setString(7, recipe.getInstructions());
            statement.setBoolean(8, recipe.isFavorite());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    recipe.setId(generatedKeys.getInt(1));
                    return recipe;
                }
            }

            throw new SQLException("Failed to retrieve generated recipe ID.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save recipe.", e);
        }
    }

    public Optional<Recipe> findById(int id) {
        String sql = BASE_SELECT + """
                WHERE r.id = ?
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find recipe by ID.", e);
        }
    }

    public List<Recipe> findAll() {
        String sql = BASE_SELECT + """
                ORDER BY r.name
                """;

        List<Recipe> recipes = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                recipes.add(mapRow(resultSet));
            }

            return recipes;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find recipes.", e);
        }
    }

    public Recipe update(Recipe recipe) {
        String sql = """
                UPDATE recipes
                SET
                    name = ?,
                    description = ?,
                    category_id = ?,
                    prep_time = ?,
                    cook_time = ?,
                    servings = ?,
                    instructions = ?,
                    favorite = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, recipe.getName());
            statement.setString(2, recipe.getDescription());

            if (recipe.getCategory() != null) {
                statement.setInt(3, recipe.getCategory().getId());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }

            statement.setInt(4, recipe.getPrepTime());
            statement.setInt(5, recipe.getCookTime());
            statement.setInt(6, recipe.getServings());
            statement.setString(7, recipe.getInstructions());
            statement.setBoolean(8, recipe.isFavorite());
            statement.setInt(9, recipe.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new IllegalArgumentException(
                        "Recipe with ID " + recipe.getId() + " does not exist."
                );
            }

            return recipe;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update recipe.", e);
        }
    }

    public void deleteById(int id) {
        String sql = """
                DELETE FROM recipes
                WHERE id = ?
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new IllegalArgumentException(
                        "Recipe with ID " + id + " does not exist."
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete recipe.", e);
        }
    }

    private Recipe mapRow(ResultSet resultSet) throws SQLException {
        Category category = null;

        int categoryId = resultSet.getInt("category_id");

        if (!resultSet.wasNull()) {
            category = new Category(
                    categoryId,
                    resultSet.getString("category_name")
            );
        }

        return new Recipe(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                category,
                resultSet.getInt("prep_time"),
                resultSet.getInt("cook_time"),
                resultSet.getInt("servings"),
                resultSet.getString("instructions"),
                resultSet.getBoolean("favorite"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
