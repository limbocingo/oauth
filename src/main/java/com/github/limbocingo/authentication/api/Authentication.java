package com.github.limbocingo.authentication.api;

/*
    Imports
*/

import java.sql.*;


/**
 * Main of the API.
 */
public class Authentication {
    Connector connector;

    /**
     * Connect to the database.
     *
     * @param host     The current host of the MySQL server.
     * @param username User on the MySQL server.
     * @param database User on the MySQL server.
     * @param password Password of the user.
     * @throws SQLException If the connection is closed.
     */
    public Authentication(String host, String username, String password, String database) throws SQLException {
        this.connector = new Connector(host, database, username, password);

        this.connector.createTable(
                "users",
                new Field("id", "INT NOT NULL AUTO_INCREMENT PRIMARY KEY"),
                new Field("username", "CHAR(255) NOT NULL UNIQUE"),
                new Field("password", "CHAR(255)"),
                new Field("discord", "CHAR(255)")
        );
    }

    /**
     * Manage a user or register it.
     *
     * @param username The username of the user.
     * @return UserManagement It will call the UserManagement class with username you want.
     * @throws SQLException If connection is closed.
     */
    public UserManager getUser(String username) throws SQLException {
        return new UserManager(username, this.connector);
    }

    /**
     * Close the connection between client and the server.
     *
     * @throws SQLException If already closed.
     */
    public void close() throws SQLException {
        this.connector.close();
    }
}