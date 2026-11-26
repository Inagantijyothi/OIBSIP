
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

@WebServlet("/admin-fines")
public class AdminFinesServlet extends HttpServlet {

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
                    f.fine_id,
                    f.issue_id,
                    f.fine_amount,
                    f.payment_status,
                    u.full_name,
                    b.title,
                    i.due_date,
                    i.return_date
                FROM fines f
                JOIN issued_books i ON f.issue_id = i.issue_id
                JOIN users u ON i.user_id = u.id
                JOIN books b ON i.book_id = b.book_id
                ORDER BY f.fine_id DESC
                """;

        List<Map<String, Object>> fines = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> row = new HashMap<>();

                row.put("fineId", rs.getLong("fine_id"));
                row.put("issueId", rs.getLong("issue_id"));
                row.put("fineAmount", rs.getBigDecimal("fine_amount"));
                row.put("paymentStatus", rs.getString("payment_status"));
                row.put("studentName", rs.getString("full_name"));
                row.put("bookTitle", rs.getString("title"));
                row.put("dueDate", rs.getDate("due_date"));
                row.put("returnDate", rs.getDate("return_date"));

                fines.add(row);
            }

            request.setAttribute("fines", fines);

            request.getRequestDispatcher("/admin-fines.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Unable to load fines.", e);
        }
    }
}
