package com.xatkit.core.recognition.nluserver.mapper;


/**
 * Represents the type of a {@link EntityParameter}.
 */
public abstract class EntityType {

    /**
     * The name of the type.
     */
    protected String name;

    /**
     * Creates a {@link EntityType} with the provided {@code name}.
     *
     * @param name the name of the type
     */
    public EntityType(String name) {
        this.name = name;
    }

    /**
     * @return the name of the type
     */
    public String getName() {
        return this.name;
    }
}
