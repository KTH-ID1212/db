package se.kth.id1212.db.jdbcintro;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A small program that illustrates how to write a simple JDBC program.
 */
public class BasicJDBC {
    private static final String TABLE_NAME = "person";
    private PreparedStatement createPersonStmt;
    private PreparedStatement findAllPersonsStmt;
    private PreparedStatement deletePersonStmt;

    private void accessDB() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientXADataSource");
            Connection connection = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/MySampleDatabase", "jdbc",
                    "jdbc");
            createTable(connection);
            Statement stmt = connection.createStatement();
            prepareStatements(connection);
            createPersonStmt.setString(1, "stina");
            createPersonStmt.setString(2, "0123456789");
            createPersonStmt.setInt(3, 43);
            createPersonStmt.executeUpdate();
            createPersonStmt.setString(1, "olle");
            createPersonStmt.setString(2, "9876543210");
            createPersonStmt.setInt(3, 12);
            createPersonStmt.executeUpdate();
            listAllRows(connection);
            deletePersonStmt.setString(1, "stina");
            deletePersonStmt.executeUpdate();
            listAllRows(connection);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void createTable(Connection connection) throws SQLException {
        if (!tableExists(connection)) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(
                    "create table " + TABLE_NAME + " (name varchar(32) primary key, phone varchar(12), age int)");
        }
    }

    private boolean tableExists(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tableMetaData = metaData.getTables(null, null, null, null);
        while (tableMetaData.next()) {
            String tableName = tableMetaData.getString(3);
            if (tableName.equalsIgnoreCase(TABLE_NAME)) {
                return true;
            }
        }
        return false;
    }

    private void listAllRows(Connection connection) throws SQLException {
        ResultSet persons = findAllPersonsStmt.executeQuery();
        while (persons.next()) {
            System.out.println(
                    "name: " + persons.getString(1) + ", phone: " + persons.getString(2) + ", age: " + persons.
                    getInt(3));
        }
    }

    private void prepareStatements(Connection connection) throws SQLException {
        createPersonStmt = connection.prepareStatement("INSERT INTO "
                                                       + TABLE_NAME + " VALUES (?, ?, ?)");
        findAllPersonsStmt = connection.prepareStatement("SELECT * from "
                                                         + TABLE_NAME);
        deletePersonStmt = connection.prepareStatement("DELETE FROM "
                                                       + TABLE_NAME
                                                       + " WHERE name = ?");
    }

    public static void main(String[] args) {
        new BasicJDBC().accessDB();
    }
}
