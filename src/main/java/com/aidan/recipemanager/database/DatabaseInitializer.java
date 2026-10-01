package com.aidan.recipemanager.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {
        String createCategoriesTable = """
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
                """;

        String createRecipesTable = """
                CREATE TABLE IF NOT EXISTS recipes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    category_id INTEGER,
                    prep_time INTEGER NOT NULL DEFAULT 0,
                    cook_time INTEGER NOT NULL DEFAULT 0,
                    servings INTEGER NOT NULL DEFAULT 1,
                    instructions TEXT,
                    favorite INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_id) REFERENCES categories(id)
                )
        """;

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createCategoriesTable);
            statement.execute(createRecipesTable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database.",e);
        }
    }
}
