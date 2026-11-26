
package org.library.servlet;

import org.library.config.DBConnection;
import org.library.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("full_name");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        String redirect = request.getContextPath() + "/register.html";

        if (fullName == null || username == null ||
                email == null || password == null ||
                confirmPassword == null) {

            response.sendRedirect(redirect + "?error=invalid");
            return;
        }

        fullName = fullName.trim();
        username = username.trim();
        email = email.trim();
        phone = phone == null ? "" : phone.trim();

        if (fullName.isEmpty() || username.isEmpty() ||
                email.isEmpty() || password.isEmpty()) {

            response.sendRedirect(redirect + "?error=empty");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            response.sendRedirect(redirect + "?error=email");
            return;
        }

        if (password.length() < 6) {
            response.sendRedirect(redirect + "?error=password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            response.sendRedirect(redirect + "?error=mismatch");
            return;
        }

        String checkSql =
                "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?) " +
                        "OR LOWER(email) = LOWER(?)";

        String insertSql =
                "INSERT INTO users " +
                        "(full_name, username, email, password, phone, role, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, 'USER', CURRENT_TIMESTAMP)";

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement check = con.prepareStatement(checkSql)) {

                check.setString(1, username);
                check.setString(2, email);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next() && rs.getInt(1) > 0) {
                        response.sendRedirect(redirect + "?error=exists");
                        return;
                    }
                }
            }

            String hashedPassword = PasswordUtil.hashPassword(password);

            try (PreparedStatement ps = con.prepareStatement(insertSql)) {

                ps.setString(1, fullName);
                ps.setString(2, username);
                ps.setString(3, email);
                ps.setString(4, hashedPassword);
                ps.setString(5, phone);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    response.sendRedirect(
                            request.getContextPath() +
                                    "/login.html?registered=success"
                    );
                } else {
                    response.sendRedirect(redirect + "?error=failed");
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(redirect + "?error=failed");
        }
    }
}
