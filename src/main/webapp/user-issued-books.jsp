
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    if (session.getAttribute("userId") == null ||
            !"USER".equals(session.getAttribute("role"))) {
        response.sendRedirect("login.html");
        return;
    }

    List<Map<String, Object>> issuedBooks =
            (List<Map<String, Object>>) request.getAttribute("issuedBooks");

    String message = request.getParameter("message");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>My Issued Books</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 25px;
        }

        .container {
            max-width: 1100px;
            margin: 30px auto;
            background: white;
            padding: 25px;
            border-radius: 10px;
        }

        h2 {
            color: #263b5e;
            text-align: center;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 25px;
        }

        th, td {
            padding: 13px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background: #263b5e;
            color: white;
        }

        .issued {
            color: green;
            font-weight: bold;
        }

        .returned {
            color: blue;
            font-weight: bold;
        }

        .pending {
            color: #d97706;
            font-weight: bold;
        }

        .back-btn, .return-btn {
            display: inline-block;
            padding: 10px 18px;
            color: white;
            text-decoration: none;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        .back-btn {
            background: #263b5e;
        }

        .return-btn {
            background: #e67e22;
        }

        .message {
            text-align: center;
            padding: 12px;
            margin-top: 15px;
            font-weight: bold;
        }

        .success {
            color: green;
        }

        .error {
            color: red;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>My Issued Books</h2>

    <a href="user-dashboard.jsp" class="back-btn">
        Back to Dashboard
    </a>

    <% if ("return-requested".equals(message)) { %>

    <p class="message success">
        Return request submitted successfully!
    </p>

    <% } else if ("return-failed".equals(message)) { %>

    <p class="message error">
        Unable to submit return request. Please try again.
    </p>

    <% } %>

    <table>

        <thead>
        <tr>
            <th>Issue ID</th>
            <th>Book Title</th>
            <th>Author</th>
            <th>Issue Date</th>
            <th>Due Date</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>

        <%
            if (issuedBooks != null && !issuedBooks.isEmpty()) {

                for (Map<String, Object> book : issuedBooks) {

                    String status = (String) book.get("status");
        %>

        <tr>

            <td><%= book.get("issueId") %></td>

            <td><%= book.get("title") %></td>

            <td><%= book.get("author") %></td>

            <td><%= book.get("issueDate") %></td>

            <td><%= book.get("dueDate") %></td>

            <td class="<%= "ISSUED".equals(status) ? "issued" :
                          "RETURN_REQUESTED".equals(status) ? "pending" :
                          "returned" %>">

                <%= status %>

            </td>

            <td>

                <% if ("ISSUED".equals(status)) { %>

                <form action="<%= request.getContextPath() %>/request-return"
                      method="post">

                    <input type="hidden"
                           name="issueId"
                           value="<%= book.get("issueId") %>">

                    <button type="submit" class="return-btn">
                        Request Return
                    </button>

                </form>

                <% } else if ("RETURN_REQUESTED".equals(status)) { %>

                <span>Waiting for Admin</span>

                <% } else { %>

                <span>Completed</span>

                <% } %>

            </td>

        </tr>

        <%
            }

        } else {
        %>

        <tr>
            <td colspan="7">No issued books found.</td>
        </tr>

        <%
            }
        %>

        </tbody>

    </table>

</div>

</body>
</html>
