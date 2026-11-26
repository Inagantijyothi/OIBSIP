
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

@WebServlet("/cancel-reservation")
public class CancelReservationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
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

        try {
            long reservationId = Long.parseLong(
                    request.getParameter("reservationId"));

            String sql = """
                    UPDATE reservations
                    SET status = 'CANCELLED'
                    WHERE reservation_id = ?
                      AND user_id = ?
                      AND status = 'PENDING'
                    """;

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, reservationId);
                ps.setLong(2, userId);

                int updated = ps.executeUpdate();

                if (updated > 0) {
                    response.sendRedirect(
                            request.getContextPath()
                                    + "/my-reservations?message=cancelled");
                } else {
                    response.sendRedirect(
                            request.getContextPath()
                                    + "/my-reservations?message=failed");
                }
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/my-reservations?message=failed");
        } catch (Exception e) {
            throw new ServletException("Unable to cancel reservation.", e);
        }
    }
}
