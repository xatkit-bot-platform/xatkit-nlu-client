package com.xatkit.core.recognition.nluserver.mapper.dsl;


import lombok.Data;

@Data
/**
 * Base Entity that can be used in the {@link EntityParameter}s It is a specialized
 * {@link EntityType} representing a default system entity type (e.g. number, date, location, etc.)
 *
 * @see CustomEntityTypeEntry
 */
public class BaseEntityType extends EntityType {

    public BaseEntityType(String name) {
        super(name);
    }
}
