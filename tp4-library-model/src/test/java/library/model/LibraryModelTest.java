package library.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryModelTest {

    @Test
    void authorAndBookBidirectionalManyToMany() {
        Author a1 = new Author("Robert C. Martin");
        Author a2 = new Author("Martin Fowler");

        Book book = new Book("978-0132350884", "Clean Code", 2008, a1);
        book.addAuthor(a2);
        // Duplicate add should be ignored
        book.addAuthor(a2);

        assertEquals(List.of(a1, a2), book.getAuthors());
        assertEquals(List.of(book), a1.getBooks());
        assertEquals(List.of(book), a2.getBooks());

        assertThrows(UnsupportedOperationException.class, () -> book.getAuthors().add(a1));
        assertThrows(UnsupportedOperationException.class, () -> a1.getBooks().add(book));
        assertThrows(IllegalArgumentException.class,
                () -> new Book("123", "No Author", 2020, List.of()));
    }

    @Test
    void copyRegistrationAvailabilityAndMarkLost() {
        Author author = new Author("Joshua Bloch");
        Book book = new Book("978-0134685991", "Effective Java", 2018, author);

        Copy c1 = new Copy("INV-001", book);
        Copy c2 = new Copy("INV-002", book);

        assertEquals(2, book.getCopies().size());
        assertEquals(2, book.availableCopies());
        assertTrue(c1.isAvailable());
        assertEquals(CopyStatus.AVAILABLE, c1.getStatus());
        assertNull(c1.getCurrentLoan());

        c2.markLost();
        assertEquals(CopyStatus.LOST, c2.getStatus());
        assertFalse(c2.isAvailable());
        assertEquals(1, book.availableCopies());
    }

    @Test
    void studentBorrowLimitAndLoanLifecycle() {
        Author author = new Author("Erich Gamma");
        Book book = new Book("978-0201633610", "Design Patterns", 1994, author);
        StudentMember student = new StudentMember("S001", "Alice", "alice@uni.edu", "L3");

        assertEquals(5, student.maxLoans());
        assertTrue(student.canBorrow());

        LocalDate today = LocalDate.of(2026, 11, 1);
        for (int i = 1; i <= 5; i++) {
            Copy copy = new Copy("INV-" + i, book);
            Loan loan = student.borrow(copy, today);
            assertEquals(today.plusDays(14), loan.getDueDate());
            assertTrue(loan.isInProgress());
            assertEquals(CopyStatus.BORROWED, copy.getStatus());
            assertEquals(loan, copy.getCurrentLoan());
        }

        assertEquals(5, student.activeLoansCount());
        assertFalse(student.canBorrow());

        Copy extraCopy = new Copy("INV-6", book);
        assertThrows(IllegalStateException.class, () -> student.borrow(extraCopy, today));

        Loan firstLoan = student.getLoans().get(0);
        assertFalse(firstLoan.isLate(today.plusDays(14)));
        assertTrue(firstLoan.isLate(today.plusDays(15)));

        firstLoan.close(today.plusDays(10));
        assertFalse(firstLoan.isInProgress());
        assertFalse(firstLoan.isLate(today.plusDays(20)));
        assertTrue(firstLoan.getCopy().isAvailable());
        assertNull(firstLoan.getCopy().getCurrentLoan());
        assertTrue(student.canBorrow());
    }

    @Test
    void teacherBorrowLimitAndUnavailableCopyCheck() {
        Author author = new Author("Donald Knuth");
        Book book = new Book("978-0201896831", "TAOCP", 1997, author);
        TeacherMember teacher = new TeacherMember("T001", "Prof. Bob", "bob@uni.edu", "CS");

        assertEquals(10, teacher.maxLoans());
        Copy lostCopy = new Copy("INV-LOST", book);
        lostCopy.markLost();

        assertThrows(IllegalStateException.class,
                () -> teacher.borrow(lostCopy, LocalDate.of(2026, 11, 1)));
    }

    @Test
    void reservationsOrderAndFirstReservation() {
        Author author = new Author("Bjarne Stroustrup");
        Book book = new Book("978-0321563842", "C++ Programming Language", 2013, author);
        StudentMember s1 = new StudentMember("S001", "Alice", "alice@uni.edu", "M1");
        TeacherMember t1 = new TeacherMember("T001", "Bob", "bob@uni.edu", "CS");

        assertNull(book.firstReservation());

        Reservation r1 = s1.reserve(book, LocalDate.of(2026, 11, 1));
        Reservation r2 = t1.reserve(book, LocalDate.of(2026, 11, 2));

        assertEquals(r1, book.firstReservation());
        assertEquals(List.of(r1, r2), book.getReservations());
        assertEquals(List.of(r1), s1.getReservations());
        assertEquals(List.of(r2), t1.getReservations());

        assertThrows(UnsupportedOperationException.class, () -> book.getReservations().add(r1));
        assertThrows(UnsupportedOperationException.class, () -> s1.getReservations().add(r2));
    }
}
