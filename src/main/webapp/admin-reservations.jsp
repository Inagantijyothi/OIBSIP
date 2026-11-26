
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="org.library.model.Reservation" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    List<Reservation> reservations =
            (List<Reservation>) request.getAttribute("reservations");

    String message = request.getParameter("message");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Manage Reservations</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
        }

        header {
            background: #2c3e50;
            color: white;
            padding: 20px;
            text-align: center;
        }

        .container {
            width: 95%;
            margin: 30px auto;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: left;
        }

        th {
            background: #2c3e50;
            color: white;
        }

        tr:nth-child(even) {
            background: #f2f2f2;
        }

        button {
            border: none;
            padding: 8px 12px;
            border-radius: 4px;
            cursor: pointer;
            color: white;
            margin: 3px;
        }

        .approve {
            background: #27ae60;
        }

        .reject {
            background: #e74c3c;
        }

        .back-link {
            display: inline-block;
            margin: 20px 3%;
            color: #2c3e50;
            font-weight: bold;
            text-decoration: none;
        }

        .message {
            padding: 12px;
            margin: 15px 0;
            border-radius: 5px;
        }

        .success {
            background: #e8f5e9;
            color: #256029;
        }

        .error {
            background: #ffebee;
            color: #c62828;
        }
    </style>

</head>

<body>

<header>
    <h1>Library Management System</h1>
    <p>Admin - Manage Reservations</p>
</header>

<a class="back-link"
   href="<%= request.getContextPath() %>/admin-dashboard.jsp">
    ← Back to Admin Dashboard
</a>

<div class="container">

    <h2>All Book Reservations</h2>

    <% if ("approved".equals(message)) { %>

    <p class="message success">
        Reservation approved and book issued successfully!
    </p>

    <% } else if ("rejected".equals(message)) { %>

    <p class="message success">
        Reservation rejected successfully!
    </p>

    <% } else if ("failed".equals(message)) { %>

    <p class="message error">
        Unable to process request. The book may be unavailable
        or the reservation may already have been processed.
    </p>

    <% } %>

    <% if (reservations == null || reservations.isEmpty()) { %>

    <p>No reservations found.</p>

    <% } else { %>

    <table>

        <thead>
        <tr>
            <th>Reservation ID</th>
            <th>Student ID</th>
            <th>Student Name</th>
            <th>Book ID</th>
            <th>Book Title</th>
            <th>Request Date</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>

        <% for (Reservation reservation : reservations) { %>

        <tr>

            <td><%= reservation.getReservationId() %></td>

            <td><%= reservation.getUserId() %></td>

            <td><%= reservation.getFullName() %></td>

            <td><%= reservation.getBookId() %></td>

            <td><%= reservation.getBookTitle() %></td>

            <td><%= reservation.getReservationDate() %></td>

            <td><%= reservation.getStatus() %></td>

            <td>

                <% if ("PENDING".equalsIgnoreCase(
                        reservation.getStatus())) { %>

                <!-- Approve form -->
                <form action="<%= request.getContextPath() %>/reservation-action"
                      method="post"
                      style="display:inline;">

                    <input type="hidden"
                           name="reservationId"
                           value="<%= reservation.getReservationId() %>">

                    <input type="hidden"
                           name="action"
                           value="approve">

                    <button type="submit" class="approve">
                        Approve
                    </button>

                </form>

                <!-- Reject form -->
                <form action="<%= request.getContextPath() %>/reservation-action"
                      method="post"
                      style="display:inline;">

                    <input type="hidden"
                           name="reservationId"
                           value="<%= reservation.getReservationId() %>">

                    <input type="hidden"
                           name="action"
                           value="reject">

                    <button type="submit" class="reject">
                        Reject
                    </button>

                </form>

                <% } else { %>

                No action

                <% } %>

            </td>

        </tr>

        <% } %>

        </tbody>

    </table>

    <% } %>

</div>

</body>
</html>
