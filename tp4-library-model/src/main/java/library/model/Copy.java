package library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a physical copy of a {@link Book}.
 */
public class Copy {
    private final String inventoryNumber;
    private final Book book;
    private CopyStatus status;
    private final List<Loan> loans = new ArrayList<>();

    public Copy(String inventoryNumber, Book book) {
        this.inventoryNumber = Objects.requireNonNull(inventoryNumber, "inventoryNumber must not be null");
        this.book = Objects.requireNonNull(book, "book must not be null");
        this.status = CopyStatus.AVAILABLE;
        this.book.addCopy(this);
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public Book getBook() {
        return book;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public boolean isAvailable() {
        return status == CopyStatus.AVAILABLE;
    }

    /**
     * Returns the loan currently in progress for this copy, or {@code null} if none.
     */
    public Loan getCurrentLoan() {
        for (Loan loan : loans) {
            if (loan.isInProgress()) {
                return loan;
            }
        }
        return null;
    }

    public void markLost() {
        if (!isAvailable()) {
            throw new IllegalStateException("Only an available copy can be marked lost");
        }
        this.status = CopyStatus.LOST;
    }

    public void returnCopy(LocalDate returnDate) {
        Loan current = getCurrentLoan();
        if (current == null) {
            throw new IllegalStateException("No loan in progress for this copy");
        }
        current.close(returnDate);
    }

    void setStatus(CopyStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    void addLoan(Loan loan) {
        Objects.requireNonNull(loan, "loan must not be null");
        if (!loans.contains(loan)) {
            loans.add(loan);
        }
    }
}
