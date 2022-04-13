package com.xatkit.core.recognition.nluserver.mapper.dsl;


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
     * Type could be either a custom {@link CustomEntityType} or a built-in one
     */
    private EntityType type;

    //Temporal storage of the referenced type by name before we can replace it by the actual object reference
    private String typeName;

    public EntityParameter(String name, String fragment, EntityType type) {
        this.name = name;
        this.fragment = fragment;
        this.type = type;
    }

    public EntityParameter(String name, String fragment, String typeName) {
        this.name = name;
        this.fragment = fragment;
        this.typeName = typeName;
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

    public String getTypeName() {
        return this.typeName;
    }

    public void setType(EntityType type) {
        this.type = type;
    }


}
