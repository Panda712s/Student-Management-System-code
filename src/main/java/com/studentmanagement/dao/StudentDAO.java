package com.studentmanagement.dao;

import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CRUD operations for the students table. Every query uses a PreparedStatement,
 * so user input is always sent as a parameter and never concatenated into SQL.
 */
public class StudentDAO {

    public int add(Student student) throws SQLException {
        String sql = """
                INSERT INTO students (name, email, phone)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getPhone());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setId(keys.getInt(1));
                }
            }
            return student.getId();
        }
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, name, email, phone, created_at FROM students ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(map(rs));
            }
            return students;
        }
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, phone, created_at FROM students WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    /** Case-insensitive partial match on name or email. */
    public List<Student> search(String keyword) throws SQLException {
        String sql = """
                SELECT id, name, email, phone, created_at
                FROM students
                WHERE name LIKE ? OR email LIKE ?
                ORDER BY name
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet rs = statement.executeQuery()) {
                List<Student> students = new ArrayList<>();
                while (rs.next()) {
                    students.add(map(rs));
                }
                return students;
            }
        }
    }

    /** @return true if a row was updated */
    public boolean update(Student student) throws SQLException {
        String sql = """
                UPDATE students
                SET name = ?, email = ?, phone = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getPhone());
            statement.setInt(4, student.getId());
            return statement.executeUpdate() > 0;
        }
    }

    /** Deletes the student; their registrations are removed by ON DELETE CASCADE. */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    /** Transaction-aware lookup used by StudentService. */
    public boolean exists(Connection connection, int id) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("created_at");
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                created == null ? null : created.toLocalDateTime());
    }
}
