
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

@WebServlet("/admin-return-requests")
public class AdminReturnRequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        String sql = """
                SELECT
                    i.issue_id,
                    u.full_name,
                    b.title,
                    b.author,
                    i.issue_date,
                    i.due_date,
                    i.status
                FROM issued_books i
                JOIN users u ON i.user_id = u.id
                JOIN books b ON i.book_id = b.book_id
                WHERE i.status = 'RETURN_REQUESTED'
                ORDER BY i.issue_id DESC
                """;

        List<Map<String, Object>> returnRequests = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                row.put("issueId", rs.getLong("issue_id"));
                row.put("studentName", rs.getString("full_name"));
                row.put("title", rs.getString("title"));
                row.put("author", rs.getString("author"));
                row.put("issueDate", rs.getDate("issue_date"));
                row.put("dueDate", rs.getDate("due_date"));
                row.put("status", rs.getString("status"));

                returnRequests.add(row);
            }

            request.setAttribute("returnRequests", returnRequests);

            request.getRequestDispatcher("/admin-return-requests.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load admin return requests.", e);
        }
    }
}
