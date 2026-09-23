/**
 * Custom checked exception thrown when a student record cannot be found.
 */
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
