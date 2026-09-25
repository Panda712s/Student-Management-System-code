package com.studentmanagement.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates JDBC connections to the MySQL database.
 *
 * Connection details are read, in order of priority, from:
 *  1. environment variables DB_URL, DB_USER, DB_PASSWORD
 *  2. src/main/resources/db.properties (not committed to Git)
 *  3. the defaults below
 */
public final class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not read db.properties - " + e.getMessage());
        }

        URL = setting("DB_URL", props.getProperty("db.url"), DEFAULT_URL);
        USER = setting("DB_USER", props.getProperty("db.user"), DEFAULT_USER);
        PASSWORD = setting("DB_PASSWORD", props.getProperty("db.password"), DEFAULT_PASSWORD);
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String setting(String envName, String fileValue, String defaultValue) {
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return fileValue != null ? fileValue.trim() : defaultValue;
    }
}
