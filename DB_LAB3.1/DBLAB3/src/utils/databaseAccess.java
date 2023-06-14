package utils;

import gui.deleteDatabaseWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class databaseAccess {
    public Connection connection = null;
    public String url;
    public String queryDir = "queries/%s";
    public String currentDatabaseName;
    public String currentTableName = "my_table";
    public String[] columnNames;
    private String currentUserName;
    private String currentUserPassword;
    public ArrayList<String> columnsDTypes;
    public ArrayList<String> categoricalColumns;

    public databaseAccess(String userName, String userPassword,
                          String databaseName, String mode, JDialog window, user current_user) {
        databaseName = databaseName.toLowerCase();
        currentDatabaseName = databaseName;
        currentUserName = userName;
        currentUserPassword = userPassword;
        String permission = current_user.getPermission();
        if (mode.equals("open")) {
            try {
                url = String.format("jdbc:postgresql://127.0.0.1:5432/%s", databaseName);
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, userName, userPassword);

                getColumnsDTypes();
                setStoredProcedures();
            } catch (Exception e) {
                if (!e.toString().equals("org.postgresql.util.PSQLException: ERROR: permission denied for schema public")){
                    JOptionPane.showMessageDialog(window,
                            e, "Open error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            try {
                url = "jdbc:postgresql://127.0.0.1:5432/";
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, userName, userPassword);

                Statement statement = connection.createStatement();
                String sql = String.format("CREATE DATABASE %s", databaseName);
                statement.executeUpdate(sql);

                connection.close();
                statement.close();

                url = String.format("jdbc:postgresql://127.0.0.1:5432/%s", databaseName);
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, userName, userPassword);
                String createTableQuery = Files.readString(Path.of(String.format(queryDir, "create_table.txt")));
                Statement table_statement = connection.createStatement();
                table_statement.executeUpdate(createTableQuery);

                table_statement.close();
                setStoredProcedures();
                connection.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(window,
                        String.format("Cannot create database due to error: %s", ex), "Error",
                        JOptionPane.ERROR_MESSAGE);;
            }
        }
    }

    public void createUserWithPermission(String userName, String userPassword,
                                         String databaseName, String permission) throws SQLException {
        CallableStatement createUser = connection.prepareCall("{ call createUserWithPermission(?, ?, ?, ?, ?) }");
        String tableName = "my_table";
        createUser.setString(1, userName);
        createUser.setString(2, userPassword);
        createUser.setString(3, permission);
        createUser.setString(4, databaseName);
        createUser.setString(5, tableName);

        createUser.executeQuery();
        createUser.close();
    }

    public void deleteAllConnections() throws SQLException {

        Statement stmt = connection.createStatement();

        // Execute a query to find the active connections
        ResultSet rs = null;
        try {

            // Execute a query to find the active connections
            rs = stmt.executeQuery(String.format("SELECT * FROM pg_stat_activity WHERE datname = '%s'", currentDatabaseName));

            // Iterate through the result set and print information about each connection
            while (rs.next()) {
                int pid = rs.getInt("pid");
                String user = rs.getString("usename");
                String database = rs.getString("datname");
                String state = rs.getString("state");

//                System.out.println("Connection pid: " + pid + ", user: " + user + ", database: " + database + ", state: " + state);
                stmt.execute("SELECT pg_terminate_backend(" + pid + ")");
            }

        } finally {
            // Close the result set, statement, and connection
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }
    }

    public void deleteUser(String userName) throws SQLException {
        CallableStatement statement = connection.prepareCall("{ call deleteUserByUsername(?) }");
        statement.setString(1, userName);
        statement.executeQuery();

        statement.close();
    }

    public void getColumnsDTypes() throws SQLException {
        CallableStatement getValuesStatement = connection.prepareCall("{ call getColumnTypes() }");
        ResultSet queryData = getValuesStatement.executeQuery();

        String[] _columnsDTypes = null;
        while(queryData.next()) {
            Array idArray = queryData.getArray(1);
            _columnsDTypes = (String[]) idArray.getArray();

        }

        columnsDTypes = new ArrayList<>(List.of(_columnsDTypes));
        queryData.close();
        getValuesStatement.close();
    }

    public ArrayList<String> getUsers() throws SQLException {
        ArrayList<String> usersList = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet users = statement.executeQuery("SELECT usename FROM pg_user");

        while (users.next()) {
            String user = users.getString("usename");
            if (!user.equals("postgres")){
                usersList.add(user);
            }
        }

        users.close();
        statement.close();
        return usersList;
    }

    public void setStoredProcedures() throws SQLException, IOException {
        String storedProcedureQuery = Files.readString(Path.of(String.format(queryDir, "stored_procedures.txt")));
        Statement storedProcedureStatement = connection.createStatement();
        storedProcedureStatement.executeUpdate(storedProcedureQuery);

        storedProcedureStatement.close();
    }

    public ResultSet getDataById(int id) throws SQLException, IOException {
        String getDataQuery = Files.readString(Path.of(String.format(queryDir, "get_values_by_id.txt")));
        String getData = String.format(getDataQuery, id);
        Statement getDataStatement = connection.createStatement();
        ResultSet resultSet = getDataStatement.executeQuery(getData);

        return resultSet;
    }

    public ResultSet getData() throws IOException, SQLException {
        if (connection == null){
            return null;
        }
        String getDataQuery = Files.readString(Path.of(String.format(queryDir, "get_data.txt")));
        Statement getDataStatement = connection.createStatement();
        ResultSet resultSet = getDataStatement.executeQuery(getDataQuery);

        return resultSet;
    }

    public void createRoles() throws SQLException {
        CallableStatement addValuesStatement = connection.prepareCall("{ call createRoles(?) }");
        addValuesStatement.setString(1, currentDatabaseName);
        addValuesStatement.executeQuery();
    }

    public void deleteConnection() throws SQLException {
        connection.close();
    }

    public Array modifyData(ArrayList<String> data) throws SQLException {
        List<Object> modifiedData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            String value = data.get(i);
            String dtype = columnsDTypes.get(i);
            String modifiedValue = value.replaceAll("\"\"", "''");
            if (dtype.equals("integer")){
                try {
                    // Try to parse value as a double
                    double doubleValue = Double.parseDouble(modifiedValue);
                    modifiedData.add(doubleValue);
                } catch (NumberFormatException e) {
                    System.out.println(e);
                }
            } else if (dtype.equals("numeric")){
                try {
                    // Try to parse value as a double
                    double doubleValue = Double.parseDouble(modifiedValue);
                    modifiedData.add(doubleValue);
                } catch (NumberFormatException e) {
                    System.out.println(e);
                }
            } else {
                String quotedValue = String.format("'%s'", modifiedValue);
                modifiedData.add(quotedValue);
            }
        }

        Array valuesArray = connection.createArrayOf("text", modifiedData.toArray());

        return valuesArray;
    }

    public void addData(ArrayList<String> data) throws SQLException {
        System.out.println(connection.toString());
        CallableStatement addValuesStatement = connection.prepareCall("{ call insertValues(?) }");
        Array valuesArray = modifyData(data);

        addValuesStatement.setArray(1, valuesArray);
        addValuesStatement.executeQuery();

        addValuesStatement.close();
    }

    public void editData(ArrayList<String> data, int id) throws SQLException {
        System.out.println(connection);
        CallableStatement editValuesStatement = connection.prepareCall("{ call editValues(?, ?) }");
        Array valuesArray = modifyData(data);
        System.out.println(valuesArray.toString());

        editValuesStatement.setArray(1, valuesArray);
        editValuesStatement.setInt(2, id);

        editValuesStatement.executeQuery();
        editValuesStatement.close();
    }

    public void dropRow(String fieldValue, String fieldName) throws SQLException {
        CallableStatement deleteById = connection.prepareCall("{ call deleteByColumn(?, ?) }");
        deleteById.setString(1, fieldName);
        deleteById.setString(2, fieldValue);

        deleteById.executeQuery();
        deleteById.close();
    }

    public void deleteDatabase() throws IOException, SQLException {
        if (connection == null) {
            return;
        }
        Statement stmt1 = connection.createStatement();

        // Execute a query to find the active connections
        ResultSet rs = null;
        try {

            // Execute a query to find the active connections
            rs = stmt1.executeQuery(String.format("SELECT * FROM pg_stat_activity WHERE datname = '%s'", currentDatabaseName));

            // Iterate through the result set and print information about each connection
            while (rs.next()) {
                int pid = rs.getInt("pid");
                String user = rs.getString("usename");
                String database = rs.getString("datname");
                String state = rs.getString("state");

                System.out.println("Connection pid: " + pid + ", user: " + user + ", database: " + database + ", state: " + state);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        connection.close();
        String Query = Files.readString(Path.of(String.format(queryDir, "delete_database.txt")));
        String deleteDatabaseQuery = String.format(Query, currentDatabaseName);
        url = "jdbc:postgresql://127.0.0.1:5432/";

        try(Connection conn = DriverManager.getConnection(url, currentUserName, currentUserPassword);
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate(deleteDatabaseQuery);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public List<Integer> getId(String fieldName, String fieldValue) throws SQLException {
        List<Integer> idsList = null;
        try {
            CallableStatement getId = connection.prepareCall("{ call getId(?,?) }");
            getId.setString(1, fieldName);
            getId.setString(2, fieldValue);

            ResultSet idsResultSet = getId.executeQuery();
            Integer[] ids = null;

            while (idsResultSet.next()) {
                Array idArray = idsResultSet.getArray(1);
                if (idArray == null) {
                    ids = null;
                } else {
                    ids = (Integer[]) idArray.getArray();
                    idsList = new ArrayList<>(Arrays.asList(ids));
                }
            }
            return idsList;
        } catch (SQLException e){
            System.out.println(e);
        }
        return idsList;
    }

}
