package library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a reservation of a {@link Book} by a {@link Member}.
 * Package-private constructor ensures reservations are only created via {@link Member#reserve(Book, LocalDate)}.
 */
public class Reservation {
    private final Member member;
    private final Book book;
    private final LocalDate date;

    Reservation(Member member, Book book, LocalDate date) {
        this.member = Objects.requireNonNull(member, "member must not be null");
        this.book = Objects.requireNonNull(book, "book must not be null");
        this.date = Objects.requireNonNull(date, "date must not be null");

        this.member.addReservation(this);
        this.book.addReservation(this);
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getDate() {
        return date;
    }
}
