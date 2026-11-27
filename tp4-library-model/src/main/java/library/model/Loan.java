package library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a loan of a {@link Copy} to a {@link Member}.
 * Package-private constructor ensures loans are only created via {@link Member#borrow(Copy, LocalDate)}.
 */
public class Loan {
    private static final int LOAN_DURATION_DAYS = 14;

    private final Member member;
    private final Copy copy;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    Loan(Member member, Copy copy, LocalDate loanDate) {
        this.member = Objects.requireNonNull(member, "member must not be null");
        this.copy = Objects.requireNonNull(copy, "copy must not be null");
        this.loanDate = Objects.requireNonNull(loanDate, "loanDate must not be null");
        this.dueDate = loanDate.plusDays(LOAN_DURATION_DAYS);
        this.returnDate = null;

        this.member.addLoan(this);
        this.copy.addLoan(this);
        this.copy.setStatus(CopyStatus.BORROWED);
    }

    public Member getMember() {
        return member;
    }

    public Copy getCopy() {
        return copy;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isInProgress() {
        return returnDate == null;
    }

    /**
     * Closes the loan on {@code returnDate} and makes the copy available again.
     */
    public void close(LocalDate returnDate) {
        Objects.requireNonNull(returnDate, "returnDate must not be null");
        if (!isInProgress()) {
            throw new IllegalStateException("Loan is already closed");
        }
        this.returnDate = returnDate;
        this.copy.setStatus(CopyStatus.AVAILABLE);
    }

    /**
     * Returns whether the loan is late as of {@code today} (or was returned after {@code dueDate}).
     */
    public boolean isLate(LocalDate today) {
        Objects.requireNonNull(today, "today must not be null");
        if (isInProgress()) {
            return today.isAfter(dueDate);
        }
        return returnDate.isAfter(dueDate);
    }
}
