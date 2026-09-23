import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Student Management System.
 * Presents a menu-driven interface and handles all user interaction.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager();

    public static void main(String[] args) {
        // Load any previously saved records on startup
        try {
            manager.loadFromFile();
            System.out.println("Loaded " + manager.totalStudents() + " student record(s) from file.");
        } catch (IOException e) {
            System.out.println("Could not load existing records: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewStudents();
                case 3 -> searchStudents();
                case 4 -> updateStudent();
                case 5 -> deleteStudent();
                case 6 -> saveStudents();
                case 7 -> {
                    saveStudents();
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please select an option between 1 and 7.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== Student Management System =====");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Save Records to File");
        System.out.println("7. Exit");
        System.out.println("=======================================");
    }

    private static void addStudent() {
        System.out.println("\n-- Add Student --");
        int id = readInt("Enter ID: ");
        String name = readNonEmptyString("Enter Name: ");
        int age = readInt("Enter Age: ");
        String course = readNonEmptyString("Enter Course: ");
        double gpa = readDouble("Enter GPA (0.0 - 4.0): ");

        try {
            manager.addStudent(id, name, age, course, gpa);
            System.out.println("Student added successfully.");
        } catch (DuplicateStudentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewStudents() {
        System.out.println("\n-- All Students --");
        List<Student> all = manager.getAllStudents();
        if (all.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        for (Student s : all) {
            System.out.println(s);
        }
        System.out.println("Total students: " + all.size());
    }

    private static void searchStudents() {
        System.out.println("\n-- Search Student --");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        int option = readInt("Choose search type: ");

        try {
            if (option == 1) {
                int id = readInt("Enter ID to search: ");
                Student s = manager.searchById(id);
                System.out.println("Found: " + s);
            } else if (option == 2) {
                String name = readNonEmptyString("Enter name (or part of it) to search: ");
                List<Student> results = manager.searchByName(name);
                System.out.println("Found " + results.size() + " match(es):");
                for (Student s : results) {
                    System.out.println(s);
                }
            } else {
                System.out.println("Invalid search option.");
            }
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateStudent() {
        System.out.println("\n-- Update Student --");
        int id = readInt("Enter ID of student to update: ");
        try {
            Student existing = manager.searchById(id);
            System.out.println("Current record: " + existing);

            String name = readNonEmptyString("Enter new Name: ");
            int age = readInt("Enter new Age: ");
            String course = readNonEmptyString("Enter new Course: ");
            double gpa = readDouble("Enter new GPA: ");

            manager.updateStudent(id, name, age, course, gpa);
            System.out.println("Student updated successfully.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        System.out.println("\n-- Delete Student --");
        int id = readInt("Enter ID of student to delete: ");
        try {
            manager.deleteStudent(id);
            System.out.println("Student deleted successfully.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void saveStudents() {
        try {
            manager.saveToFile();
            System.out.println("Records saved to file successfully.");
        } catch (IOException e) {
            System.out.println("Error saving records: " + e.getMessage());
        }
    }

    // ---------- Input validation helpers ----------

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }
}
