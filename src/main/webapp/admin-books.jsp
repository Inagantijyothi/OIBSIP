
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

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

    List<Map<String, Object>> books =
            (List<Map<String, Object>>) request.getAttribute("books");

    List<Map<String, Object>> categories =
            (List<Map<String, Object>>) request.getAttribute("categories");

    String message = request.getParameter("message");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Manage Books</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/style.css">

    <style>
        .books-container {
            width: 95%;
            margin: 30px auto;
            overflow-x: auto;
        }

        .add-book-form {
            max-width: 650px;
            margin: 25px auto;
            padding: 22px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .add-book-form label {
            display: block;
            margin-top: 12px;
            font-weight: bold;
        }

        .add-book-form input,
        .add-book-form select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        table {
            width: 100%;
            min-width: 1050px;
            border-collapse: collapse;
            background: white;
        }

        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: center;
            vertical-align: middle;
        }

        th {
            background: #2c3e50;
            color: white;
        }

        .edit-form input,
        .edit-form select {
            width: 130px;
            padding: 7px;
            margin: 3px;
            box-sizing: border-box;
        }

        .action-btn,
        .back-btn {
            display: inline-block;
            padding: 9px 15px;
            border: none;
            border-radius: 5px;
            color: white;
            text-decoration: none;
            cursor: pointer;
            margin: 3px;
        }

        .add-btn {
            background: #198754;
            color: white;
            padding: 11px 20px;
            border: none;
            border-radius: 5px;
            margin-top: 15px;
            cursor: pointer;
        }

        .update-btn {
            background: #0d6efd;
        }

        .delete-btn {
            background: #dc3545;
        }

        .back-btn {
            background: #2c3e50;
            margin-top: 20px;
        }

        .success {
            color: green;
            font-weight: bold;
            text-align: center;
        }

        .error {
            color: red;
            font-weight: bold;
            text-align: center;
        }

        .action-form {
            display: inline-block;
        }
    </style>
</head>

<body>

<header>
    <h1>Library Management System</h1>
    <p>Admin - Manage Books</p>
</header>

<main class="books-container">

    <h2 style="text-align:center;">Add New Book</h2>

    <% if ("added".equals(message)) { %>
    <p class="success">Book added successfully!</p>
    <% } else if ("updated".equals(message)) { %>
    <p class="success">Book updated successfully!</p>
    <% } else if ("deleted".equals(message)) { %>
    <p class="success">Book deleted successfully!</p>
    <% } else if ("updatefailed".equals(message)) { %>
    <p class="error">
        Update failed. Check the quantity: it cannot be less than
        the number of copies currently issued.
    </p>
    <% } else if ("deletefailed".equals(message)) { %>
    <p class="error">Book not found or could not be deleted.</p>
    <% } else if ("invalid".equals(message)) { %>
    <p class="error">Please enter valid book details and quantity.</p>
    <% } else if ("error".equals(message)) { %>
    <p class="error">
        Unable to complete the operation. The book may be linked
        to existing reservations or issue records.
    </p>
    <% } %>

    <form class="add-book-form"
          action="<%= request.getContextPath() %>/admin-books"
          method="post">

        <input type="hidden" name="action" value="add">

        <label>Book Title</label>
        <input type="text" name="title" maxlength="255"
               placeholder="Enter book title" required>

        <label>Author</label>
        <input type="text" name="author" maxlength="255"
               placeholder="Enter author name" required>

        <label>ISBN</label>
        <input type="text" name="isbn"
               placeholder="Enter ISBN" required>

        <label>Category</label>
        <select name="categoryId" required>
            <option value="">Select Category</option>

            <% if (categories != null) {
                for (Map<String, Object> category : categories) { %>

            <option value="<%= category.get("categoryId") %>">
                <%= escapeHtml(category.get("categoryName")) %>
            </option>

            <%  }
            } %>
        </select>

        <label>Total Quantity</label>
        <input type="number" name="totalQuantity"
               min="1" required>

        <button type="submit" class="add-btn">
            Add Book
        </button>

    </form>

    <h2 style="text-align:center;">Available Book Records</h2>

    <table>

        <thead>
        <tr>
            <th>Book ID</th>
            <th>Book Details</th>
            <th>Category</th>
            <th>Total Quantity</th>
            <th>Available Quantity</th>
            <th>Actions</th>
        </tr>
        </thead>

        <tbody>

        <% if (books != null && !books.isEmpty()) {
            for (Map<String, Object> book : books) {

                long bookId = (Long) book.get("bookId");
                long selectedCategory =
                        (Long) book.get("categoryId");
        %>

        <tr>

            <td><%= bookId %></td>

            <td>
                <form class="edit-form"
                      action="<%= request.getContextPath() %>/admin-books"
                      method="post">

                    <input type="hidden" name="action" value="update">

                    <input type="hidden" name="bookId"
                           value="<%= bookId %>">

                    <input type="text" name="title"
                           value="<%= escapeHtml(book.get("title")) %>"
                           placeholder="Title" required>

                    <input type="text" name="author"
                           value="<%= escapeHtml(book.get("author")) %>"
                           placeholder="Author" required>

                    <input type="text" name="isbn"
                           value="<%= escapeHtml(book.get("isbn")) %>"
                           placeholder="ISBN" required>
            </td>

            <td>
                <select name="categoryId" required>
                    <% if (categories != null) {
                        for (Map<String, Object> category : categories) {
                            long categoryId =
                                    (Long) category.get("categoryId");
                    %>

                    <option value="<%= categoryId %>"
                            <%= categoryId == selectedCategory
                                    ? "selected" : "" %>>
                        <%= escapeHtml(category.get("categoryName")) %>
                    </option>

                    <%  }
                    } %>
                </select>
            </td>

            <td>
                <input type="number" name="totalQuantity"
                       min="1"
                       value="<%= book.get("totalQuantity") %>"
                       required>
            </td>

            <td>
                <%= book.get("availableQuantity") %>
            </td>

            <td>

                <button type="submit"
                        class="action-btn update-btn">
                    Update
                </button>

                </form>

                <form class="action-form"
                      action="<%= request.getContextPath() %>/admin-books"
                      method="post"
                      onsubmit="return confirm('Are you sure you want to delete this book?');">

                    <input type="hidden" name="action" value="delete">

                    <input type="hidden" name="bookId"
                           value="<%= bookId %>">

                    <button type="submit"
                            class="action-btn delete-btn">
                        Delete
                    </button>

                </form>

            </td>

        </tr>

        <%  }
        } else { %>

        <tr>
            <td colspan="6">No books found.</td>
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
