package com.github.limbocingo.authentication.api;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Connect to your MySQL server instance faster and easier.
 */
public class Connector {
    private final Connection connection;

    /**
     * When instanced creates a
     * connection between the MySQL server and the program.
     *
     * @param hostname The current host of the MySQL server.
     * @param database Database where the queries will be made. If it doesn't exist one will be created.
     * @param username User on the MySQL server.
     * @param password Password of the user.
     * @throws SQLException If the connection is closed or the database doesn't exist.
     */
    public Connector(String hostname, String database, String username, String password) throws SQLException {
        String URL = "jdbc:mysql://" + hostname + "/" + database + "?characterEncoding=utf8&createDatabaseIfNotExist=true";
        this.connection = DriverManager.getConnection(URL, username, username);

        this.connection.setAutoCommit(true);
    }

    /**
     * Create a table in the used database in case it doesn't exist.
     *
     * @param name Name of the database.
     * @throws SQLException If the connection is closed.
     */
    public void createTable(String name, Field... fields) throws SQLException {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + name + " (");
        int currentField = 0;

        for (Field field : fields) {
            currentField++;

            query.append(field.getName());
            query.append(" ");
            query.append(field.getType());

            if (currentField != fields.length) {
                query.append(", ");
            }
        }

        query.append(")");

        Statement statement = connection.createStatement();
        statement.executeUpdate(query.toString());
        statement.close();
    }

    /**
     * Go through each column and gets the value for finally
     * transform it to a HashMap.
     *
     * @param resultSet The ResultSet from which you want to get the values.
     * @return HashMap<String, Object> Name of the column and the value.
     * @throws SQLException If the connection is already closed.
     */
    private List<HashMap<String, Object>> transformResultSet(ResultSet resultSet) throws SQLException {
        List<HashMap<String, Object>> columns = new ArrayList<>();
        int columnsCount = resultSet.getMetaData().getColumnCount() + 1;

        while (resultSet.next()) {
            HashMap<String, Object> row = new HashMap<>();
            for (int currColumn = 1; columnsCount > currColumn; currColumn++) {
                String name = resultSet.getMetaData().getColumnName(currColumn);
                Object value = resultSet.getObject(currColumn);

                row.put(name, value);
            }
            columns.add(row);
        }

        return columns;
    }

    /**
     * Execute a query.
     * <b>Recommended when you're trying to select values.</b>
     *
     * @param query  SQL query.
     * @param values Values of the query has.
     * @return HashMap<String [ column ], Object [ value ]> Column and value.
     */
    public List<HashMap<String, Object>> executeQuery(String query, Object... values) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int currValueIndex = 0; values.length > currValueIndex; currValueIndex++) {
            statement.setObject(currValueIndex + 1, values[currValueIndex]);
        }

        ResultSet resultSet = statement.executeQuery();

        List<HashMap<String, Object>> result = this.transformResultSet(resultSet);

        resultSet.close();
        statement.close();

        return result;
    }

    /**
     * Execute a update query.
     * <b>Recommended when you're trying to update, delete or insert values.</b>
     *
     * @param query  SQL query.
     * @param values Values of the query.
     */
    public void updateQuery(String query, Object... values) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int currValueIndex = 0; values.length > currValueIndex; currValueIndex++) {
            statement.setObject(currValueIndex + 1, values[currValueIndex]);
        }

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Close the connection with the MySQL server.
     *
     * @throws SQLException Is already closed
     */
    public void close() throws SQLException {
        connection.close();
    }
}
