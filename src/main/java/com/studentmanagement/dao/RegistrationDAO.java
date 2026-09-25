package com.studentmanagement.dao;

import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.Registration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for the registrations table.
 *
 * The write methods take a Connection instead of opening their own, so that
 * StudentService can run several of them inside a single transaction.
 */
public class RegistrationDAO {

    private static final String SELECT_JOINED = """
            SELECT r.id, r.student_id, s.name, r.course_id,
                   c.course_code, c.course_name, r.registration_date
            FROM registrations r
            JOIN students s ON s.id = r.student_id
            JOIN courses  c ON c.id = r.course_id
            """;

    public boolean isRegistered(Connection connection, int studentId, int courseId) throws SQLException {
        String sql = "SELECT 1 FROM registrations WHERE student_id = ? AND course_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void register(Connection connection, int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO registrations (student_id, course_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        }
    }

    public List<Registration> findAll() throws SQLException {
        String sql = SELECT_JOINED + " ORDER BY s.name, c.course_code";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            return mapAll(rs);
        }
    }

    public List<Registration> findByStudent(int studentId) throws SQLException {
        String sql = SELECT_JOINED + " WHERE r.student_id = ? ORDER BY c.course_code";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    private List<Registration> mapAll(ResultSet rs) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        while (rs.next()) {
            Timestamp date = rs.getTimestamp("registration_date");
            registrations.add(new Registration(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    date == null ? null : date.toLocalDateTime()));
        }
        return registrations;
    }
}
