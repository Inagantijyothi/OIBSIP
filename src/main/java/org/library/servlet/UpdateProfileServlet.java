
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

@WebServlet("/update-profile")
public class UpdateProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"USER".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (fullName == null || email == null || phone == null ||
                fullName.isBlank() || email.isBlank() || phone.isBlank()) {

            response.sendRedirect(
                    request.getContextPath() + "/my-profile?message=invalid");
            return;
        }

        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? " +
                "WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName.trim());
            ps.setString(2, email.trim());
            ps.setString(3, phone.trim());
            ps.setLong(4, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                session.setAttribute("username", fullName.trim());

                response.sendRedirect(
                        request.getContextPath() + "/my-profile?message=success");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/my-profile?message=failed");
            }

        } catch (Exception e) {
            throw new ServletException("Unable to update profile.", e);
        }
    }
}
