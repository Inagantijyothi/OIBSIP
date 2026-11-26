
package org.library.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.library.config.DBConnection;
import org.library.util.PasswordUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null
                || email.isBlank() || password.isBlank()) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html?error=empty"
            );
            return;
        }

        String sql = """
                SELECT id, full_name, email, password, role
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = DBConnection.getConnection()) {

            if (connection == null) {
                throw new ServletException("Database connection failed");
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, email.trim());

                try (ResultSet rs = statement.executeQuery()) {

                    if (rs.next()) {

                        String storedPassword = rs.getString("password");

                        boolean validPassword =
                                PasswordUtil.verifyPassword(
                                        password,
                                        storedPassword
                                );

                        if (validPassword) {

                            String role = rs.getString("role");

                            HttpSession session = request.getSession(true);

                            session.setAttribute("userId", rs.getLong("id"));
                            session.setAttribute(
                                    "username", rs.getString("full_name")
                            );
                            session.setAttribute(
                                    "email", rs.getString("email")
                            );
                            session.setAttribute("role", role);

                            session.setMaxInactiveInterval(30 * 60);

                            if ("ADMIN".equalsIgnoreCase(role)) {

                                response.sendRedirect(
                                        request.getContextPath()
                                                + "/admin-dashboard.jsp"
                                );

                            } else if ("USER".equalsIgnoreCase(role)) {

                                response.sendRedirect(
                                        request.getContextPath()
                                                + "/user-dashboard.jsp"
                                );

                            } else {

                                session.invalidate();

                                response.sendRedirect(
                                        request.getContextPath()
                                                + "/login.html?error=role"
                                );
                            }

                            return;
                        }
                    }

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/login.html?error=invalid"
                    );
                }
            }

        } catch (Exception e) {

            log("Login error", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to process login. Please try again."
            );
        }
    }
}
