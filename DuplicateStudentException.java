/**
 * Custom checked exception thrown when attempting to add a student
 * with an ID that already exists.
 */
public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(String message) {
        super(message);
    }
}
