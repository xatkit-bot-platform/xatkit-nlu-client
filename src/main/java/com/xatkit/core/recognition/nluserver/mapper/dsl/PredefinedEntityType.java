package com.xatkit.core.recognition.nluserver.mapper.dsl;


/**
 * Custom Entity that can be used in the {@link EntityParameter}s Ii is a specialized
 * {@link EntityType} representing an enumeration of values with possible synonyms
 *
 * @see CustomEntityTypeEntry
 */
public class PredefinedEntityType extends EntityType {


    public PredefinedEntityType(String name) {
        super(name);
    }

    /**
     * @return the varName
     */
    public String getVarName() {
        return this.name;
    }


}
