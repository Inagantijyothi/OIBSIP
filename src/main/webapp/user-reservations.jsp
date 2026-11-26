
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.sql.Timestamp" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("USER")) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    List<Map<String, Object>> reservations =
            (List<Map<String, Object>>) request.getAttribute("reservations");

    String message = request.getParameter("message");

    java.util.function.Function<Object, String> safe = value -> {
        if (value == null) return "";
        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    };
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Reservations</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 25px;
        }

        .container {
            max-width: 1100px;
            margin: auto;
            background: white;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: #2c3e50;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }

        th {
            background: #2c3e50;
            color: white;
        }

        tr:nth-child(even) {
            background: #f2f2f2;
        }

        .status {
            font-weight: bold;
        }

        .pending {
            color: #b7791f;
        }

        .approved {
            color: #16803c;
        }

        .rejected, .cancelled {
            color: #c0392b;
        }

        button, .back-button {
            padding: 9px 14px;
            border: none;
            border-radius: 4px;
            background: #2c3e50;
            color: white;
            cursor: pointer;
            text-decoration: none;
        }

        .cancel-button {
            background: #c0392b;
        }

        .cancel-button:hover {
            background: #922b21;
        }

        .back-button:hover {
            background: #34495e;
        }

        .message {
            padding: 12px;
            margin: 15px 0;
            border-radius: 4px;
            background: #eaf5ea;
            color: #216e39;
        }

        .error {
            background: #fdecea;
            color: #b42318;
        }

        .empty {
            text-align: center;
            padding: 20px;
        }

        .back {
            margin-top: 25px;
        }

        .table-wrapper {
            overflow-x: auto;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>My Book Reservations</h2>

    <% if ("cancelled".equals(message)) { %>
    <div class="message">
        Reservation cancelled successfully.
    </div>
    <% } else if ("failed".equals(message)) { %>
    <div class="message error">
        Unable to cancel reservation. It may already have been processed.
    </div>
    <% } %>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>Reservation ID</th>
                <th>Book ID</th>
                <th>Book Title</th>
                <th>Author</th>
                <th>Request Date</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>

            <%
                if (reservations != null && !reservations.isEmpty()) {
                    for (Map<String, Object> reservation : reservations) {

                        String status =
                                String.valueOf(reservation.get("status"));

                        Timestamp date =
                                (Timestamp) reservation.get("reservationDate");

                        String statusClass = "status";

                        if ("PENDING".equalsIgnoreCase(status)) {
                            statusClass += " pending";
                        } else if ("APPROVED".equalsIgnoreCase(status)) {
                            statusClass += " approved";
                        } else if ("REJECTED".equalsIgnoreCase(status)
                                || "CANCELLED".equalsIgnoreCase(status)) {
                            statusClass += " rejected";
                        }
            %>

            <tr>
                <td><%= safe.apply(reservation.get("reservationId")) %></td>
                <td><%= safe.apply(reservation.get("bookId")) %></td>
                <td><%= safe.apply(reservation.get("title")) %></td>
                <td><%= safe.apply(reservation.get("author")) %></td>

                <td>
                    <%= date == null ? "-" : date.toLocalDateTime() %>
                </td>

                <td>
                    <span class="<%= statusClass %>">
                        <%= safe.apply(status) %>
                    </span>
                </td>

                <td>
                    <% if ("PENDING".equalsIgnoreCase(status)) { %>

                    <form method="post"
                          action="<%= request.getContextPath() %>/cancel-reservation"
                          onsubmit="return confirm('Are you sure you want to cancel this reservation?');">

                        <input type="hidden"
                               name="reservationId"
                               value="<%= safe.apply(reservation.get("reservationId")) %>">

                        <button type="submit" class="cancel-button">
                            Cancel
                        </button>
                    </form>

                    <% } else { %>
                    -
                    <% } %>
                </td>
            </tr>

            <%
                }
            } else {
            %>

            <tr>
                <td colspan="7" class="empty">
                    You have not made any book reservations yet.
                </td>
            </tr>

            <%
                }
            %>

            </tbody>
        </table>
    </div>

    <div class="back">
        <a class="back-button"
           href="<%= request.getContextPath() %>/user-dashboard.jsp">
            Back to User Dashboard
        </a>
    </div>

</div>

</body>
</html>
