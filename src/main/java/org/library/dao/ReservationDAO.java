
package org.library.dao;

import org.library.config.DBConnection;
import org.library.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // USER: Request a book
    public boolean requestBook(long userId, long bookId) {

        String sql = """
                INSERT INTO reservations
                (user_id, book_id, reservation_date, status)
                VALUES (?, ?, CURRENT_TIMESTAMP, 'PENDING')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setLong(2, bookId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ADMIN: Get all reservations
    public List<Reservation> getAllReservations() {

        List<Reservation> reservations = new ArrayList<>();

        String sql = """
                SELECT
                    r.reservation_id,
                    r.user_id,
                    u.full_name,
                    r.book_id,
                    b.title,
                    r.reservation_date,
                    r.status
                FROM reservations r
                JOIN users u ON r.user_id = u.id
                JOIN books b ON r.book_id = b.book_id
                ORDER BY r.reservation_id DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Reservation reservation = new Reservation(
                        resultSet.getLong("reservation_id"),
                        resultSet.getLong("user_id"),
                        resultSet.getString("full_name"),
                        resultSet.getLong("book_id"),
                        resultSet.getString("title"),
                        resultSet.getTimestamp("reservation_date"),
                        resultSet.getString("status")
                );

                reservations.add(reservation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    // ADMIN: Approve reservation and issue book
    public boolean approveReservation(long reservationId) {

        String reservationSQL = """
                SELECT user_id, book_id, status
                FROM reservations
                WHERE reservation_id = ?
                FOR UPDATE
                """;

        String updateBookSQL = """
                UPDATE books
                SET available_quantity = available_quantity - 1
                WHERE book_id = ?
                AND available_quantity > 0
                """;

        String issueSQL = """
                INSERT INTO issued_books
                (user_id, book_id, issue_date, due_date,
                 return_date, status)
                VALUES (?, ?, CURRENT_DATE, CURRENT_DATE + 14,
                        NULL, 'ISSUED')
                """;

        String updateReservationSQL = """
                UPDATE reservations
                SET status = 'APPROVED'
                WHERE reservation_id = ?
                AND status = 'PENDING'
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                long userId;
                long bookId;

                // 1. Lock and check reservation
                try (PreparedStatement statement =
                             connection.prepareStatement(reservationSQL)) {

                    statement.setLong(1, reservationId);

                    try (ResultSet rs = statement.executeQuery()) {

                        if (!rs.next() ||
                                !"PENDING".equalsIgnoreCase(
                                        rs.getString("status"))) {

                            connection.rollback();
                            return false;
                        }

                        userId = rs.getLong("user_id");
                        bookId = rs.getLong("book_id");
                    }
                }

                // 2. Decrease available quantity
                try (PreparedStatement statement =
                             connection.prepareStatement(updateBookSQL)) {

                    statement.setLong(1, bookId);

                    if (statement.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                // 3. Create issue record
                try (PreparedStatement statement =
                             connection.prepareStatement(issueSQL)) {

                    statement.setLong(1, userId);
                    statement.setLong(2, bookId);

                    statement.executeUpdate();
                }

                // 4. Update reservation status
                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     updateReservationSQL)) {

                    statement.setLong(1, reservationId);

                    if (statement.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                // 5. Save all changes together
                connection.commit();

                return true;

            } catch (SQLException e) {

                connection.rollback();
                e.printStackTrace();
                return false;

            } finally {

                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }

    // ADMIN: Reject reservation
    public boolean rejectReservation(long reservationId) {

        String sql = """
                UPDATE reservations
                SET status = 'REJECTED'
                WHERE reservation_id = ?
                AND status = 'PENDING'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, reservationId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
}
