package library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a book author, linked bidirectionally to {@link Book}.
 */
public class Author {
    private final String name;
    private final List<Book> books = new ArrayList<>();

    public Author(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    /**
     * Package-private so only {@link Book} can maintain the bidirectional link.
     */
    void addBook(Book book) {
        Objects.requireNonNull(book, "book must not be null");
        if (!books.contains(book)) {
            books.add(book);
        }
    }
}
