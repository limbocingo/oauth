package com.github.limbocingo.authentication.api;

import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;


/**
 * Manage any user you want.
 */
public class UserManager {

    Connector connector;
    String username;

    /**
     * Instance of the manager.
     *
     * @param username  The username of the user you want to manage.
     * @param connector Connector for the database.
     */
    public UserManager(String username, Connector connector) {
        this.connector = connector;
        this.username = username;
    }

    /**
     * Get the information of the user.
     *
     * @return User Return the user that you want to select if it doesn't exist will be null.
     * @throws SQLException If the connection is closed.
     */
    @Nullable
    public User information() throws SQLException {
        List<HashMap<String, Object>> result = this.connector.executeQuery("SELECT * FROM users WHERE username = ?", username);

        for (HashMap<String, Object> user : result)
            if (user.get("username").equals(this.username))
                return new User(user);

        return null;
    }

    /**
     * Register a user in the database.
     *
     * @param password The password of the user.
     * @throws SQLException If the connection is closed.
     */
    public void register(String password) throws SQLException {
        password = Base64.getEncoder().encodeToString(password.getBytes());

        if (this.information() == null)
            this.connector.updateQuery("INSERT INTO users (username, password) VALUES (?, ?);", username, password);
    }

    /**
     * Change user discord.
     *
     * @param discord The current discord of the user.
     * @throws SQLException If the connection is closed.
     */
    public void discord(String discord) throws SQLException {
        if (this.information() != null)
            this.connector.updateQuery("UPDATE users SET discord = ? WHERE username = ?", discord, username);
    }

    /**
     * Change user password.
     *
     * @param newPassword The new password of the user.
     * @throws SQLException If the connection is closed.
     */
    public void password(String newPassword) throws SQLException {
        newPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());

        if (this.information() != null)
            this.connector.updateQuery("UPDATE users SET password = ? WHERE username = ?", newPassword, username);
    }
}
