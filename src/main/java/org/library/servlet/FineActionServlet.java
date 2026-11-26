
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

@WebServlet("/fine-action")
public class FineActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        try {
            long fineId = Long.parseLong(request.getParameter("fineId"));

            String sql = """
                    UPDATE fines
                    SET payment_status = 'PAID'
                    WHERE fine_id = ?
                    AND payment_status = 'UNPAID'
                    """;

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, fineId);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated > 0) {
                    response.sendRedirect(request.getContextPath()
                            + "/admin-fines?message=success");
                } else {
                    response.sendRedirect(request.getContextPath()
                            + "/admin-fines?message=failed");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect(request.getContextPath()
                    + "/admin-fines?message=failed");
        }
    }
}
