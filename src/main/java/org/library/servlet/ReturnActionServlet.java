
package org.library.servlet;

import org.library.config.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/return-action")
public class ReturnActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String action = request.getParameter("action");

        if (!"approve".equals(action)) {
            response.sendRedirect(request.getContextPath()
                    + "/admin-return-requests");
            return;
        }

        try {
            long issueId = Long.parseLong(request.getParameter("issueId"));

            approveReturn(issueId);

            response.sendRedirect(request.getContextPath()
                    + "/admin-return-requests?message=success");

        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect(request.getContextPath()
                    + "/admin-return-requests?message=failed");
        }
    }

    private void approveReturn(long issueId) throws SQLException {

        String selectSql = """
                SELECT book_id, due_date, status
                FROM issued_books
                WHERE issue_id = ?
                FOR UPDATE
                """;

        String updateIssueSql = """
                UPDATE issued_books
                SET status = 'RETURNED',
                    return_date = CURRENT_DATE
                WHERE issue_id = ?
                AND status = 'RETURN_REQUESTED'
                """;

        String updateBookSql = """
                UPDATE books
                SET available_quantity = available_quantity + 1
                WHERE book_id = ?
                """;

        String insertFineSql = """
                INSERT INTO fines (issue_id, fine_amount, payment_status)
                VALUES (?, ?, 'UNPAID')
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                long bookId;
                java.sql.Date dueDate;

                // Lock and check the return request
                try (PreparedStatement ps =
                             con.prepareStatement(selectSql)) {

                    ps.setLong(1, issueId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Return request not found.");
                        }

                        if (!"RETURN_REQUESTED".equals(
                                rs.getString("status"))) {
                            throw new SQLException(
                                    "This return request was already processed.");
                        }

                        bookId = rs.getLong("book_id");
                        dueDate = rs.getDate("due_date");
                    }
                }

                // Mark book as returned
                try (PreparedStatement ps =
                             con.prepareStatement(updateIssueSql)) {

                    ps.setLong(1, issueId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to update return status.");
                    }
                }

                // Increase available book quantity
                try (PreparedStatement ps =
                             con.prepareStatement(updateBookSql)) {

                    ps.setLong(1, bookId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to update book quantity.");
                    }
                }

                // Calculate overdue fine: ₹5 per day
                String daysSql = """
                        SELECT GREATEST(CURRENT_DATE - ?::date, 0)
                        """;

                int overdueDays;

                try (PreparedStatement ps =
                             con.prepareStatement(daysSql)) {

                    ps.setDate(1, dueDate);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        overdueDays = rs.getInt(1);
                    }
                }

                double fineAmount = overdueDays * 5.0;

                // Insert fine only when overdue
                if (fineAmount > 0) {

                    try (PreparedStatement ps =
                                 con.prepareStatement(insertFineSql)) {

                        ps.setLong(1, issueId);
                        ps.setDouble(2, fineAmount);
                        ps.executeUpdate();
                    }
                }

                con.commit();

            } catch (Exception e) {
                con.rollback();

                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }

                throw new SQLException(
                        "Return approval failed.", e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
