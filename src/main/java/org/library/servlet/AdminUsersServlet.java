
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

@WebServlet("/admin-users")
public class AdminUsersServlet extends HttpServlet {

    private boolean isAdmin(HttpSession session) {
        String role = (String) session.getAttribute("role");
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !isAdmin(session)) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String keyword = request.getParameter("search");
        if (keyword == null) {
            keyword = "";
        }
        keyword = keyword.trim();

        List<Map<String, Object>> users = new ArrayList<>();

        String sql = """
                SELECT id, full_name, username, email, phone, role, created_at
                FROM users
                WHERE CAST(id AS TEXT) ILIKE ?
                   OR full_name ILIKE ?
                   OR username ILIKE ?
                   OR email ILIKE ?
                   OR phone ILIKE ?
                   OR role ILIKE ?
                ORDER BY id
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            for (int i = 1; i <= 6; i++) {
                ps.setString(i, searchValue);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> user = new HashMap<>();

                    user.put("id", rs.getLong("id"));
                    user.put("fullName", rs.getString("full_name"));
                    user.put("username", rs.getString("username"));
                    user.put("email", rs.getString("email"));
                    user.put("phone", rs.getString("phone"));
                    user.put("role", rs.getString("role"));
                    user.put("createdAt", rs.getTimestamp("created_at"));

                    users.add(user);
                }
            }

        } catch (Exception e) {
            throw new ServletException("Unable to load users.", e);
        }

        request.setAttribute("users", users);
        request.setAttribute("search", keyword);

        request.getRequestDispatcher("/admin-users.jsp")
                .forward(request, response);
    }
}