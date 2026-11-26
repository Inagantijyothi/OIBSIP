
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="org.library.model.Book" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("USER")) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    List<Book> books = (List<Book>) request.getAttribute("books");
    String keyword = (String) request.getAttribute("keyword");

    String safeKeyword = keyword == null ? "" :
            keyword.replace("&", "&amp;")
                    .replace("\"", "&quot;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Available Books</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/style.css">

    <style>
        .books-container {
            width: 90%;
            margin: 30px auto;
            overflow-x: auto;
        }

        .books-table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        .books-table th,
        .books-table td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: left;
        }

        .books-table th {
            background: #2c3e50;
            color: white;
        }

        .books-table tr:nth-child(even) {
            background: #f2f2f2;
        }

        .back-link {
            display: inline-block;
            margin: 20px 5%;
            text-decoration: none;
            color: #2c3e50;
            font-weight: bold;
        }

        .search-form {
            display: flex;
            gap: 10px;
            margin: 20px 0;
            flex-wrap: wrap;
        }

        .search-form input {
            padding: 11px;
            width: 300px;
            max-width: 100%;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .search-form button,
        .search-form a {
            padding: 11px 20px;
            background: #2c3e50;
            color: white;
            border: none;
            border-radius: 5px;
            text-decoration: none;
            cursor: pointer;
        }
    </style>
</head>

<body>

<%
    String message = request.getParameter("message");
%>

<% if ("success".equals(message)) { %>
<div style="background:#d4edda; color:#155724;
                padding:15px; margin:15px 0;
                border-radius:6px; text-align:center;">
    <strong>Request sent successfully!</strong>
    Please wait for admin approval.
</div>
<% } else if ("error".equals(message)) { %>
<div style="background:#f8d7da; color:#721c24;
                padding:15px; margin:15px 0;
                border-radius:6px; text-align:center;">
    <strong>Request failed!</strong>
    Please try again.
</div>
<% } %>

<header>
    <h1>Library Management System</h1>
    <p>Available Books</p>
</header>

<a class="back-link"
   href="<%= request.getContextPath() %>/user-dashboard.jsp">
    ← Back to Dashboard
</a>

<div class="books-container">

    <h2>Available Books Catalogue</h2>

    <!-- Search form -->
    <form action="<%= request.getContextPath() %>/books"
          method="get"
          class="search-form">

        <input type="text"
               name="keyword"
               placeholder="Search by title, author or category"
               value="<%= safeKeyword %>">

        <button type="submit">Search</button>

        <a href="<%= request.getContextPath() %>/books">
            Show All Books
        </a>
    </form>

    <% if (books == null || books.isEmpty()) { %>

    <p>No available books found.</p>

    <% } else { %>

    <table class="books-table">

        <thead>
        <tr>
            <th>Book ID</th>
            <th>Title</th>
            <th>Author</th>
            <th>ISBN</th>
            <th>Category</th>
            <th>Total Quantity</th>
            <th>Available Quantity</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>

        <% for (Book book : books) { %>

        <tr>
            <td><%= book.getBookId() %></td>
            <td><%= book.getTitle() %></td>
            <td><%= book.getAuthor() %></td>
            <td><%= book.getIsbn() %></td>
            <td><%= book.getCategory() %></td>
            <td><%= book.getTotalQuantity() %></td>
            <td><%= book.getAvailableQuantity() %></td>
            <td>
                <form action="<%= request.getContextPath() %>/reserve-book"
                      method="post">

                    <input type="hidden"
                           name="bookId"
                           value="<%= book.getBookId() %>">

                    <button type="submit"
                            style="background:#27ae60;
                       color:white;
                       padding:8px 15px;
                       border:none;
                       border-radius:5px;
                       cursor:pointer;">
                        Request Book
                    </button>

                </form>
            </td>
        </tr>

        <% } %>

        </tbody>

    </table>

    <% } %>

</div>

<footer>
    <p>Library Management System | Available Books</p>
</footer>

</body>
</html>
