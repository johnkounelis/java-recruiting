package com.recruiting.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static HikariDataSource dataSource;

    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties");

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new RuntimeException("db.properties file not found!");
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName(properties.getProperty("db.driver"));

            // Pool settings
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(300000);        // 5 minutes
            config.setConnectionTimeout(20000);   // 20 seconds
            config.setMaxLifetime(1200000);       // 20 minutes
            config.setLeakDetectionThreshold(60000); // 1 minute

            // Performance settings
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            System.err.println("Error initializing connection pool: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error initializing connection pool", e);
        }
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            String userFriendlyMsg;

            if (errorMsg != null && errorMsg.contains("Access denied")) {
                userFriendlyMsg = "Database authentication failed. Please check db.properties credentials.";
            } else if (errorMsg != null && errorMsg.contains("Unknown database")) {
                userFriendlyMsg = "Database 'recruiting_db' not found. Please run sql/database_schema.sql.";
            } else if (errorMsg != null && (errorMsg.contains("Communications link failure") || errorMsg.contains("Connection refused"))) {
                userFriendlyMsg = "Cannot connect to MySQL server. Please ensure MySQL is running on localhost:3306";
            } else {
                userFriendlyMsg = "Database connection error: " + (errorMsg != null ? errorMsg : "Unknown error");
            }

            System.err.println("Database Connection Error: " + userFriendlyMsg);
            throw new RuntimeException(userFriendlyMsg, e);
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close(); // Returns connection to pool
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
