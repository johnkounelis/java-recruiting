package com.recruiting.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * Utility class to test database connection and verify setup
 */
public class DatabaseTestConnection {
    
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        System.out.println("================================");
        
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection successful!");
                
                // Check if database exists
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("Database Product: " + metaData.getDatabaseProductName());
                System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
                System.out.println("Driver Name: " + metaData.getDriverName());
                System.out.println("Driver Version: " + metaData.getDriverVersion());
                
                // Check if tables exist
                System.out.println("\nChecking tables...");
                ResultSet tables = metaData.getTables(null, null, "users", null);
                if (tables.next()) {
                    System.out.println("✓ 'users' table exists");
                } else {
                    System.out.println("✗ 'users' table NOT found. Please run sql/database_schema.sql");
                }
                
                tables = metaData.getTables(null, null, "jobs", null);
                if (tables.next()) {
                    System.out.println("✓ 'jobs' table exists");
                } else {
                    System.out.println("✗ 'jobs' table NOT found. Please run sql/database_schema.sql");
                }
                
                tables = metaData.getTables(null, null, "applications", null);
                if (tables.next()) {
                    System.out.println("✓ 'applications' table exists");
                } else {
                    System.out.println("✗ 'applications' table NOT found. Please run sql/database_schema.sql");
                }
                
                DatabaseConnection.closeConnection(conn);
                System.out.println("\n✓ Database setup looks good!");
            } else {
                System.out.println("✗ Connection is null or closed");
            }
        } catch (Exception e) {
            System.err.println("\n✗ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Check if MySQL server is running");
            System.err.println("2. Verify credentials in src/main/resources/db.properties");
            System.err.println("3. Ensure database 'recruiting_db' exists");
            System.err.println("4. Run sql/database_schema.sql to create tables");
            e.printStackTrace();
        }
    }
}
