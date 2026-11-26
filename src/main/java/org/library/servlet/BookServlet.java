
package org.library.servlet;

import org.library.dao.BookDAO;
import org.library.model.Book;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !"USER".equalsIgnoreCase(
                        (String) session.getAttribute("role"))) {

            response.sendRedirect(
                    request.getContextPath() + "/login.html"
            );
            return;
        }

        // Get search keyword from the search box
        String keyword = request.getParameter("keyword");

        // Retrieve matching books
        List<Book> books = bookDAO.getAvailableBooks(keyword);

        request.setAttribute("books", books);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher(
                "/user-books.jsp"
        ).forward(request, response);
    }
}
