package com.sellout.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/SellOutEasyTrack_SQL?" +
            "useSSL=false&" +
            "allowPublicKeyRetrieval=true&" +
            "serverTimezone=America/Sao_Paulo&" +
            "useUnicode=true&" +
            "characterEncoding=UTF-8";

    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "SenhaForte123#";

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConnection() {
        this(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASSWORD);
    }

    public DatabaseConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(true);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getConnectionInfo() {
        try (Connection conn = getConnection()) {
            return String.format(
                    "Connected to: %s\nUser: %s\nDriver: %s\nMySQL Version: %s",
                    conn.getMetaData().getURL(),
                    conn.getMetaData().getUserName(),
                    conn.getMetaData().getDriverName(),
                    conn.getMetaData().getDatabaseProductVersion()
            );
        } catch (SQLException e) {
            return "Error getting connection info: " + e.getMessage();
        }
    }
}