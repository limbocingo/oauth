package com.github.limbocingo.authentication.api;

/**
 * Dataclass used for the creation of tables, the fields part.
 */
public class Field {
    private final String name;
    private final String type;

    /**
     * Instance for the dataclass.
     *
     * @param name Name of field.
     * @param type Type of the field.
     */
    public Field(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Getter for name of the field.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for type of the field.
     *
     * @return String
     */
    public String getType() {
        return this.type;
    }
}
