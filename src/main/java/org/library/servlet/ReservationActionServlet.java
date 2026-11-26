
package org.library.servlet;

import org.library.dao.ReservationDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/reservation-action")
public class ReservationActionServlet extends HttpServlet {

    private final ReservationDAO reservationDAO =
            new ReservationDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check admin login
        if (session == null ||
                !"ADMIN".equalsIgnoreCase(
                        (String) session.getAttribute("role"))) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return;
        }

        String reservationIdValue =
                request.getParameter("reservationId");

        String action = request.getParameter("action");

        String message = "failed";

        try {

            long reservationId =
                    Long.parseLong(reservationIdValue);

            if ("approve".equalsIgnoreCase(action)) {

                boolean success =
                        reservationDAO.approveReservation(reservationId);

                message = success ? "approved" : "failed";

            } else if ("reject".equalsIgnoreCase(action)) {

                boolean success =
                        reservationDAO.rejectReservation(reservationId);

                message = success ? "rejected" : "failed";
            }

        } catch (NumberFormatException | NullPointerException e) {

            message = "failed";
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/admin-reservations?message=" + message
        );
    }
}
