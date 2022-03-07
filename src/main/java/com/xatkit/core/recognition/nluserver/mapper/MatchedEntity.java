package com.xatkit.core.recognition.nluserver.mapper;


public class MatchedEntity {
    EntityParameter entity;
    String value;

    public MatchedEntity(EntityParameter entity, String value) {
        this.entity = entity;
        this.value = value;
    }
}
