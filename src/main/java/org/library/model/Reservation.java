
package org.library.model;

import java.sql.Timestamp;

public class Reservation {

    private long reservationId;
    private long userId;
    private String fullName;
    private long bookId;
    private String bookTitle;
    private Timestamp reservationDate;
    private String status;

    public Reservation(long reservationId,
                       long userId,
                       String fullName,
                       long bookId,
                       String bookTitle,
                       Timestamp reservationDate,
                       String status) {

        this.reservationId = reservationId;
        this.userId = userId;
        this.fullName = fullName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    public long getReservationId() {
        return reservationId;
    }

    public long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Timestamp getReservationDate() {
        return reservationDate;
    }

    public String getStatus() {
        return status;
    }
}

