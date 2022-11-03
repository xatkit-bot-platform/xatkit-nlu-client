package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.recognition.nluserver.mapper.dsl.BaseEntityType;
import com.xatkit.core.recognition.nluserver.mapper.dsl.CustomEntityType;
import com.xatkit.core.recognition.nluserver.mapper.dsl.CustomEntityTypeEntry;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityType;
import com.xatkit.intent.BaseEntityDefinition;
import com.xatkit.intent.CompositeEntityDefinition;
import com.xatkit.intent.CustomEntityDefinition;
import com.xatkit.intent.EntityDefinition;
import com.xatkit.intent.MappingEntityDefinition;
import com.xatkit.intent.MappingEntityDefinitionEntry;
import lombok.NonNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps {@link EntityDefinition} instances to Xatkit NLUServer {@link EntityType}s.
 * <p>
 * This class is used to translate generic {@link EntityDefinition}s to platform-specific construct representing
 * Xatkit NLUServer entities.
 * <p>
 * <b>Note</b>: this class does not create references to existing entities, see
 * {@link NLUServerEntityReferenceMapper} for more information.
 *
 */
public class NLUServerEntityMapper {

    /**
     * The {@link NLUServerEntityReferenceMapper} used to map internal references to other entities.
     * <p>
     * These references are typically found in {@link CompositeEntityDefinition}s, that can contain other entities as
     * part of their values.
     */
    private NLUServerEntityReferenceMapper entityReferenceMapper;

    /**
     * Constructs a {@link NLUServerEntityMapper} with the provided {@code entityReferenceMapper}.
     *
     * @param entityReferenceMapper the {@link NLUServerEntityReferenceMapper} used to map internal references to
     *                              other entities
     * @throws NullPointerException if the provided {@code entityReferenceMapper} is {@code null}
     */
    public NLUServerEntityMapper(@NonNull NLUServerEntityReferenceMapper entityReferenceMapper) {
        this.entityReferenceMapper = entityReferenceMapper;
    }

    /**
     * Maps the provided {@code entityDefinition} to a NLUServer {@link EntityType}.
     *
     * @param entityDefinition the {@link EntityDefinition} to map
     * @return the created {@link EntityType}
     * @throws NullPointerException     if the provided {@code entityDefinition} is {@code null}
     * @throws IllegalArgumentException if the provided {@code entityDefinition} is a
     *                                  {@link BaseEntityDefinition}, or if the provided {@code entityDefinition}'s
     *                                  type is not supported
     */
    public EntityType mapEntityDefinition(@NonNull EntityDefinition entityDefinition) {
        if (entityDefinition instanceof BaseEntityDefinition) {
            return mapBaseEntityDefinition((BaseEntityDefinition) entityDefinition);
        } else if (entityDefinition instanceof CustomEntityDefinition) {
            return mapCustomEntityDefinition((CustomEntityDefinition) entityDefinition);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Cannot register the provided {0}, unsupported "
                            + "{1}", entityDefinition.getClass().getSimpleName(),
                    EntityDefinition.class.getSimpleName()));
        }
    }

    /**
     * Creates a Xatkit NLUServer {@link EntityType} from the provided {@code baseEntityDefinition}.
     *
     * @param baseEntityDefinition the {@link BaseEntityDefinition} to create an {@link EntityType} from
     * @return the created {@link EntityType}
     * @throws NullPointerException     if the provided {@code entityDefinition} is {@code null}
     */
    private EntityType mapBaseEntityDefinition(@NonNull BaseEntityDefinition baseEntityDefinition) {
        String entityName = entityReferenceMapper.getMappingFor(baseEntityDefinition);
        BaseEntityType entityType = new BaseEntityType(adaptEntityTypeNameToNLUServer(entityName));
        return entityType;

    }

    /**
     * Creates a Xatkit NLUServer {@link EntityType} from the provided {@code customEntityDefinition}.
     *
     * @param customEntityDefinition the {@link CustomEntityDefinition} to create an {@link EntityType} from
     * @return the created {@link EntityType}
     * @throws NullPointerException     if the provided {@code entityDefinition} is {@code null}
     * @throws IllegalArgumentException if the {@code customEntityDefinition}'s type is not supported
     * @see #createEntityEntriesForMapping(MappingEntityDefinition)
     */
    private EntityType mapCustomEntityDefinition(@NonNull CustomEntityDefinition customEntityDefinition) {
        String entityName = entityReferenceMapper.getMappingFor(customEntityDefinition);
        CustomEntityType entityType = new CustomEntityType(adaptEntityTypeNameToNLUServer(entityName));

        if (customEntityDefinition instanceof MappingEntityDefinition) {
            MappingEntityDefinition mappingEntityDefinition = (MappingEntityDefinition) customEntityDefinition;
            List<CustomEntityTypeEntry> entries = createEntityEntriesForMapping(mappingEntityDefinition);
            entityType.addAllEntries(entries);
        /* Composite entitites are not yet supported in Xatkit NLUServer
         else if (customEntityDefinition instanceof CompositeEntityDefinition) {

            CompositeEntityDefinition compositeEntityDefinition = (CompositeEntityDefinition) customEntityDefinition;
            List<EntityType.Entity> entities = createEntitiesForComposite(compositeEntityDefinition);
            builder.setKind(EntityType.Kind.KIND_LIST).addAllEntities(entities);
        } */
        }
        else {
            throw new IllegalArgumentException(MessageFormat.format("Cannot register the provided {0}, unsupported {1}",
                    customEntityDefinition.getClass().getSimpleName(), EntityDefinition.class.getSimpleName()));
        }
        return entityType;
    }

    /**
     * Creates the Xatkit NLUServer {@link CustomEntityTypeEntry} instances from the provided {@code
     * mappingEntityDefinition}.
     * <p>
     * {@link CustomEntityTypeEntry} instances are created from the provided {@link MappingEntityDefinition}'s entries,
     * and contain the specified <i>referredValue</i> as well as the list of <i>synonyms</i>.
     *
     * @param mappingEntityDefinition the {@link MappingEntityDefinition} to create the {@link CustomEntityTypeEntry}
     *                                instances from
     * @return the created {@link List} of NLUServer {@link CustomEntityTypeEntry} instances
     * @throws NullPointerException if the provided {@code mappingEntityDefinition} is {@code null}
     */
    private List<CustomEntityTypeEntry> createEntityEntriesForMapping(@NonNull MappingEntityDefinition mappingEntityDefinition) {
        List<CustomEntityTypeEntry> entries = new ArrayList<>();

        for (MappingEntityDefinitionEntry entryMapping : mappingEntityDefinition.getEntries()) {
            CustomEntityTypeEntry newEntry = new CustomEntityTypeEntry(entryMapping.getReferenceValue());
            newEntry.addAllSynonyms(entryMapping.getSynonyms());
            entries.add(newEntry);
        }
        return entries;

    }

    /**
     * Adapts the provided {@code EntityDefinition.name} by removing its {@code _} as this may cause issues when
     * tokenizing the names after ner replacing in the server.
     * <p>
     *
     * @param name the {@link EntityType} name to adapt
     * @return the adapted {@code intentDefinitionName}
     * @throws NullPointerException if the provided {@code name} is {@code null}
     */
    private String adaptEntityTypeNameToNLUServer(@NonNull String name) {
        return name.replaceAll("_", "");
    }
}
