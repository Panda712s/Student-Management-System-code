package com.studentmanagement.service;

import com.studentmanagement.dao.CourseDAO;
import com.studentmanagement.dao.RegistrationDAO;
import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.Course;
import com.studentmanagement.model.Registration;
import com.studentmanagement.model.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Business logic between the CLI and the DAOs: input validation and
 * multi-step operations that must run as a single database transaction.
 *
 * Validation problems are reported with IllegalArgumentException;
 * database problems propagate as SQLException.
 */
public class StudentService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9 ()-]{7,20}$");

    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    // ---------------------------------------------------------------- students

    public Student addStudent(String name, String email, String phone) throws SQLException {
        Student student = new Student(requireText(name, "Name"), validEmail(email), validPhone(phone));
        studentDAO.add(student);
        return student;
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public Optional<Student> findStudent(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        return studentDAO.search(requireText(keyword, "Search term"));
    }

    public boolean updateStudent(Student student) throws SQLException {
        student.setName(requireText(student.getName(), "Name"));
        student.setEmail(validEmail(student.getEmail()));
        student.setPhone(validPhone(student.getPhone()));
        return studentDAO.update(student);
    }

    public boolean deleteStudent(int id) throws SQLException {
        return studentDAO.delete(id);
    }

    // ----------------------------------------------------------------- courses

    public Course addCourse(String courseName, String courseCode) throws SQLException {
        Course course = new Course(
                requireText(courseName, "Course name"),
                requireText(courseCode, "Course code").toUpperCase());
        courseDAO.add(course);
        return course;
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.findAll();
    }

    // ----------------------------------------------------------- registrations

    /**
     * Registers a student for one or more courses as a single transaction:
     * either every registration is saved, or (on any error) none are.
     *
     * @return the number of registrations created
     */
    public int registerStudent(int studentId, List<Integer> courseIds) throws SQLException {
        Set<Integer> uniqueCourseIds = new LinkedHashSet<>(courseIds);
        if (uniqueCourseIds.isEmpty()) {
            throw new IllegalArgumentException("Select at least one course.");
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!studentDAO.exists(connection, studentId)) {
                    throw new IllegalArgumentException("No student with ID " + studentId + ".");
                }
                for (int courseId : uniqueCourseIds) {
                    if (!courseDAO.exists(connection, courseId)) {
                        throw new IllegalArgumentException("No course with ID " + courseId + ".");
                    }
                    if (registrationDAO.isRegistered(connection, studentId, courseId)) {
                        throw new IllegalArgumentException(
                                "Student " + studentId + " is already registered for course " + courseId + ".");
                    }
                    registrationDAO.register(connection, studentId, courseId);
                }

                connection.commit();
                return uniqueCourseIds.size();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public List<Registration> getAllRegistrations() throws SQLException {
        return registrationDAO.findAll();
    }

    public List<Registration> getRegistrationsForStudent(int studentId) throws SQLException {
        return registrationDAO.findByStudent(studentId);
    }

    // -------------------------------------------------------------- validation

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    private static String validEmail(String email) {
        String value = requireText(email, "Email").toLowerCase();
        if (!EMAIL.matcher(value).matches()) {
            throw new IllegalArgumentException("'" + value + "' is not a valid email address.");
        }
        return value;
    }

    /** Phone is optional: blank becomes NULL in the database. */
    private static String validPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String value = phone.trim();
        if (!PHONE.matcher(value).matches()) {
            throw new IllegalArgumentException("'" + value + "' is not a valid phone number.");
        }
        return value;
    }
}
