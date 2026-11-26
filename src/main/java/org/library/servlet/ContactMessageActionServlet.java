
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

@WebServlet("/contact-message-action")
public class ContactMessageActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
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

        String action = request.getParameter("action");
        String messageIdParam = request.getParameter("messageId");

        try {
            long messageId = Long.parseLong(messageIdParam);

            String sql;

            if ("read".equals(action)) {
                sql = "UPDATE contact_messages " +
                        "SET status = 'READ' WHERE message_id = ?";
            } else if ("delete".equals(action)) {
                sql = "DELETE FROM contact_messages " +
                        "WHERE message_id = ?";
            } else {
                response.sendRedirect(
                        request.getContextPath() +
                                "/admin-contact-messages?message=invalid");
                return;
            }

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, messageId);

                int rows = ps.executeUpdate();

                String result = rows > 0 ? "success" : "failed";

                response.sendRedirect(
                        request.getContextPath() +
                                "/admin-contact-messages?message=" + result);
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(
                    request.getContextPath() +
                            "/admin-contact-messages?message=invalid");
        } catch (Exception e) {
            throw new ServletException(
                    "Unable to process contact message.", e);
        }
    }
}
