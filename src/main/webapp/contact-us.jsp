
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("USER")) {
        response.sendRedirect("login.html");
        return;
    }

    String message = request.getParameter("message");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Contact Library</title>

    <link rel="stylesheet" href="css/style.css">

    <style>
        .contact-container {
            max-width: 550px;
            margin: 30px auto;
            padding: 25px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .contact-container label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
        }

        .contact-container input,
        .contact-container textarea {
            width: 100%;
            padding: 12px;
            margin-top: 7px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .contact-container textarea {
            min-height: 130px;
            resize: vertical;
        }

        .contact-container button,
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

        .contact-container button:hover,
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
    <p>Contact Library Administrator</p>
</header>

<main class="contact-container">

    <h2>Submit Your Query</h2>

    <% if ("success".equals(message)) { %>
    <p class="success">
        Your message has been submitted successfully!
    </p>
    <% } else if ("failed".equals(message)) { %>
    <p class="error">
        Unable to submit your message. Please try again.
    </p>
    <% } else if ("invalid".equals(message)) { %>
    <p class="error">
        Please enter a valid subject and message.
    </p>
    <% } %>

    <form action="<%= request.getContextPath() %>/contact-us"
          method="post">

        <label>Subject</label>

        <input type="text"
               name="subject"
               maxlength="200"
               placeholder="Enter your subject"
               required>

        <label>Your Message</label>

        <textarea name="message"
                  placeholder="Write your query here..."
                  required></textarea>

        <button type="submit">
            Submit Message
        </button>

    </form>

    <a href="<%= request.getContextPath() %>/user-dashboard.jsp"
       class="back-btn">
        Back to Dashboard
    </a>

</main>

<footer>
    <p>Library Management System | User Panel</p>
</footer>

</body>
</html>
