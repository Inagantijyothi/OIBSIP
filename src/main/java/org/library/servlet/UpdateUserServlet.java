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

@WebServlet("/update-user")
public class UpdateUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        String role = session == null
                ? null
                : (String) session.getAttribute("role");

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String idValue = request.getParameter("userId");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (idValue == null || fullName == null ||
                email == null || phone == null ||
                fullName.trim().isEmpty() ||
                email.trim().isEmpty() ||
                phone.trim().isEmpty()) {

            response.sendRedirect(
                    request.getContextPath() + "/admin-users?message=invalid");
            return;
        }

        try {
            long userId = Long.parseLong(idValue);

            if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                response.sendRedirect(
                        request.getContextPath() + "/admin-users?message=invalid");
                return;
            }

            String sql = """
                    UPDATE users
                    SET full_name = ?, email = ?, phone = ?
                    WHERE id = ?
                    """;

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, fullName.trim());
                ps.setString(2, email.trim());
                ps.setString(3, phone.trim());
                ps.setLong(4, userId);

                int updated = ps.executeUpdate();

                if (updated > 0) {
                    response.sendRedirect(
                            request.getContextPath() + "/admin-users?message=updated");
                } else {
                    response.sendRedirect(
                            request.getContextPath() + "/admin-users?message=notfound");
                }
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(
                    request.getContextPath() + "/admin-users?message=invalid");
        } catch (Exception e) {
            throw new ServletException("Unable to update user details.", e);
        }
    }
}