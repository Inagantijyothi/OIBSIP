
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

@WebServlet("/contact-us")
public class ContactServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"USER".equalsIgnoreCase(
                        (String) session.getAttribute("role"))) {
            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        request.getRequestDispatcher("/contact-us.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"USER".equalsIgnoreCase(
                        (String) session.getAttribute("role"))) {
            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        if (subject == null || message == null ||
                subject.trim().isEmpty() ||
                message.trim().isEmpty() ||
                subject.trim().length() > 200) {

            response.sendRedirect(
                    request.getContextPath() +
                            "/contact-us?message=invalid");
            return;
        }

        String sql = "INSERT INTO contact_messages " +
                "(user_id, subject, message) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, subject.trim());
            ps.setString(3, message.trim());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                response.sendRedirect(
                        request.getContextPath() +
                                "/contact-us?message=success");
            } else {
                response.sendRedirect(
                        request.getContextPath() +
                                "/contact-us?message=failed");
            }

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to submit contact message.", e);
        }
    }
}
