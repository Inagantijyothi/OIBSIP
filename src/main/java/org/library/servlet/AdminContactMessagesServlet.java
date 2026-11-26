
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

@WebServlet("/admin-contact-messages")
public class AdminContactMessagesServlet extends HttpServlet {

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

        String search = request.getParameter("search");

        List<Map<String, Object>> messages = new ArrayList<>();

        String sql =
                "SELECT c.message_id, c.user_id, " +
                        "COALESCE(u.full_name, 'Unknown User') AS full_name, " +
                        "c.subject, c.message, c.sent_at, c.status " +
                        "FROM contact_messages c " +
                        "LEFT JOIN users u ON c.user_id = u.id ";

        if (search != null && !search.trim().isEmpty()) {
            sql += "WHERE LOWER(c.subject) LIKE ? " +
                    "OR LOWER(c.message) LIKE ? " +
                    "OR LOWER(COALESCE(u.full_name, '')) LIKE ? ";
        }

        sql += "ORDER BY c.sent_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (search != null && !search.trim().isEmpty()) {
                String keyword = "%" + search.trim().toLowerCase() + "%";

                ps.setString(1, keyword);
                ps.setString(2, keyword);
                ps.setString(3, keyword);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Map<String, Object> row = new HashMap<>();

                    row.put("messageId", rs.getLong("message_id"));
                    row.put("userId", rs.getObject("user_id"));
                    row.put("fullName", rs.getString("full_name"));
                    row.put("subject", rs.getString("subject"));
                    row.put("message", rs.getString("message"));
                    row.put("sentAt", rs.getTimestamp("sent_at"));
                    row.put("status", rs.getString("status"));

                    messages.add(row);
                }
            }

            request.setAttribute("messages", messages);
            request.setAttribute("search", search);

            request.getRequestDispatcher(
                            "/admin-contact-messages.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load contact messages.", e);
        }
    }
}
