
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.sql.Timestamp" %>

<%!
    private String escapeHtml(Object value) {
        if (value == null) return "";

        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("ADMIN")) {
        response.sendRedirect("login.html");
        return;
    }

    List<Map<String, Object>> messages =
            (List<Map<String, Object>>) request.getAttribute("messages");

    String search = (String) request.getAttribute("search");
    String result = request.getParameter("message");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Manage Contact Messages</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        .messages-container {
            width: 95%;
            margin: 30px auto;
            overflow-x: auto;
        }

        .search-form {
            margin: 20px 0;
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .search-form input {
            padding: 10px;
            width: 280px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .search-form button,
        .action-btn,
        .back-btn {
            padding: 9px 15px;
            border: none;
            border-radius: 5px;
            background: #2c3e50;
            color: white;
            text-decoration: none;
            cursor: pointer;
        }

        .action-btn:hover,
        .search-form button:hover,
        .back-btn:hover {
            opacity: 0.85;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            min-width: 900px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
            vertical-align: top;
        }

        th {
            background: #2c3e50;
            color: white;
        }

        .read-status {
            color: green;
            font-weight: bold;
        }

        .new-status {
            color: red;
            font-weight: bold;
        }

        .read-btn {
            background: #198754;
        }

        .delete-btn {
            background: #dc3545;
        }

        .action-form {
            display: inline-block;
            margin: 3px;
        }

        .success {
            color: green;
            font-weight: bold;
        }

        .error {
            color: red;
            font-weight: bold;
        }

        .back-btn {
            display: inline-block;
            margin-top: 20px;
        }
    </style>
</head>

<body>

<header>
    <h1>Library Management System</h1>
    <p>Admin - Manage Contact Messages</p>
</header>

<main class="messages-container">

    <h2>Student Contact Messages</h2>

    <% if ("success".equals(result)) { %>
    <p class="success">Action completed successfully!</p>
    <% } else if ("failed".equals(result)) { %>
    <p class="error">Unable to process the message.</p>
    <% } else if ("invalid".equals(result)) { %>
    <p class="error">Invalid request.</p>
    <% } %>

    <!-- Search Messages -->
    <form class="search-form"
          action="<%= request.getContextPath() %>/admin-contact-messages"
          method="get">

        <input type="text"
               name="search"
               placeholder="Search by student, subject or message"
               value="<%= escapeHtml(search) %>">

        <button type="submit">Search</button>

        <a class="action-btn"
           href="<%= request.getContextPath() %>/admin-contact-messages">
            Clear
        </a>

    </form>

    <table>

        <thead>
        <tr>
            <th>Message ID</th>
            <th>Student Name</th>
            <th>Subject</th>
            <th>Message</th>
            <th>Sent At</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>

        <tbody>

        <%
            if (messages != null && !messages.isEmpty()) {

                for (Map<String, Object> msg : messages) {

                    String status = (String) msg.get("status");
                    boolean isRead = "READ".equalsIgnoreCase(status);
        %>

        <tr>

            <td><%= msg.get("messageId") %></td>

            <td><%= escapeHtml(msg.get("fullName")) %></td>

            <td><%= escapeHtml(msg.get("subject")) %></td>

            <td><%= escapeHtml(msg.get("message")) %></td>

            <td><%= escapeHtml(msg.get("sentAt")) %></td>

            <td>
                <% if (isRead) { %>
                <span class="read-status">READ</span>
                <% } else { %>
                <span class="new-status">
                        <%= escapeHtml(status) %>
                    </span>
                <% } %>
            </td>

            <td>

                <% if (!isRead) { %>

                <form class="action-form"
                      action="<%= request.getContextPath() %>/contact-message-action"
                      method="post">

                    <input type="hidden"
                           name="messageId"
                           value="<%= msg.get("messageId") %>">

                    <input type="hidden"
                           name="action"
                           value="read">

                    <button class="action-btn read-btn"
                            type="submit">
                        Mark as Read
                    </button>

                </form>

                <% } %>

                <form class="action-form"
                      action="<%= request.getContextPath() %>/contact-message-action"
                      method="post"
                      onsubmit="return confirm('Are you sure you want to delete this message?');">

                    <input type="hidden"
                           name="messageId"
                           value="<%= msg.get("messageId") %>">

                    <input type="hidden"
                           name="action"
                           value="delete">

                    <button class="action-btn delete-btn"
                            type="submit">
                        Delete
                    </button>

                </form>

            </td>

        </tr>

        <%
            }

        } else {
        %>

        <tr>
            <td colspan="7" style="text-align:center;">
                No contact messages found.
            </td>
        </tr>

        <% } %>

        </tbody>

    </table>

    <a class="back-btn"
       href="<%= request.getContextPath() %>/admin-dashboard.jsp">
        Back to Admin Dashboard
    </a>

</main>

<footer>
    <p>Library Management System | Admin Panel</p>
</footer>

</body>
</html>
