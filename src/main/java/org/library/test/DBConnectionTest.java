

package org.library.test;

import org.library.config.DBConnection;
import java.sql.Connection;

public class DBConnectionTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            if (connection != null) {
                System.out.println("Library Web Application is connected to PostgreSQL!");
            } else {
                System.out.println("Please check your database credentials.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

