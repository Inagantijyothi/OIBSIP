
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("USER")) {
        response.sendRedirect("login.html");
        return;
    }

    String fullName = (String) request.getAttribute("fullName");
    String username = (String) request.getAttribute("username");
    String email = (String) request.getAttribute("email");
    String phone = (String) request.getAttribute("phone");

    String message = request.getParameter("message");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>My Profile</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        .profile-container {
            max-width: 500px;
            margin: 30px auto;
            padding: 25px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .profile-container label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
        }

        .profile-container input {
            width: 100%;
            padding: 11px;
            margin-top: 7px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .profile-container button,
        .back-btn {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 22px;
            background: #2c3e50;
            color: white;
            border: none;
            border-radius: 5px;
            text-decoration: none;
            cursor: pointer;
        }

        .profile-container button:hover,
        .back-btn:hover {
            background: #34495e;
        }

        .success {
            color: green;
            text-align: center;
        }

        .error {
            color: red;
            text-align: center;
        }
    </style>
</head>

<body>

<header>
    <h1>Library Management System</h1>
    <p>My Profile</p>
</header>

<main class="profile-container">

    <h2>My Personal Details</h2>

    <% if ("success".equals(message)) { %>
    <p class="success">Profile updated successfully!</p>
    <% } else if ("failed".equals(message)) { %>
    <p class="error">Unable to update profile.</p>
    <% } else if ("invalid".equals(message)) { %>
    <p class="error">Please fill in all required fields.</p>
    <% } %>

    <form action="<%= request.getContextPath() %>/update-profile"
          method="post">

        <label>Full Name</label>
        <input type="text" name="fullName"
               value="<%= fullName == null ? "" : fullName %>" required>

        <label>Username</label>
        <input type="text" value="<%= username == null ? "" : username %>"
               readonly>

        <label>Email</label>
        <input type="email" name="email"
               value="<%= email == null ? "" : email %>" required>

        <label>Phone Number</label>
        <input type="text" name="phone"
               value="<%= phone == null ? "" : phone %>" required>

        <button type="submit">Update Profile</button>

    </form>

    <a class="back-btn"
       href="<%= request.getContextPath() %>/user-dashboard.jsp">
        Back to Dashboard
    </a>

</main>

<footer>
    <p>Library Management System | User Panel</p>
</footer>

</body>
</html>
