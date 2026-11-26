
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    if (session.getAttribute("role") == null ||
            !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect("login.html");
        return;
    }

    List<Map<String, Object>> fines =
            (List<Map<String, Object>>) request.getAttribute("fines");

    String contextPath = request.getContextPath();
    String message = request.getParameter("message");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Fines</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            padding: 25px;
        }

        .container {
            max-width: 1100px;
            margin: auto;
            background: white;
            padding: 25px;
            border-radius: 10px;
        }

        h2 {
            text-align: center;
            color: #243b64;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 25px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: center;
        }

        th {
            background: #243b64;
            color: white;
        }

        .paid {
            color: green;
            font-weight: bold;
        }

        .unpaid {
            color: red;
            font-weight: bold;
        }

        .pay-btn {
            background: #198754;
            color: white;
            border: none;
            padding: 9px 14px;
            border-radius: 5px;
            cursor: pointer;
        }

        .back-btn {
            display: inline-block;
            background: #243b64;
            color: white;
            padding: 10px 15px;
            text-decoration: none;
            border-radius: 5px;
        }

        .message {
            text-align: center;
            color: green;
            font-weight: bold;
        }

        .empty {
            text-align: center;
            padding: 20px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Manage Library Fines</h2>

    <a class="back-btn"
       href="<%= contextPath %>/admin-dashboard.jsp">
        Back to Dashboard
    </a>

    <% if ("success".equals(message)) { %>
    <p class="message">Fine marked as PAID successfully!</p>
    <% } else if ("failed".equals(message)) { %>
    <p style="color:red; text-align:center;">
        Unable to update payment status.
    </p>
    <% } %>

    <% if (fines == null || fines.isEmpty()) { %>

    <p class="empty">No fines found.</p>

    <% } else { %>

    <table>
        <tr>
            <th>Fine ID</th>
            <th>Issue ID</th>
            <th>Student Name</th>
            <th>Book Title</th>
            <th>Due Date</th>
            <th>Return Date</th>
            <th>Fine Amount</th>
            <th>Payment Status</th>
            <th>Action</th>
        </tr>

        <% for (Map<String, Object> row : fines) { %>

        <tr>
            <td><%= row.get("fineId") %></td>
            <td><%= row.get("issueId") %></td>
            <td><%= row.get("studentName") %></td>
            <td><%= row.get("bookTitle") %></td>
            <td><%= row.get("dueDate") %></td>
            <td><%= row.get("returnDate") %></td>
            <td>₹<%= row.get("fineAmount") %></td>

            <td>
                <% if ("PAID".equals(row.get("paymentStatus"))) { %>
                <span class="paid">PAID</span>
                <% } else { %>
                <span class="unpaid">UNPAID</span>
                <% } %>
            </td>

            <td>
                <% if ("UNPAID".equals(row.get("paymentStatus"))) { %>

                <form action="<%= contextPath %>/fine-action"
                      method="post">

                    <input type="hidden"
                           name="fineId"
                           value="<%= row.get("fineId") %>">

                    <button class="pay-btn" type="submit">
                        Mark as Paid
                    </button>

                </form>

                <% } else { %>
                Completed
                <% } %>
            </td>
        </tr>

        <% } %>

    </table>

    <% } %>

</div>

</body>
</html>
