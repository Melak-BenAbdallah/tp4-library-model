package library.model;

import java.util.Objects;

/**
 * Student library member with a maximum of 5 concurrent loans.
 */
public class StudentMember extends Member {
    private static final int MAX_LOANS = 5;
    private final String level;

    public StudentMember(String memberNumber, String name, String email, String level) {
        super(memberNumber, name, email);
        this.level = Objects.requireNonNull(level, "level must not be null");
    }

    public String getLevel() {
        return level;
    }

    @Override
    public int maxLoans() {
        return MAX_LOANS;
    }
}
