package com.xatkit.core.recognition.nluserver.mapper.dsl;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomEntityTypeEntry {

    private String value;
    private List<String> synonyms;

    public CustomEntityTypeEntry(String value, List<String> synonyms) {
        this.value = value;
        this.synonyms = synonyms;
    }

    public CustomEntityTypeEntry(String value) {
        this.value = value;
        this.synonyms = new ArrayList<String>();
    }

    /**
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the synonyms
     */
    public List<String> getSynonyms() {
        return this.synonyms;
    }

    /*
    * add all synonyms to an entry
    */
    public void addAllSynonyms(List<String> synonyms) {
        this.synonyms.addAll(synonyms);
    }
    /*
    * add a synonym to an entry
    */
    public void addSynonym(String synonym) {
        this.synonyms.add(synonym);
    }

}
