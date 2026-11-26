
package org.library.servlet;

import org.library.config.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin-books")
public class AdminBooksServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request,
                            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"ADMIN".equalsIgnoreCase(
                        (String) session.getAttribute("role"))) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html");
            return false;
        }

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        List<Map<String, Object>> books = new ArrayList<>();
        List<Map<String, Object>> categories = new ArrayList<>();

        String bookSql =
                "SELECT b.book_id, b.title, b.author, b.isbn, " +
                        "b.category_id, c.category_name, " +
                        "b.total_quantity, b.available_quantity " +
                        "FROM books b " +
                        "LEFT JOIN categories c ON b.category_id = c.category_id " +
                        "ORDER BY b.book_id DESC";

        String categorySql =
                "SELECT category_id, category_name " +
                        "FROM categories ORDER BY category_name";

        try (Connection con = DBConnection.getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(bookSql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> book = new HashMap<>();

                    book.put("bookId", rs.getLong("book_id"));
                    book.put("title", rs.getString("title"));
                    book.put("author", rs.getString("author"));
                    book.put("isbn", rs.getString("isbn"));
                    book.put("categoryId", rs.getLong("category_id"));
                    book.put("categoryName", rs.getString("category_name"));
                    book.put("totalQuantity", rs.getInt("total_quantity"));
                    book.put("availableQuantity",
                            rs.getInt("available_quantity"));

                    books.add(book);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(categorySql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> category = new HashMap<>();

                    category.put("categoryId",
                            rs.getLong("category_id"));
                    category.put("categoryName",
                            rs.getString("category_name"));

                    categories.add(category);
                }
            }

            request.setAttribute("books", books);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/admin-books.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load books.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try (Connection con = DBConnection.getConnection()) {

            if ("add".equals(action)) {

                String title = request.getParameter("title");
                String author = request.getParameter("author");
                String isbn = request.getParameter("isbn");
                long categoryId = Long.parseLong(
                        request.getParameter("categoryId"));
                int quantity = Integer.parseInt(
                        request.getParameter("totalQuantity"));

                if (title == null || title.trim().isEmpty() ||
                        author == null || author.trim().isEmpty() ||
                        isbn == null || isbn.trim().isEmpty() ||
                        categoryId <= 0 || quantity <= 0) {

                    redirect(request, response, "invalid");
                    return;
                }

                String sql =
                        "INSERT INTO books " +
                                "(title, author, isbn, category_id, " +
                                "total_quantity, available_quantity) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, title.trim());
                    ps.setString(2, author.trim());
                    ps.setString(3, isbn.trim());
                    ps.setLong(4, categoryId);
                    ps.setInt(5, quantity);
                    ps.setInt(6, quantity);

                    ps.executeUpdate();
                }

                redirect(request, response, "added");

            } else if ("update".equals(action)) {

                long bookId = Long.parseLong(
                        request.getParameter("bookId"));
                String title = request.getParameter("title");
                String author = request.getParameter("author");
                String isbn = request.getParameter("isbn");
                long categoryId = Long.parseLong(
                        request.getParameter("categoryId"));
                int quantity = Integer.parseInt(
                        request.getParameter("totalQuantity"));

                if (title == null || title.trim().isEmpty() ||
                        author == null || author.trim().isEmpty() ||
                        isbn == null || isbn.trim().isEmpty() ||
                        categoryId <= 0 || quantity <= 0) {

                    redirect(request, response, "invalid");
                    return;
                }

                String sql =
                        "UPDATE books SET title = ?, author = ?, " +
                                "isbn = ?, category_id = ?, " +
                                "available_quantity = available_quantity + " +
                                "(? - total_quantity), total_quantity = ? " +
                                "WHERE book_id = ? " +
                                "AND available_quantity + " +
                                "(? - total_quantity) >= 0";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, title.trim());
                    ps.setString(2, author.trim());
                    ps.setString(3, isbn.trim());
                    ps.setLong(4, categoryId);
                    ps.setInt(5, quantity);
                    ps.setInt(6, quantity);
                    ps.setLong(7, bookId);
                    ps.setInt(8, quantity);

                    int rows = ps.executeUpdate();

                    if (rows == 0) {
                        redirect(request, response, "updatefailed");
                        return;
                    }
                }

                redirect(request, response, "updated");

            } else if ("delete".equals(action)) {

                long bookId = Long.parseLong(
                        request.getParameter("bookId"));

                String sql = "DELETE FROM books WHERE book_id = ?";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setLong(1, bookId);

                    int rows = ps.executeUpdate();

                    redirect(request, response,
                            rows > 0 ? "deleted" : "deletefailed");
                }

            } else {
                redirect(request, response, "invalid");
            }

        } catch (NumberFormatException e) {
            redirect(request, response, "invalid");
        } catch (Exception e) {
            getServletContext().log(
                    "Error processing admin book action.", e);

            redirect(request, response, "error");
        }
    }

    private void redirect(HttpServletRequest request,
                          HttpServletResponse response,
                          String message) throws IOException {

        response.sendRedirect(
                request.getContextPath() +
                        "/admin-books?message=" + message);
    }
}
