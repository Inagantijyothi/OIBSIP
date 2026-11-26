
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/my-reservations")
public class UserReservationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String role = session == null
                ? null
                : (String) session.getAttribute("role");

        if (role == null || !role.equalsIgnoreCase("USER")) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        List<Map<String, Object>> reservations = new ArrayList<>();

        String sql = """
                SELECT r.reservation_id,
                       r.book_id,
                       b.title,
                       b.author,
                       r.reservation_date,
                       r.status
                FROM reservations r
                JOIN books b ON r.book_id = b.book_id
                WHERE r.user_id = ?
                ORDER BY r.reservation_id DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> reservation = new HashMap<>();

                    reservation.put("reservationId",
                            rs.getLong("reservation_id"));
                    reservation.put("bookId",
                            rs.getLong("book_id"));
                    reservation.put("title",
                            rs.getString("title"));
                    reservation.put("author",
                            rs.getString("author"));
                    reservation.put("reservationDate",
                            rs.getTimestamp("reservation_date"));
                    reservation.put("status",
                            rs.getString("status"));

                    reservations.add(reservation);
                }
            }

        } catch (Exception e) {
            throw new ServletException("Unable to load reservations.", e);
        }

        request.setAttribute("reservations", reservations);
        request.getRequestDispatcher("/user-reservations.jsp")
                .forward(request, response);
    }
}
