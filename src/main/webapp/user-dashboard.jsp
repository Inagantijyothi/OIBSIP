
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("USER")) {
        response.sendRedirect("login.html");
        return;
    }

    String username = (String) session.getAttribute("username");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>User Dashboard</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        .dashboard-buttons {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
            margin-top: 25px;
        }

        .dashboard-btn {
            display: inline-block;
            padding: 12px 25px;
            background: #2c3e50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            text-align: center;
            transition: background 0.3s ease;
        }

        .dashboard-btn:hover {
            background: #34495e;
            color: white;
        }
    </style>
</head>

<body>

<header>
    <h1>Library Management System</h1>
    <p>User Dashboard</p>
</header>

<main class="welcome">

    <h2>Welcome to Your Library Account!</h2>

    <h3>
        Welcome, <%= username == null ? "User" : username %>
    </h3>

    <p>You are successfully logged in.</p>

    <p>Explore books and manage your library activities.</p>

    <div class="dashboard-buttons">

        <!-- Available Books -->
        <a href="<%= request.getContextPath() %>/books"
           class="dashboard-btn">
            View Available Books
        </a>

        <!-- My Issued Books -->
        <a href="<%= request.getContextPath() %>/my-issued-books"
           class="dashboard-btn">
            My Issued Books
        </a>

        <!-- My Fines -->
        <a href="<%= request.getContextPath() %>/my-fines"
           class="dashboard-btn">
            My Fines
        </a>

        <a href="<%= request.getContextPath() %>/my-profile"
           class="dashboard-btn">
            My Profile
        </a>

        <!-- My Reservations -->
        <a class="dashboard-btn"
           href="<%= request.getContextPath() %>/my-reservations">
            My Reservations
        </a>


        <!-- Contact Us -->
        <a href="<%= request.getContextPath() %>/contact-us"
           class="dashboard-btn">
            Contact Us
        </a>

        <!-- Logout -->
        <a href="<%= request.getContextPath() %>/logout"
           class="dashboard-btn">
            Logout
        </a>



    </div>

</main>

<footer>
    <p>Library Management System | User Panel</p>
</footer>

</body>
</html>
