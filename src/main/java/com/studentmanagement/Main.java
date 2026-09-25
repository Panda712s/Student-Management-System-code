package com.studentmanagement;

import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.Course;
import com.studentmanagement.model.Registration;
import com.studentmanagement.model.Student;
import com.studentmanagement.service.StudentService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

/**
 * Command-line interface for the Student Management System.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentService service = new StudentService();

    /** A menu action that may hit the database. */
    @FunctionalInterface
    private interface Action {
        void run() throws SQLException;
    }

    public static void main(String[] args) {
        if (!canConnect()) {
            return;
        }

        try {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = prompt("Choose an option: ");
                System.out.println();

                switch (choice) {
                    case "1" -> execute(Main::addStudent);
                    case "2" -> execute(Main::viewStudents);
                    case "3" -> execute(Main::searchStudent);
                    case "4" -> execute(Main::updateStudent);
                    case "5" -> execute(Main::deleteStudent);
                    case "6" -> execute(Main::addCourse);
                    case "7" -> execute(Main::registerStudent);
                    case "8" -> execute(Main::viewRegistrations);
                    case "9" -> running = false;
                    default -> System.out.println("Invalid option. Please enter a number from 1 to 9.");
                }
            }
        } catch (NoSuchElementException e) {
            // input stream closed (e.g. Ctrl+D / Ctrl+Z) - fall through to exit
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=================================");
        System.out.println("      STUDENT MANAGEMENT SYSTEM");
        System.out.println("=================================");
        System.out.println();
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Add Course");
        System.out.println("7. Register Student");
        System.out.println("8. View Registrations");
        System.out.println("9. Exit");
        System.out.println();
    }

    // ---------------------------------------------------------------- actions

    private static void addStudent() throws SQLException {
        System.out.println("--- Add Student ---");
        String name = prompt("Name: ");
        String email = prompt("Email: ");
        String phone = prompt("Phone (optional): ");

        Student student = service.addStudent(name, email, phone);
        System.out.println("Student added successfully with ID " + student.getId() + ".");
    }

    private static void viewStudents() throws SQLException {
        System.out.println("--- All Students ---");
        printStudents(service.getAllStudents());
    }

    private static void searchStudent() throws SQLException {
        System.out.println("--- Search Student ---");
        String input = prompt("Enter a student ID, or part of a name/email: ");

        Integer id = parseIntOrNull(input);
        if (id != null) {
            Optional<Student> student = service.findStudent(id);
            printStudents(student.map(List::of).orElse(List.of()));
        } else {
            printStudents(service.searchStudents(input));
        }
    }

    private static void updateStudent() throws SQLException {
        System.out.println("--- Update Student ---");
        int id = promptInt("Student ID: ");

        Optional<Student> found = service.findStudent(id);
        if (found.isEmpty()) {
            System.out.println("No student with ID " + id + ".");
            return;
        }

        Student student = found.get();
        System.out.println("Press Enter to keep the current value.");
        student.setName(promptOrKeep("Name", student.getName()));
        student.setEmail(promptOrKeep("Email", student.getEmail()));
        student.setPhone(promptOrKeep("Phone", student.getPhone()));

        if (service.updateStudent(student)) {
            System.out.println("Student updated successfully.");
        } else {
            System.out.println("Student was not updated.");
        }
    }

    private static void deleteStudent() throws SQLException {
        System.out.println("--- Delete Student ---");
        int id = promptInt("Student ID: ");

        Optional<Student> found = service.findStudent(id);
        if (found.isEmpty()) {
            System.out.println("No student with ID " + id + ".");
            return;
        }

        String answer = prompt("Delete " + found.get().getName()
                + " and all their registrations? (y/n): ");
        if (!answer.equalsIgnoreCase("y")) {
            System.out.println("Delete cancelled.");
            return;
        }

        if (service.deleteStudent(id)) {
            System.out.println("Student deleted successfully.");
        }
    }

    private static void addCourse() throws SQLException {
        System.out.println("--- Add Course ---");
        String name = prompt("Course name: ");
        String code = prompt("Course code: ");

        Course course = service.addCourse(name, code);
        System.out.println("Course " + course.getCourseCode() + " added with ID " + course.getId() + ".");
    }

    private static void registerStudent() throws SQLException {
        System.out.println("--- Register Student for Courses ---");
        List<Course> courses = service.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("There are no courses yet. Add a course first (option 6).");
            return;
        }

        int studentId = promptInt("Student ID: ");

        System.out.println();
        System.out.printf("%-4s %-10s %-35s%n", "ID", "Code", "Course");
        courses.forEach(System.out::println);
        System.out.println();

        List<Integer> courseIds = promptIntList("Course ID(s), comma separated: ");
        int count = service.registerStudent(studentId, courseIds);
        System.out.println("Registered student " + studentId + " for " + count + " course(s).");
    }

    private static void viewRegistrations() throws SQLException {
        System.out.println("--- View Registrations ---");
        String input = prompt("Student ID (press Enter for all students): ");

        List<Registration> registrations;
        if (input.isEmpty()) {
            registrations = service.getAllRegistrations();
        } else {
            Integer id = parseIntOrNull(input);
            if (id == null) {
                System.out.println("Please enter a whole number.");
                return;
            }
            registrations = service.getRegistrationsForStudent(id);
        }

        if (registrations.isEmpty()) {
            System.out.println("No registrations found.");
            return;
        }
        System.out.printf("%-4s %-25s %-10s %-30s %s%n", "ID", "Student", "Code", "Course", "Registered");
        System.out.println("-".repeat(90));
        registrations.forEach(System.out::println);
        System.out.println("\n" + registrations.size() + " registration(s).");
    }

    // ---------------------------------------------------------------- helpers

    /** Runs an action and turns any error into a friendly message instead of a stack trace. */
    private static void execute(Action action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: that record conflicts with existing data "
                    + "(for example, a duplicate email or course code).");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static boolean canConnect() {
        try (Connection ignored = DatabaseConnection.getConnection()) {
            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to the database: " + e.getMessage());
            System.out.println("Check that MySQL is running, that sql/schema.sql has been run,");
            System.out.println("and that src/main/resources/db.properties has the right credentials.");
            return false;
        }
    }

    private static void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-4s %-25s %-32s %-15s%n", "ID", "Name", "Email", "Phone");
        System.out.println("-".repeat(78));
        students.forEach(System.out::println);
        System.out.println("\n" + students.size() + " student(s).");
    }

    private static String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static String promptOrKeep(String field, String current) {
        String value = prompt(field + " [" + (current == null ? "" : current) + "]: ");
        return value.isEmpty() ? current : value;
    }

    private static int promptInt(String message) {
        while (true) {
            Integer value = parseIntOrNull(prompt(message));
            if (value != null) {
                return value;
            }
            System.out.println("Please enter a whole number.");
        }
    }

    private static List<Integer> promptIntList(String message) {
        while (true) {
            List<Integer> values = new ArrayList<>();
            boolean valid = true;
            for (String part : prompt(message).split(",")) {
                if (part.isBlank()) {
                    continue;
                }
                Integer value = parseIntOrNull(part.trim());
                if (value == null) {
                    valid = false;
                    break;
                }
                values.add(value);
            }
            if (valid && !values.isEmpty()) {
                return values;
            }
            System.out.println("Please enter one or more whole numbers, e.g. 1,3");
        }
    }

    private static Integer parseIntOrNull(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
