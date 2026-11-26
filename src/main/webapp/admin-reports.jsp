
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || !role.equalsIgnoreCase("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login.html");
        return;
    }

    Map<String, Object> bookStats =
            (Map<String, Object>) request.getAttribute("bookStats");

    Map<String, Object> userStats =
            (Map<String, Object>) request.getAttribute("userStats");

    Map<String, Object> issueStats =
            (Map<String, Object>) request.getAttribute("issueStats");

    Map<String, Object> fineStats =
            (Map<String, Object>) request.getAttribute("fineStats");

    Map<String, Object> reservationStats =
            (Map<String, Object>) request.getAttribute("reservationStats");

    List<Map<String, Object>> categoryStats =
            (List<Map<String, Object>>) request.getAttribute("categoryStats");

    if (bookStats == null) bookStats = new HashMap<>();
    if (userStats == null) userStats = new HashMap<>();
    if (issueStats == null) issueStats = new HashMap<>();
    if (fineStats == null) fineStats = new HashMap<>();
    if (reservationStats == null) reservationStats = new HashMap<>();
    if (categoryStats == null) categoryStats = new ArrayList<>();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Reports</title>

    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/css/style.css">

    <style>
        .reports-container {
            max-width: 1100px;
            margin: 30px auto;
            padding: 20px;
        }

        .reports-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 18px;
            margin: 25px 0;
        }

        .report-card {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.12);
            text-align: center;
        }

        .report-card h3 {
            font-size: 16px;
            color: #555;
            margin-bottom: 12px;
        }

        .report-card p {
            font-size: 26px;
            font-weight: bold;
            color: #1d3557;
            margin: 0;
        }

        .report-section {
            background: white;
            padding: 20px;
            margin-top: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.10);
            overflow-x: auto;
        }

        .report-section h2 {
            color: #1d3557;
            margin-bottom: 15px;
        }

        .report-table {
            width: 100%;
            border-collapse: collapse;
        }

        .report-table th,
        .report-table td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
        }

        .report-table th {
            background: #1d3557;
            color: white;
        }

        .report-actions {
            display: flex;
            gap: 12px;
            margin-top: 25px;
            flex-wrap: wrap;
        }

        .print-button,
        .back-button {
            display: inline-block;
            padding: 11px 20px;
            background: #1d3557;
            color: white;
            text-decoration: none;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 15px;
        }

        .print-button:hover,
        .back-button:hover {
            background: #457b9d;
        }

        @media print {
            body {
                background: white;
            }

            .reports-container {
                max-width: 100%;
                margin: 0;
                padding: 10px;
            }

            .report-card,
            .report-section {
                box-shadow: none;
                border: 1px solid #ddd;
                break-inside: avoid;
            }

            .report-actions {
                display: none !important;
            }

            .report-table th {
                background: #ddd !important;
                color: black !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }
        }
    </style>
</head>

<body>

<div class="reports-container">

    <h1>Library Reports & Analytics</h1>

    <p>Overview of library books, users, issues, fines and reservations.</p>

    <!-- BOOK AND USER STATISTICS -->

    <div class="reports-grid">

        <div class="report-card">
            <h3>Total Book Copies</h3>
            <p><%= bookStats.getOrDefault("totalBooks", 0) %></p>
        </div>

        <div class="report-card">
            <h3>Available Copies</h3>
            <p><%= bookStats.getOrDefault("availableBooks", 0) %></p>
        </div>

        <div class="report-card">
            <h3>Issued / Unavailable Copies</h3>
            <p><%= bookStats.getOrDefault("unavailableBooks", 0) %></p>
        </div>

        <div class="report-card">
            <h3>Total Users</h3>
            <p><%= userStats.getOrDefault("totalUsers", 0) %></p>
        </div>

        <div class="report-card">
            <h3>Total Admins</h3>
            <p><%= userStats.getOrDefault("totalAdmins", 0) %></p>
        </div>

    </div>

    <!-- ISSUE AND RETURN STATISTICS -->

    <div class="report-section">

        <h2>Issue & Return Statistics</h2>

        <table class="report-table">
            <tr>
                <th>Category</th>
                <th>Count</th>
            </tr>

            <tr>
                <td>Currently Issued</td>
                <td><%= issueStats.getOrDefault("issuedBooks", 0) %></td>
            </tr>

            <tr>
                <td>Returned Books</td>
                <td><%= issueStats.getOrDefault("returnedBooks", 0) %></td>
            </tr>

        </table>

    </div>

    <!-- FINE STATISTICS -->

    <div class="report-section">

        <h2>Fine Statistics</h2>

        <table class="report-table">

            <tr>
                <th>Fine Category</th>
                <th>Amount (₹)</th>
            </tr>

            <tr>
                <td>Total Fines</td>
                <td><%= fineStats.getOrDefault("totalFines", 0) %></td>
            </tr>

            <tr>
                <td>Paid Fines</td>
                <td><%= fineStats.getOrDefault("paidFines", 0) %></td>
            </tr>

            <tr>
                <td>Unpaid Fines</td>
                <td><%= fineStats.getOrDefault("unpaidFines", 0) %></td>
            </tr>

        </table>

    </div>

    <!-- RESERVATION STATISTICS -->

    <div class="report-section">

        <h2>Reservation Statistics</h2>

        <table class="report-table">

            <tr>
                <th>Status</th>
                <th>Count</th>
            </tr>

            <tr>
                <td>Pending</td>
                <td><%= reservationStats.getOrDefault("pendingReservations", 0) %></td>
            </tr>

            <tr>
                <td>Approved</td>
                <td><%= reservationStats.getOrDefault("approvedReservations", 0) %></td>
            </tr>

            <tr>
                <td>Rejected</td>
                <td><%= reservationStats.getOrDefault("rejectedReservations", 0) %></td>
            </tr>

            <tr>
                <td>Cancelled</td>
                <td><%= reservationStats.getOrDefault("cancelledReservations", 0) %></td>
            </tr>

        </table>

    </div>

    <!-- CATEGORY-WISE BOOK STATISTICS -->

    <div class="report-section">

        <h2>Category-wise Book Statistics</h2>

        <table class="report-table">

            <tr>
                <th>Category</th>
                <th>Book Titles</th>
                <th>Total Copies</th>
                <th>Available Copies</th>
            </tr>

            <% if (!categoryStats.isEmpty()) {
                for (Map<String, Object> category : categoryStats) {
            %>

            <tr>
                <td><%= category.getOrDefault("categoryName", "-") %></td>
                <td><%= category.getOrDefault("bookTitles", 0) %></td>
                <td><%= category.getOrDefault("totalCopies", 0) %></td>
                <td><%= category.getOrDefault("availableCopies", 0) %></td>
            </tr>

            <%
                }
            } else {
            %>

            <tr>
                <td colspan="4">No category data available.</td>
            </tr>

            <% } %>

        </table>

    </div>

    <!-- PRINT AND BACK BUTTONS -->

    <div class="report-actions">

        <button type="button"
                class="print-button"
                onclick="window.print()">
            Print / Save as PDF
        </button>

        <button type="button"
                class="print-button"
                onclick="exportReportsCSV()">
            Export CSV
        </button>

        <a class="back-button"
           href="<%= request.getContextPath() %>/admin-dashboard.jsp">
            Back to Admin Dashboard
        </a>

    </div>

</div>

<script>

    function exportReportsCSV() {

        let csv = [];

        function escapeCSV(value) {
            return '"' + String(value)
                .replace(/"/g, '""')
                .trim() + '"';
        }

        // Export report heading
        csv.push(escapeCSV("LIBRARY MANAGEMENT SYSTEM - REPORTS"));
        csv.push("");

        // Export summary cards
        csv.push(escapeCSV("BOOK AND USER STATISTICS"));
        csv.push(escapeCSV("Category") + "," + escapeCSV("Count"));

        document.querySelectorAll(".report-card").forEach(card => {

            let heading = card.querySelector("h3");
            let value = card.querySelector("p");

            if (heading && value) {
                csv.push(
                    escapeCSV(heading.innerText) + "," +
                    escapeCSV(value.innerText)
                );
            }
        });

        csv.push("");

        // Export all report tables
        document.querySelectorAll(".report-section").forEach(section => {

            let heading = section.querySelector("h2");

            if (heading) {
                csv.push(escapeCSV(heading.innerText));
            }

            let table = section.querySelector("table");

            if (table) {

                table.querySelectorAll("tr").forEach(row => {

                    let cells = Array.from(row.querySelectorAll("th, td"));

                    let rowData = cells.map(cell =>
                        escapeCSV(cell.innerText)
                    );

                    csv.push(rowData.join(","));
                });
            }

            csv.push("");
        });

        // Create downloadable CSV file
        let csvContent = "\uFEFF" + csv.join("\r\n");

        let blob = new Blob([csvContent], {
            type: "text/csv;charset=utf-8;"
        });

        let url = URL.createObjectURL(blob);

        let link = document.createElement("a");

        link.href = url;
        link.download = "Library_Management_Reports.csv";

        document.body.appendChild(link);

        link.click();

        document.body.removeChild(link);

        URL.revokeObjectURL(url);
    }

</script>

</body>
</html>
