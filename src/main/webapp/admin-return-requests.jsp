
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    if (session.getAttribute("role") == null ||
            !"ADMIN".equals(session.getAttribute("role"))) {
        response.sendRedirect("login.html");
        return;
    }

    String contextPath = request.getContextPath();

    List<Map<String, Object>> returnRequests =
            (List<Map<String, Object>>) request.getAttribute("returnRequests");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Return Requests</title>

    <link rel="stylesheet"
          href="<%= contextPath %>/css/style.css">

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

        .approve-btn {
            background: #198754;
            color: white;
            border: none;
            padding: 9px 15px;
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

        .empty {
            text-align: center;
            padding: 20px;
            color: #777;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Manage Return Requests</h2>

    <a class="back-btn"
       href="<%= contextPath %>/admin-dashboard.jsp">
        Back to Dashboard
    </a>

    <% if (returnRequests == null || returnRequests.isEmpty()) { %>

    <p class="empty">No pending return requests found.</p>

    <% } else { %>

    <table>
        <tr>
            <th>Issue ID</th>
            <th>Student Name</th>
            <th>Book Title</th>
            <th>Author</th>
            <th>Issue Date</th>
            <th>Due Date</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <% for (Map<String, Object> row : returnRequests) { %>

        <tr>
            <td><%= row.get("issueId") %></td>
            <td><%= row.get("studentName") %></td>
            <td><%= row.get("title") %></td>
            <td><%= row.get("author") %></td>
            <td><%= row.get("issueDate") %></td>
            <td><%= row.get("dueDate") %></td>
            <td>RETURN REQUESTED</td>

            <td>
                <form action="<%= contextPath %>/return-action"
                      method="post">

                    <input type="hidden"
                           name="issueId"
                           value="<%= row.get("issueId") %>">

                    <input type="hidden"
                           name="action"
                           value="approve">

                    <button type="submit" class="approve-btn">
                        Approve Return
                    </button>

                </form>
            </td>
        </tr>

        <% } %>

    </table>

    <% } %>

</div>

</body>
</html>
