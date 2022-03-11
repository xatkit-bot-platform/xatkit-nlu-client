package com.xatkit.core.recognition.nluserver.mapper.dsl;

import lombok.Data;

@Data

/**
 * Represents the type of a {@link EntityParameter}.
 */
public abstract class EntityType {

    /**
     * The name of the type.
     */
    protected String name;

    public EntityType(String name) {
        this.name = name;
    }
}
