package com.aidan.recipemanager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String URL = "jdbc:sqlite:recipe-manager.db";

    private Database(){
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
