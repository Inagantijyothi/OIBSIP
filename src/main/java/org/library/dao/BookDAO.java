
package org.library.dao;

import org.library.config.DBConnection;
import org.library.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Display all books, including books with zero available quantity
    public List<Book> getAvailableBooks() {
        return getAvailableBooks("");
    }

    // Search books by title, author or category
    public List<Book> getAvailableBooks(String keyword) {

        List<Book> books = new ArrayList<>();

        String sql = """
                SELECT
                    b.book_id,
                    b.title,
                    b.author,
                    b.isbn,
                    c.category_name,
                    b.total_quantity,
                    b.available_quantity
                FROM books b
                LEFT JOIN categories c
                    ON b.category_id = c.category_id
                WHERE
                    b.title ILIKE ?
                    OR b.author ILIKE ?
                    OR c.category_name ILIKE ?
                ORDER BY b.book_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            String searchValue = "%" +
                    (keyword == null ? "" : keyword.trim()) + "%";

            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    Book book = new Book(
                            rs.getLong("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getString("category_name"),
                            rs.getInt("total_quantity"),
                            rs.getInt("available_quantity")
                    );

                    books.add(book);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }
}
