import java.io.*;
import java.util.*;

/**
 * Manages the collection of Student records: add, view, search, update,
 * delete, and persist them to a file.
 *
 * Collections used:
 *  - HashMap<Integer, Student>  : fast lookup of a student by ID
 *  - HashSet<String>            : tracks course names with no duplicates
 *  - ArrayList<Student>         : ordered results for searches/listing
 */
public class StudentManager {
    private final Map<Integer, Student> students = new HashMap<>();
    private final Set<String> courses = new HashSet<>();
    private static final String FILE_NAME = "students.txt";

    // Add a student (Method Overloading: with and without GPA)
    public void addStudent(int id, String name, int age, String course, double gpa)
            throws DuplicateStudentException {
        if (students.containsKey(id)) {
            throw new DuplicateStudentException("A student with ID " + id + " already exists.");
        }
        students.put(id, new Student(id, name, age, course, gpa));
        courses.add(course);
    }

    public void addStudent(int id, String name, int age, String course)
            throws DuplicateStudentException {
        addStudent(id, name, age, course, 0.0);
    }

    // Return all students as a List (ArrayList)
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    // Search by ID
    public Student searchById(int id) throws StudentNotFoundException {
        Student student = students.get(id);
        if (student == null) {
            throw new StudentNotFoundException("No student found with ID " + id);
        }
        return student;
    }

    // Search by name (partial, case-insensitive) - returns ArrayList of matches
    public List<Student> searchByName(String name) throws StudentNotFoundException {
        List<Student> results = new ArrayList<>();
        for (Student s : students.values()) {
            if (s.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(s);
            }
        }
        if (results.isEmpty()) {
            throw new StudentNotFoundException("No student found matching name \"" + name + "\"");
        }
        return results;
    }

    // Update student details
    public void updateStudent(int id, String name, int age, String course, double gpa)
            throws StudentNotFoundException {
        Student student = searchById(id);
        student.setName(name);
        student.setAge(age);
        student.setCourse(course);
        student.setGpa(gpa);
        courses.add(course);
    }

    // Delete a student
    public void deleteStudent(int id) throws StudentNotFoundException {
        if (!students.containsKey(id)) {
            throw new StudentNotFoundException("No student found with ID " + id);
        }
        students.remove(id);
    }

    public Set<String> getAllCourses() {
        return courses;
    }

    public int totalStudents() {
        return students.size();
    }

    // ---------- File I/O ----------

    public void saveToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : students.values()) {
                writer.write(s.toFileString());
                writer.newLine();
            }
        }
    }

    public void loadFromFile() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // Nothing to load yet
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    Student s = Student.fromFileString(line);
                    students.put(s.getId(), s);
                    courses.add(s.getCourse());
                } catch (Exception e) {
                    System.out.println("Skipping malformed line in file: " + line);
                }
            }
        }
    }
}
