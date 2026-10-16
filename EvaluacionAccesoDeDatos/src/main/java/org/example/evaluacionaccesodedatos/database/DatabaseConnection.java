package org.example.evaluacionaccesodedatos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/papeleria_campus";
    private static final String USER = "josh";
    private static final String PASSWORD = "josh1220";

    private DatabaseConnection() {

    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
