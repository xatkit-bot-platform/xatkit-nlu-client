package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.intent.BaseEntityDefinition;
import com.xatkit.intent.CompositeEntityDefinition;
import com.xatkit.intent.CustomEntityDefinition;
import com.xatkit.intent.EntityDefinition;
import com.xatkit.intent.MappingEntityDefinition;
import com.xatkit.intent.MappingEntityDefinitionEntry;
import com.xatkit.core.recognition.nluserver.mapper.NLUServerEntityReferenceMapper;
import com.xatkit.core.recognition.nluserver.mapper.dsl.CustomEntityTypeEntry;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityType;
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
     * <p>
     * This method does not support {@link BaseEntityDefinition}s, because base entities are already deployed in
     * DialogFlow agents (and called system entities).
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
            throw new IllegalArgumentException(MessageFormat.format("Cannot map the provided {0} {1}, base entities "
                            + "are already mapped in DialogFlow", EntityDefinition.class.getSimpleName(),
                    entityDefinition.toString()));
        } else if (entityDefinition instanceof CustomEntityDefinition) {
            return mapCustomEntityDefinition((CustomEntityDefinition) entityDefinition);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Cannot register the provided {0}, unsupported "
                            + "{1}", entityDefinition.getClass().getSimpleName(),
                    EntityDefinition.class.getSimpleName()));
        }
    }

    /**
     * Creates a Xatkit NLUServer {@link EntityType} from the provided {@code customEntityDefinition}.
     *
     * @param customEntityDefinition the {@link CustomEntityDefinition} to create an {@link EntityType} from
     * @return the created {@link EntityType}
     * @throws NullPointerException     if the provided {@code entityDefinition} is {@code null}
     * @throws IllegalArgumentException if the {@code customEntityDefinition}'s type is not supported
     * @see #createEntitiesForMapping(MappingEntityDefinition)
     */
    private EntityType mapCustomEntityDefinition(@NonNull CustomEntityDefinition customEntityDefinition) {
      /* TO IMPLEMENT

      String entityName = customEntityDefinition.getName();
        EntityType.Builder builder = EntityType.newBuilder().setDisplayName(entityName);
        if (customEntityDefinition instanceof MappingEntityDefinition) {
            MappingEntityDefinition mappingEntityDefinition = (MappingEntityDefinition) customEntityDefinition;
            List<EntityType.Entity> entities = createEntitiesForMapping(mappingEntityDefinition);
            builder.setKind(EntityType.Kind.KIND_MAP).addAllEntities(entities);
        } else if (customEntityDefinition instanceof CompositeEntityDefinition) {
            CompositeEntityDefinition compositeEntityDefinition = (CompositeEntityDefinition) customEntityDefinition;
            List<EntityType.Entity> entities = createEntitiesForComposite(compositeEntityDefinition);
            builder.setKind(EntityType.Kind.KIND_LIST).addAllEntities(entities);
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Cannot register the provided {0}, unsupported {1}",
                    customEntityDefinition.getClass().getSimpleName(), EntityDefinition.class.getSimpleName()));
        }
        return builder.build();

       */
        return null;
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
    private List<CustomEntityTypeEntry> createEntitiesForMapping(@NonNull MappingEntityDefinition mappingEntityDefinition) {
        /* TO IMPLEMENT
        List<EntityType.Entity> entities = new ArrayList<>();

        for (MappingEntityDefinitionEntry entry : mappingEntityDefinition.getEntries()) {
            EntityType.Entity.Builder builder = EntityType.Entity.newBuilder().setValue(entry.getReferenceValue())
                    .addAllSynonyms(entry.getSynonyms()).addSynonyms(entry.getReferenceValue());
            entities.add(builder.build());
        }
        return entities;

         */
        return null;
    }



}
