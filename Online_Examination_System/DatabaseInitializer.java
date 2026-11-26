package org.rgukt;

// FILE----3

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            // Users table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(50) PRIMARY KEY,
                    password_hash VARCHAR(100) NOT NULL
                )
            """);

            // Trains table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS trains (
                    train_number INTEGER PRIMARY KEY,
                    train_name VARCHAR(100) NOT NULL
                )
            """);

            // Reservations table
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reservations (
                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    pnr VARCHAR(30) UNIQUE,
                    passenger_name VARCHAR(100) NOT NULL,
                    train_number INTEGER NOT NULL REFERENCES trains(train_number),
                    train_name VARCHAR(100) NOT NULL,
                    class_type VARCHAR(30) NOT NULL,
                    journey_date DATE NOT NULL,
                    source VARCHAR(100) NOT NULL,
                    destination VARCHAR(100) NOT NULL
                )
            """);

            // Create default admin account
            String hashedPassword =
                    PasswordUtil.hashPassword("admin123");

            String userSQL = """
                INSERT INTO users(username, password_hash)
                VALUES (?, ?)
                ON CONFLICT(username) DO NOTHING
            """;

            try (PreparedStatement ps =
                         con.prepareStatement(userSQL)) {

                ps.setString(1, "admin");
                ps.setString(2, hashedPassword);

                ps.executeUpdate();
            }

            // Insert sample train records
            String trainSQL = """
                INSERT INTO trains(train_number, train_name)
                VALUES (?, ?)
                ON CONFLICT(train_number) DO NOTHING
            """;

            try (PreparedStatement ps =
                         con.prepareStatement(trainSQL)) {

                int[][] numbers = {
                        {12727}, {12728},
                        {12625}, {12626},
                        {12805}, {12806},
                        {12711}, {12712}
                };

                String[] names = {
                        "Godavari Express",
                        "Godavari Express",
                        "Kerala Express",
                        "Kerala Express",
                        "Janmabhoomi Express",
                        "Janmabhoomi Express",
                        "Pinakini Express",
                        "Pinakini Express"
                };

                for (int i = 0; i < numbers.length; i++) {

                    ps.setInt(1, numbers[i][0]);
                    ps.setString(2, names[i]);

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            System.out.println(
                    "Database initialized successfully!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Database initialization failed!"
            );

            e.printStackTrace();
        }
    }
}
