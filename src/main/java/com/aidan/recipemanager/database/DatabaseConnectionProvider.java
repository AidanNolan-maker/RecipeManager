package com.aidan.recipemanager.database;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface DatabaseConnectionProvider {
    Connection getConnection() throws SQLException;
}
