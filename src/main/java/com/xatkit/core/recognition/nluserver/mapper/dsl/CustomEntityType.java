package com.xatkit.core.recognition.nluserver.mapper.dsl;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
/**
 * Custom Entity that can be used in the {@link EntityParameter}s Ii is a specialized
 * {@link EntityType} representing an enumeration of values with possible synonyms
 *
 * @see CustomEntityTypeEntry
 */
public class CustomEntityType extends EntityType {

    private List<CustomEntityTypeEntry> entries;

    public CustomEntityType(String name) {
        super(name);
        this.entries = new ArrayList<>();
    }

    /**
     * @return the varName
     */
    public String getVarName() {
        return this.name;
    }


    /**
     * @return the entries
     */
    public List<CustomEntityTypeEntry> getEntries() {
        return this.entries;
    }

    public void addEntry(CustomEntityTypeEntry entry) {
        this.entries.add(entry);
    }

    /**
     * add all entries
     *
     */
    public void addAllEntries(List<CustomEntityTypeEntry> entries) {
        this.entries.addAll(entries);
    }


}
