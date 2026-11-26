
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.sql.Timestamp" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    List<Map<String, Object>> users =
            (List<Map<String, Object>>) request.getAttribute("users");

    String search = (String) request.getAttribute("search");
    if (search == null) search = "";

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
    <title>Manage Users</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f9;
            margin: 0;
            padding: 25px;
        }

        .container {
            max-width: 1400px;
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

        .search-form {
            display: flex;
            gap: 10px;
            margin: 20px 0;
        }

        .search-form input {
            flex: 1;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        button, .back-button {
            padding: 10px 15px;
            background: #2c3e50;
            color: white;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            cursor: pointer;
        }

        button:hover, .back-button:hover {
            background: #34495e;
        }

        .table-wrapper {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }

        th, td {
            padding: 10px;
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

        .edit-form input {
            width: 150px;
            padding: 7px;
            box-sizing: border-box;
        }

        .edit-form button {
            margin-top: 7px;
            background: #16803c;
        }

        .role-admin {
            color: #c0392b;
            font-weight: bold;
        }

        .role-user {
            color: #16803c;
            font-weight: bold;
        }

        .count {
            margin-top: 15px;
            font-weight: bold;
            color: #2c3e50;
        }

        .message {
            padding: 10px;
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
            color: #777;
        }

        .back {
            margin-top: 20px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Admin - Manage Users</h2>

    <% if ("updated".equals(message)) { %>
    <div class="message">User details updated successfully.</div>
    <% } else if ("invalid".equals(message)) { %>
    <div class="message error">Please enter valid user details.</div>
    <% } else if ("notfound".equals(message)) { %>
    <div class="message error">User was not found.</div>
    <% } %>

    <form class="search-form"
          action="<%= request.getContextPath() %>/admin-users"
          method="get">

        <input type="text"
               name="search"
               placeholder="Search by ID, name, username, email, phone or role"
               value="<%= safe.apply(search) %>">

        <button type="submit">Search</button>

        <a class="back-button"
           href="<%= request.getContextPath() %>/admin-users">
            Clear
        </a>
    </form>

    <div class="count">
        Total matching users: <%= users == null ? 0 : users.size() %>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>User ID</th>
                <th>Full Name</th>
                <th>Username</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Role</th>
                <th>Registration Date</th>
                <th>Edit Details</th>
            </tr>
            </thead>

            <tbody>

            <%
                if (users != null && !users.isEmpty()) {
                    for (Map<String, Object> user : users) {
                        String userRole = String.valueOf(user.get("role"));
                        Timestamp createdAt =
                                (Timestamp) user.get("createdAt");
            %>

            <tr>
                <td><%= safe.apply(user.get("id")) %></td>
                <td><%= safe.apply(user.get("fullName")) %></td>
                <td><%= safe.apply(user.get("username")) %></td>
                <td><%= safe.apply(user.get("email")) %></td>
                <td><%= safe.apply(user.get("phone")) %></td>

                <td>
                    <% if ("ADMIN".equalsIgnoreCase(userRole)) { %>
                    <span class="role-admin">ADMIN</span>
                    <% } else { %>
                    <span class="role-user">USER</span>
                    <% } %>
                </td>

                <td>
                    <%= createdAt == null ? "-" :
                            createdAt.toLocalDateTime().toLocalDate() %>
                </td>

                <td>
                    <form class="edit-form"
                          action="<%= request.getContextPath() %>/update-user"
                          method="post">

                        <input type="hidden"
                               name="userId"
                               value="<%= safe.apply(user.get("id")) %>">

                        <input type="text"
                               name="fullName"
                               placeholder="Full name"
                               value="<%= safe.apply(user.get("fullName")) %>"
                               required>

                        <br><br>

                        <input type="email"
                               name="email"
                               placeholder="Email"
                               value="<%= safe.apply(user.get("email")) %>"
                               required>

                        <br><br>

                        <input type="text"
                               name="phone"
                               placeholder="Phone"
                               value="<%= safe.apply(user.get("phone")) %>"
                               required>

                        <br>

                        <button type="submit">Update</button>
                    </form>
                </td>
            </tr>

            <%
                }
            } else {
            %>

            <tr>
                <td colspan="8" class="empty">No users found.</td>
            </tr>

            <%
                }
            %>

            </tbody>
        </table>
    </div>

    <div class="back">
        <a class="back-button"
           href="<%= request.getContextPath() %>/admin-dashboard.jsp">
            Back to Admin Dashboard
        </a>
    </div>

</div>

</body>
</html>
