
package org.library.servlet;

import org.library.dao.ReservationDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/reserve-book")
public class ReservationServlet extends HttpServlet {

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

        try {
            long userId = ((Number)
                    session.getAttribute("userId")).longValue();

            long bookId = Long.parseLong(
                    request.getParameter("bookId"));

            ReservationDAO reservationDAO = new ReservationDAO();

            reservationDAO.requestBook(userId, bookId);

            response.sendRedirect(
                    request.getContextPath() + "/books?message=success");

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/books?message=error");
        }
    }
}
