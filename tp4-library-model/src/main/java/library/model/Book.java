package library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a library book with at least one author, physical copies, and reservations.
 */
public class Book {
    private final String isbn;
    private final String title;
    private final int year;
    private final List<Author> authors = new ArrayList<>();
    private final List<Copy> copies = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    public Book(String isbn, String title, int year, Author firstAuthor) {
        this.isbn = Objects.requireNonNull(isbn, "isbn must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.year = year;
        addAuthor(Objects.requireNonNull(firstAuthor, "firstAuthor must not be null"));
    }

    public Book(String isbn, String title, int year, List<Author> authors) {
        this.isbn = Objects.requireNonNull(isbn, "isbn must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.year = year;
        Objects.requireNonNull(authors, "authors must not be null");
        if (authors.isEmpty()) {
            throw new IllegalArgumentException("A book must have at least one author");
        }
        for (Author author : authors) {
            addAuthor(author);
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public List<Author> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public List<Copy> getCopies() {
        return Collections.unmodifiableList(copies);
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    /**
     * Adds an author to this book and updates the inverse association on the author.
     */
    public void addAuthor(Author author) {
        Objects.requireNonNull(author, "author must not be null");
        if (!authors.contains(author)) {
            authors.add(author);
            author.addBook(this);
        }
    }

    void addCopy(Copy copy) {
        Objects.requireNonNull(copy, "copy must not be null");
        if (!copies.contains(copy)) {
            copies.add(copy);
        }
    }

    void addReservation(Reservation reservation) {
        Objects.requireNonNull(reservation, "reservation must not be null");
        if (!reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }

    /**
     * Returns the number of copies currently available for borrowing.
     */
    public int availableCopies() {
        int count = 0;
        for (Copy copy : copies) {
            if (copy.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the oldest reservation for this book, or {@code null} if none exist.
     */
    public Reservation firstReservation() {
        return reservations.isEmpty() ? null : reservations.get(0);
    }
}
