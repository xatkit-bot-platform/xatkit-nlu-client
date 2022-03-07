package com.xatkit.core.recognition.nluserver.mapper.dsl;


public class MatchedEntity {
    EntityParameter entity;
    String value;

    public MatchedEntity(EntityParameter entity, String value) {
        this.entity = entity;
        this.value = value;
    }
}
