package library.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    private static final LocalDate DAY = LocalDate.of(2026, 10, 1);

    private Author author;
    private Book book;
    private Copy copy;
    private Member student;

    @BeforeEach
    void setUp() {
        author = new Author("First author");
        book = new Book("978-0-00-000001-1", "Software Engineering", 2016, author);
        copy = new Copy("C-001", book);
        student = new StudentMember("M-01", "Student 1", "s1@example.org", "L3");
    }

    @Test
    void bookAndAuthorKnowEachOther() {
        assertTrue(book.getAuthors().contains(author));
        assertTrue(author.getBooks().contains(book));
    }

    @Test
    void newCopyIsAvailableAndKnownByBook() {
        assertTrue(copy.isAvailable());
        assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        assertTrue(book.getCopies().contains(copy));
        assertEquals(book, copy.getBook());
        assertEquals(1, book.availableCopies());
    }

    @Test
    void borrowingLinksLoanAndChangesCopyStatus() {
        Loan loan = student.borrow(copy, DAY);

        assertTrue(student.getLoans().contains(loan));
        assertTrue(copy.getLoans().contains(loan));
        assertEquals(student, loan.getMember());
        assertEquals(copy, loan.getCopy());
        assertEquals(CopyStatus.BORROWED, copy.getStatus());
        assertFalse(copy.isAvailable());
        assertEquals(loan, copy.getCurrentLoan());
    }

    @Test
    void studentCannotBorrowSixthCopyWhileTeacherCanBorrowTen() {
        for (int i = 1; i <= 5; i++) {
            Copy c = new Copy("S-" + i, book);
            student.borrow(c, DAY);
        }
        assertFalse(student.canBorrow());
        Copy sixth = new Copy("S-6", book);
        assertThrows(IllegalStateException.class, () -> student.borrow(sixth, DAY));

        Member teacher = new TeacherMember("T-01", "Teacher 1", "t1@example.org", "CS");
        for (int i = 1; i <= 10; i++) {
            Copy c = new Copy("T-" + i, book);
            assertNotNull(teacher.borrow(c, DAY));
        }
        assertEquals(10, teacher.activeLoansCount());
        assertFalse(teacher.canBorrow());
        Copy eleventh = new Copy("T-11", book);
        assertThrows(IllegalStateException.class, () -> teacher.borrow(eleventh, DAY));
    }

    @Test
    void copyCannotBeBorrowedTwice() {
        student.borrow(copy, DAY);
        Member otherStudent = new StudentMember("M-02", "Student 2", "s2@example.org", "M1");

        assertThrows(IllegalStateException.class, () -> otherStudent.borrow(copy, DAY));
    }

    @Test
    void closingLoanMakesCopyAvailableAgainAndKeepsHistory() {
        Loan loan = student.borrow(copy, DAY);
        assertFalse(copy.isAvailable());

        loan.close(DAY.plusDays(5));

        assertTrue(copy.isAvailable());
        assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        assertNull(copy.getCurrentLoan());
        assertFalse(loan.isInProgress());
        assertTrue(student.getLoans().contains(loan));
        assertTrue(copy.getLoans().contains(loan));
    }

    @Test
    void loanIsLateFromDayAfterDueDate() {
        Loan loan = student.borrow(copy, DAY);
        LocalDate dueDate = DAY.plusDays(14);

        assertEquals(dueDate, loan.getDueDate());
        assertFalse(loan.isLate(dueDate));
        assertTrue(loan.isLate(dueDate.plusDays(1)));
    }

    @Test
    void collectionGettersReturnUnmodifiableViews() {
        Loan loan = student.borrow(copy, DAY);
        Reservation reservation = student.reserve(book, DAY);

        assertThrows(UnsupportedOperationException.class, () -> author.getBooks().add(book));
        assertThrows(UnsupportedOperationException.class, () -> book.getAuthors().add(author));
        assertThrows(UnsupportedOperationException.class, () -> book.getCopies().add(copy));
        assertThrows(UnsupportedOperationException.class, () -> book.getReservations().add(reservation));
        assertThrows(UnsupportedOperationException.class, () -> copy.getLoans().add(loan));
        assertThrows(UnsupportedOperationException.class, () -> student.getLoans().add(loan));
        assertThrows(UnsupportedOperationException.class, () -> student.getReservations().add(reservation));
    }

    @Test
    void reservationsAreKeptInOrder() {
        Member teacher = new TeacherMember("T-01", "Teacher 1", "t1@example.org", "CS");

        Reservation r1 = student.reserve(book, DAY);
        Reservation r2 = teacher.reserve(book, DAY.plusDays(1));

        assertEquals(List.of(r1, r2), book.getReservations());
        assertEquals(r1, book.firstReservation());
        assertEquals(List.of(r1), student.getReservations());
        assertEquals(List.of(r2), teacher.getReservations());
    }

    @Test
    void onlyAvailableCopyCanBeMarkedLost() {
        student.borrow(copy, DAY);
        assertThrows(IllegalStateException.class, () -> copy.markLost());

        Copy availableCopy = new Copy("C-002", book);
        availableCopy.markLost();
        assertEquals(CopyStatus.LOST, availableCopy.getStatus());
        assertFalse(availableCopy.isAvailable());

        assertThrows(IllegalStateException.class, () -> availableCopy.markLost());
    }

    @Test
    void objectDiagramScenario() {
        Author a1 = new Author("Author 1");
        Author a2 = new Author("Author 2");
        Book b = new Book("978-0-00-000001-1", "Software Engineering", 2016, a1);
        b.addAuthor(a2);

        Copy c1 = new Copy("C-001", b);
        Copy c2 = new Copy("C-002", b);

        StudentMember s = new StudentMember("M-01", "Student 1", "s1@example.org", "L3");
        TeacherMember t = new TeacherMember("M-02", "Teacher 1", "t1@example.org", "CS");

        Loan l1 = s.borrow(c1, DAY);
        Loan l2 = s.borrow(c2, DAY);
        Reservation r = t.reserve(b, DAY.plusDays(1));

        // Book <-> Authors
        assertEquals(List.of(a1, a2), b.getAuthors());
        assertEquals(List.of(b), a1.getBooks());
        assertEquals(List.of(b), a2.getBooks());

        // Book <-> Copies
        assertEquals(List.of(c1, c2), b.getCopies());
        assertEquals(b, c1.getBook());
        assertEquals(b, c2.getBook());
        assertEquals(0, b.availableCopies());

        // Student <-> Loans <-> Copies
        assertEquals(List.of(l1, l2), s.getLoans());
        assertEquals(s, l1.getMember());
        assertEquals(s, l2.getMember());
        assertEquals(c1, l1.getCopy());
        assertEquals(c2, l2.getCopy());
        assertEquals(List.of(l1), c1.getLoans());
        assertEquals(List.of(l2), c2.getLoans());
        assertEquals(CopyStatus.BORROWED, c1.getStatus());
        assertEquals(CopyStatus.BORROWED, c2.getStatus());
        assertEquals(DAY.plusDays(14), l1.getDueDate());
        assertEquals(DAY.plusDays(14), l2.getDueDate());

        // Teacher <-> Reservation <-> Book
        assertEquals(List.of(r), t.getReservations());
        assertEquals(List.of(r), b.getReservations());
        assertEquals(t, r.getMember());
        assertEquals(b, r.getBook());
        assertEquals(r, b.firstReservation());
    }
}
