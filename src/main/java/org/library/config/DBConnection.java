
package org.library.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/library_db";

    private static final String USER = "postgres";

    // Replace this with your actual PostgreSQL password
    private static final String PASSWORD = "root123";

    public static Connection getConnection() {

        try {
            Class.forName("org.postgresql.Driver");

            Connection connection = DriverManager.getConnection(
                    URL, USER, PASSWORD
            );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (ClassNotFoundException | SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();

            return null;
        }
    }
}

