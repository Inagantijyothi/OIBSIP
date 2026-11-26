
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

@WebServlet("/request-return")
public class ReturnRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"USER".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        try {
            long issueId = Long.parseLong(
                    request.getParameter("issueId"));

            String sql = """
                    UPDATE issued_books
                    SET status = 'RETURN_REQUESTED'
                    WHERE issue_id = ?
                    AND user_id = ?
                    AND status = 'ISSUED'
                    """;

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, issueId);
                ps.setLong(2, userId);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated > 0) {
                    response.sendRedirect(
                            request.getContextPath()
                                    + "/my-issued-books?message=return-requested");
                } else {
                    response.sendRedirect(
                            request.getContextPath()
                                    + "/my-issued-books?message=return-failed");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath()
                            + "/my-issued-books?message=return-failed");
        }
    }
}
