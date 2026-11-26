
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
import java.util.List;

@WebServlet("/my-issued-books")
public class UserIssuedBooksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"USER".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        String sql = """
                SELECT i.issue_id, b.title, b.author,
                       i.issue_date, i.due_date, i.status
                FROM issued_books i
                JOIN books b ON i.book_id = b.book_id
                WHERE i.user_id = ?
                ORDER BY i.issue_id DESC
                """;

        List<java.util.Map<String, Object>> issuedBooks =
                new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> book =
                            new java.util.HashMap<>();

                    book.put("issueId", rs.getLong("issue_id"));
                    book.put("title", rs.getString("title"));
                    book.put("author", rs.getString("author"));
                    book.put("issueDate", rs.getDate("issue_date"));
                    book.put("dueDate", rs.getDate("due_date"));
                    book.put("status", rs.getString("status"));

                    issuedBooks.add(book);
                }
            }

            request.setAttribute("issuedBooks", issuedBooks);

            request.getRequestDispatcher("/user-issued-books.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(
                    "Unable to load issued books.", e);
        }
    }
}
