package com.github.limbocingo.authentication.api;

/*
    Imports
*/

import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;


/**
 * User object dataclass.
 */
public class User {
    private final Integer id;
    private final String username;
    private final String password;
    private final String discord;

    /**
     * Constructor of the user dataclass.
     *
     * @param user HashMap of the user properties.
     * @throws SQLException
     */
    public User(HashMap<String, Object> user) throws SQLException {
        this.id = (Integer) user.get("id");
        this.username = (String) user.get("username");
        this.password = (String) user.get("password");
        this.discord = (String) user.get("discord");
    }

    /**
     * Return the ID of the current user.
     *
     * @return Integer
     */
    public Integer getId() {
        return id;
    }

    /**
     * Return the discord of the current user.
     *
     * @return String
     */
    public String getDiscord() {
        return discord;
    }

    /**
     * Return the password of the current user.
     *
     * @return String
     */
    public String getPassword() {
        return new String(Base64.getDecoder().decode(password));
    }

    /**
     * Return the username of the current user.
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }
}
