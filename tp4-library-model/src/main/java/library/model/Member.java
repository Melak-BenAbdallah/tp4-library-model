package library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for library members.
 */
public abstract class Member {
    private final String memberNumber;
    private final String name;
    private final String email;
    private final List<Loan> loans = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();

    protected Member(String memberNumber, String name, String email) {
        this.memberNumber = Objects.requireNonNull(memberNumber, "memberNumber must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    public abstract int maxLoans();

    public int activeLoansCount() {
        int count = 0;
        for (Loan loan : loans) {
            if (loan.isInProgress()) {
                count++;
            }
        }
        return count;
    }

    public boolean canBorrow() {
        return activeLoansCount() < maxLoans();
    }

    /**
     * Creates a new loan if the member can borrow and the copy is available.
     */
    public Loan borrow(Copy copy, LocalDate loanDate) {
        Objects.requireNonNull(copy, "copy must not be null");
        Objects.requireNonNull(loanDate, "loanDate must not be null");
        if (!canBorrow()) {
            throw new IllegalStateException("Member has reached the maximum number of active loans");
        }
        if (!copy.isAvailable()) {
            throw new IllegalStateException("Copy is not available for borrowing");
        }
        return new Loan(this, copy, loanDate);
    }

    /**
     * Creates a reservation for the given book on the specified date.
     */
    public Reservation reserve(Book book, LocalDate date) {
        Objects.requireNonNull(book, "book must not be null");
        Objects.requireNonNull(date, "date must not be null");
        return new Reservation(this, book, date);
    }

    void addLoan(Loan loan) {
        Objects.requireNonNull(loan, "loan must not be null");
        if (!loans.contains(loan)) {
            loans.add(loan);
        }
    }

    void addReservation(Reservation reservation) {
        Objects.requireNonNull(reservation, "reservation must not be null");
        if (!reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }
}
