
package org.library.servlet;

import org.library.dao.ReservationDAO;
import org.library.model.Reservation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin-reservations")
public class AdminReservationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check whether admin is logged in
        if (session == null ||
                !"ADMIN".equalsIgnoreCase(
                        (String) session.getAttribute("role"))) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        try {
            ReservationDAO reservationDAO = new ReservationDAO();

            List<Reservation> reservations =
                    reservationDAO.getAllReservations();

            request.setAttribute("reservations", reservations);

            request.getRequestDispatcher(
                            "/admin-reservations.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load admin reservations.", e);
        }
    }
}
