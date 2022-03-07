package com.xatkit.core.recognition.dialogflow.mapper;

import com.google.cloud.dialogflow.v2.EntityType;
import com.xatkit.intent.BaseEntityDefinition;
import com.xatkit.intent.CompositeEntityDefinition;
import com.xatkit.intent.EntityDefinition;
import com.xatkit.intent.IntentFactory;
import com.xatkit.intent.MappingEntityDefinition;
import com.xatkit.intent.MappingEntityDefinitionEntry;
import com.xatkit.test.bot.IntentProviderTestBot;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class DialogFlowEntityMapperTest {

    private static IntentProviderTestBot intentProviderTestBot;

    @BeforeClass
    public static void setUpBeforeClass() {
        intentProviderTestBot = new IntentProviderTestBot();
    }

    private NLUServerEntityMapper mapper;

    @Test(expected = NullPointerException.class)
    public void constructNullEntityReferenceMapper() {
        mapper = new NLUServerEntityMapper(null);
    }

    @Test
    public void constructValid() {
        mapper = new NLUServerEntityMapper(new DialogFlowEntityReferenceMapper());
        assertThat(mapper).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapEntityDefinitionBaseEntity() {
        mapper = new NLUServerEntityMapper(new DialogFlowEntityReferenceMapper());
        BaseEntityDefinition baseEntityDefinition = IntentFactory.eINSTANCE.createBaseEntityDefinition();
        baseEntityDefinition.setEntityType(com.xatkit.intent.EntityType.ANY);
        mapper.mapEntityDefinition(baseEntityDefinition);
    }

    @Test
    public void mapEntityDefinitionMappingEntity() {
        mapper = new NLUServerEntityMapper(new DialogFlowEntityReferenceMapper());
        EntityType entityType = mapper.mapEntityDefinition(intentProviderTestBot.getMappingEntity());
        assertCorrectMappingForMappingEntity(intentProviderTestBot.getMappingEntity(), entityType);
    }

    @Test
    public void mapEntityDefinitionCompositeEntity() {
        mapper = new NLUServerEntityMapper(new DialogFlowEntityReferenceMapper());
        EntityType entityType = mapper.mapEntityDefinition(intentProviderTestBot.getCompositeEntity());
        assertCorrectMappingForCompositeEntity(intentProviderTestBot.getCompositeEntity(), entityType);

    }

    private void assertCorrectMappingForEntityDefinition(EntityDefinition entityDefinition, EntityType entityType) {
        assertThat(entityType).isNotNull();
        assertThat(entityType.getDisplayName()).isEqualTo(entityDefinition.getName());
    }

    private void assertCorrectMappingForMappingEntity(MappingEntityDefinition mappingEntityDefinition,
                                                      EntityType entityType) {
        assertCorrectMappingForEntityDefinition(mappingEntityDefinition, entityType);
        assertThat(entityType.getKind()).isEqualTo(EntityType.Kind.KIND_MAP);
        List<EntityType.Entity> entities = entityType.getEntitiesList();
        List<MappingEntityDefinitionEntry> entries = mappingEntityDefinition.getEntries();
        for (MappingEntityDefinitionEntry entry : entries) {
            List<EntityType.Entity> foundEntities = entities.stream().filter(e -> e
                    .getValue().equals(entry.getReferenceValue())).collect(Collectors.toList());
            assertThat(foundEntities).as("A single entity matches the entry").hasSize(1);
            com.google.cloud.dialogflow.v2.EntityType.Entity foundEntity = foundEntities.get(0);
            /*
             * There is 1 more synonym in the registered Entity: the value of the entity itself (see
             * https://dialogflow.com/docs/entities/developer-entities)
             */
            assertThat(foundEntity.getSynonymsList()).as("Valid synonym number").hasSize(entry.getSynonyms().size() +
                    1);
            assertThat(foundEntity.getSynonymsList()).as("Synonym list contains the entry reference value").contains
                    (entry.getReferenceValue());
            for (String entrySynonym : entry.getSynonyms()) {
                assertThat(foundEntity.getSynonymsList()).as("Synonym list contains the entry synonym " +
                        entrySynonym).contains(entrySynonym);
            }
        }
    }

    private void assertCorrectMappingForCompositeEntity(CompositeEntityDefinition compositeEntityDefinition,
                                                        EntityType entityType) {
        assertCorrectMappingForEntityDefinition(compositeEntityDefinition, entityType);
        assertThat(entityType.getKind()).isEqualTo(EntityType.Kind.KIND_LIST);
        assertThat(entityType.getEntitiesCount()).isEqualTo(compositeEntityDefinition.getEntries().size());
        /*
         * TODO check the content of the entity. This is related to the DialogFlowEntityReferenceMapperTest.
         */
    }
}
