
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.library.config.DBConnection" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    String username = (String) session.getAttribute("username");

    int totalBooks = 0;
    int totalUsers = 0;
    int issuedBooks = 0;
    int pendingReservations = 0;
    int unreadMessages = 0;

    String sql =
            "SELECT " +
                    "(SELECT COALESCE(SUM(total_quantity), 0) FROM books) AS total_books, " +
                    "(SELECT COUNT(*) FROM users WHERE UPPER(role) = 'USER') AS total_users, " +
                    "(SELECT COUNT(*) FROM issued_books WHERE status IN ('ISSUED', 'RETURN_REQUESTED')) AS issued_books, " +
                    "(SELECT COUNT(*) FROM reservations WHERE status = 'PENDING') AS pending_reservations, " +
                    "(SELECT COUNT(*) FROM contact_messages WHERE status = 'NEW') AS unread_messages";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            totalBooks = rs.getInt("total_books");
            totalUsers = rs.getInt("total_users");
            issuedBooks = rs.getInt("issued_books");
            pendingReservations = rs.getInt("pending_reservations");
            unreadMessages = rs.getInt("unread_messages");
        }

    } catch (Exception e) {
        application.log("Unable to load admin dashboard statistics.", e);
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Admin Dashboard</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/style.css">

    <style>
        .dashboard-options {
            margin-top: 30px;
            display: flex;
            justify-content: center;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }

        .dashboard-button {
            display: inline-block;
            padding: 12px 25px;
            background: #2c3e50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            text-align: center;
        }

        .dashboard-button:hover {
            background: #34495e;
            color: white;
        }

        .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 18px;
            max-width: 1100px;
            margin: 30px auto;
            padding: 10px;
        }

        .stat-card {
            background: white;
            padding: 22px 15px;
            border-radius: 8px;
            text-align: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.12);
        }

        .stat-card h3 {
            color: #2c3e50;
            font-size: 16px;
            margin-bottom: 12px;
        }

        .stat-card p {
            color: #2980b9;
            font-size: 30px;
            font-weight: bold;
            margin: 0;
        }

        .stats-heading {
            text-align: center;
            margin-top: 30px;
        }
    </style>

</head>

<body>

<header>
    <h1>Library Management System</h1>
    <p>Administrator Dashboard</p>
</header>

<main class="welcome">

    <h2>Welcome, Administrator!</h2>

    <p>You are successfully logged in.</p>

    <h3>
        Welcome, <%= username == null ? "Admin" : username %>
    </h3>

    <p>Manage books, users, reservations and library activities.</p>

    <!-- Dashboard Statistics -->

    <h2 class="stats-heading">Library Statistics</h2>

    <div class="stats-container">

        <div class="stat-card">
            <h3>Total Book Copies</h3>
            <p><%= totalBooks %></p>
        </div>

        <div class="stat-card">
            <h3>Registered Students</h3>
            <p><%= totalUsers %></p>
        </div>

        <div class="stat-card">
            <h3>Currently Issued Books</h3>
            <p><%= issuedBooks %></p>
        </div>

        <div class="stat-card">
            <h3>Pending Reservations</h3>
            <p><%= pendingReservations %></p>
        </div>

        <div class="stat-card">
            <h3>Unread Contact Messages</h3>
            <p><%= unreadMessages %></p>
        </div>

    </div>

    <!-- Admin Dashboard Buttons -->

    <div class="dashboard-options">

        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/admin-reports">
            Reports & Analytics
        </a>


        <!-- Manage Books -->
        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/admin-books">
            Manage Books
        </a>

        <!-- Manage Users -->
        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/admin-users">
            Manage Users
        </a>

        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/admin-reservations">
            Manage Reservations
        </a>

        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/admin-return-requests">
            Manage Return Requests
        </a>

        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/admin-fines">
            Manage Fines
        </a>

        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/admin-contact-messages">
            Manage Contact Messages
        </a>



        <a class="dashboard-button"
           href="<%= request.getContextPath() %>/logout">
            Logout
        </a>

    </div>

</main>

<footer>
    <p>Library Management System | Admin Panel</p>
</footer>

</body>
</html>
