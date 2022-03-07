package com.xatkit.core.recognition.nluserver.mapper;


/**
 * Parameter to recognize entities in a {@link Intent}
 */
public class EntityParameter {

    private String name;


    /*
     * Fragment in the training sentences that should be matched to the parameter
     *
     * For now we assume a single fragment per Parameter
     */
    private String fragment;

    /**
     * {@link EntityType} defining the range of values of entities matching the parameter
     *
     * Type could be either a custom {@link CustomEntityType} or a {@link PredefinedEntity}
     */
    private EntityType type;

    public EntityParameter(String name, String fragment, EntityType type) {
        this.name = name;
        this.fragment = fragment;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getFragment() {
        return this.fragment;
    }

    public EntityType getType() {
        return this.type;
    }
}
