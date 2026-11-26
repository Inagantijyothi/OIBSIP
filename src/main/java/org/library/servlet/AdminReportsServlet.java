
package org.library.servlet;

import org.library.config.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/admin-reports")
public class AdminReportsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"ADMIN".equalsIgnoreCase(
                        (String) session.getAttribute("role"))) {
            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // 1. BOOK STATISTICS
            Map<String, Object> bookStats = new HashMap<>();

            String bookSql =
                    "SELECT " +
                            "COALESCE(SUM(total_quantity), 0) AS total_books, " +
                            "COALESCE(SUM(available_quantity), 0) AS available_books, " +
                            "COALESCE(SUM(total_quantity - available_quantity), 0) AS unavailable_books " +
                            "FROM books";

            try (PreparedStatement ps = con.prepareStatement(bookSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    bookStats.put("totalBooks", rs.getInt("total_books"));
                    bookStats.put("availableBooks", rs.getInt("available_books"));
                    bookStats.put("unavailableBooks", rs.getInt("unavailable_books"));
                }
            }

            request.setAttribute("bookStats", bookStats);


            // 2. USER STATISTICS
            Map<String, Object> userStats = new HashMap<>();

            String userSql =
                    "SELECT " +
                            "COUNT(*) FILTER (WHERE UPPER(role) = 'USER') AS total_users, " +
                            "COUNT(*) FILTER (WHERE UPPER(role) = 'ADMIN') AS total_admins " +
                            "FROM users";

            try (PreparedStatement ps = con.prepareStatement(userSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    userStats.put("totalUsers", rs.getInt("total_users"));
                    userStats.put("totalAdmins", rs.getInt("total_admins"));
                }
            }

            request.setAttribute("userStats", userStats);


            // 3. ISSUE AND RETURN STATISTICS
            Map<String, Object> issueStats = new HashMap<>();

            String issueSql =
                    "SELECT " +
                            "COUNT(*) FILTER (WHERE status IN ('ISSUED', 'RETURN_REQUESTED')) AS issued_books, " +
                            "COUNT(*) FILTER (WHERE status = 'RETURNED') AS returned_books " +
                            "FROM issued_books";

            try (PreparedStatement ps = con.prepareStatement(issueSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    issueStats.put("issuedBooks", rs.getInt("issued_books"));
                    issueStats.put("returnedBooks", rs.getInt("returned_books"));
                }
            }

            request.setAttribute("issueStats", issueStats);


            // 4. FINE STATISTICS
            Map<String, Object> fineStats = new HashMap<>();

            String fineSql =
                    "SELECT " +
                            "COALESCE(SUM(fine_amount), 0) AS total_fines, " +
                            "COALESCE(SUM(fine_amount) FILTER (WHERE payment_status = 'PAID'), 0) AS paid_fines, " +
                            "COALESCE(SUM(fine_amount) FILTER (WHERE payment_status = 'UNPAID'), 0) AS unpaid_fines " +
                            "FROM fines";

            try (PreparedStatement ps = con.prepareStatement(fineSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    fineStats.put("totalFines", rs.getBigDecimal("total_fines"));
                    fineStats.put("paidFines", rs.getBigDecimal("paid_fines"));
                    fineStats.put("unpaidFines", rs.getBigDecimal("unpaid_fines"));
                }
            }

            request.setAttribute("fineStats", fineStats);


            // 5. RESERVATION STATISTICS
            Map<String, Object> reservationStats = new HashMap<>();

            String reservationSql =
                    "SELECT " +
                            "COUNT(*) FILTER (WHERE status = 'PENDING') AS pending_reservations, " +
                            "COUNT(*) FILTER (WHERE status = 'APPROVED') AS approved_reservations, " +
                            "COUNT(*) FILTER (WHERE status = 'REJECTED') AS rejected_reservations, " +
                            "COUNT(*) FILTER (WHERE status = 'CANCELLED') AS cancelled_reservations " +
                            "FROM reservations";

            try (PreparedStatement ps = con.prepareStatement(reservationSql);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    reservationStats.put("pendingReservations",
                            rs.getInt("pending_reservations"));
                    reservationStats.put("approvedReservations",
                            rs.getInt("approved_reservations"));
                    reservationStats.put("rejectedReservations",
                            rs.getInt("rejected_reservations"));
                    reservationStats.put("cancelledReservations",
                            rs.getInt("cancelled_reservations"));
                }
            }

            request.setAttribute("reservationStats", reservationStats);


            // 6. CATEGORY-WISE BOOK STATISTICS
            List<Map<String, Object>> categoryStats = new ArrayList<>();

            String categorySql =
                    "SELECT c.category_name, " +
                            "COUNT(b.book_id) AS book_titles, " +
                            "COALESCE(SUM(b.total_quantity), 0) AS total_copies, " +
                            "COALESCE(SUM(b.available_quantity), 0) AS available_copies " +
                            "FROM categories c " +
                            "LEFT JOIN books b ON c.category_id = b.category_id " +
                            "GROUP BY c.category_id, c.category_name " +
                            "ORDER BY c.category_name";

            try (PreparedStatement ps = con.prepareStatement(categorySql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> category = new HashMap<>();

                    category.put("categoryName",
                            rs.getString("category_name"));
                    category.put("bookTitles",
                            rs.getInt("book_titles"));
                    category.put("totalCopies",
                            rs.getInt("total_copies"));
                    category.put("availableCopies",
                            rs.getInt("available_copies"));

                    categoryStats.add(category);
                }
            }

            request.setAttribute("categoryStats", categoryStats);

            request.getRequestDispatcher("/admin-reports.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to generate admin reports.", e);
        }
    }
}
