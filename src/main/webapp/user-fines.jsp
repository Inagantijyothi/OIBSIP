
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    if (session.getAttribute("role") == null ||
            !"USER".equals(session.getAttribute("role"))) {
        response.sendRedirect("login.html");
        return;
    }

    List<Map<String, Object>> fines =
            (List<Map<String, Object>>) request.getAttribute("fines");

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Fines</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            padding: 25px;
        }

        .container {
            max-width: 1000px;
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
            padding: 12px;
            border: 1px solid #ddd;
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

        .back-btn {
            display: inline-block;
            background: #243b64;
            color: white;
            padding: 10px 15px;
            text-decoration: none;
            border-radius: 5px;
        }

        .empty {
            text-align: center;
            padding: 20px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>My Fine Details</h2>

    <a class="back-btn"
       href="<%= contextPath %>/user-dashboard.jsp">
        Back to Dashboard
    </a>

    <% if (fines == null || fines.isEmpty()) { %>

    <p class="empty">You have no fine records.</p>

    <% } else { %>

    <table>
        <tr>
            <th>Fine ID</th>
            <th>Issue ID</th>
            <th>Book Name</th>
            <th>Due Date</th>
            <th>Return Date</th>
            <th>Fine Amount</th>
            <th>Payment Status</th>
        </tr>

        <% for (Map<String, Object> row : fines) { %>

        <tr>
            <td><%= row.get("fineId") %></td>
            <td><%= row.get("issueId") %></td>
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
        </tr>

        <% } %>

    </table>

    <% } %>

</div>

</body>
</html>
