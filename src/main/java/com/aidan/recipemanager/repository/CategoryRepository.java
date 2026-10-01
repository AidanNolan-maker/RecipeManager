package com.aidan.recipemanager.repository;

import com.aidan.recipemanager.database.Database;
import com.aidan.recipemanager.database.DatabaseConnectionProvider;
import com.aidan.recipemanager.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository {
    private final DatabaseConnectionProvider connectionProvider;

    public CategoryRepository() {
        this(Database::getConnection);
    }

    public CategoryRepository(DatabaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Category save(Category category) {
        String sql = """
               INSERT INTO categories (name)
               VALUES (?)
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            statement.setString(1, category.getName());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                    return category;
                }
            }

            throw new SQLException("Failed to retrieve generated category ID.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save category.", e);
        }
    }

    public Optional<Category> findById(int id) {
        String sql = """
                SELECT id, name
                FROM categories
                WHERE id = ?
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Category category = new Category(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );

                    return Optional.of(category);
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category by ID.", e);
        }
    }

    public List<Category> findAll() {
        String sql = """
                SELECT id, name
                FROM categories
                ORDER BY name
                """;

        List<Category> categories = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Category category = new Category(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );

                categories.add(category);
            }

            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find categories.", e);
        }
    }

    public Category update(Category category) {
        String sql = """
                UPDATE categories
                SET name = ?
                WHERE id = ?
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getName());
            statement.setInt(2, category.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new IllegalArgumentException(
                        "Category with ID " + category.getId() + " does not exist."
                );
            }

            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update category.", e);
        }
    }

    public void deleteById(int id) {
        String sql = """
                DELETE FROM categories
                WHERE id = ?
                """;

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new IllegalArgumentException(
                        "Category with ID " + id + " does not exist."
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete category.", e);
        }
    }
}
