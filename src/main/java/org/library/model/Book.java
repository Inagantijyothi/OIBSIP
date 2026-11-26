
package org.library.model;

public class Book {

    private long bookId;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int totalQuantity;
    private int availableQuantity;

    public Book(long bookId, String title, String author,
                String isbn, String category,
                int totalQuantity, int availableQuantity) {

        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    public long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}

