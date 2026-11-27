package library.model;

import java.util.Objects;

/**
 * Teacher library member with a maximum of 10 concurrent loans.
 */
public class TeacherMember extends Member {
    private static final int MAX_LOANS = 10;
    private final String department;

    public TeacherMember(String memberNumber, String name, String email, String department) {
        super(memberNumber, name, email);
        this.department = Objects.requireNonNull(department, "department must not be null");
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public int maxLoans() {
        return MAX_LOANS;
    }
}
