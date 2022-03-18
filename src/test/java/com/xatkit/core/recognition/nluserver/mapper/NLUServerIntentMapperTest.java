package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.intent.IntentDefinition;
import com.xatkit.test.bot.IntentProviderTestBot;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NLUServerIntentMapperTest {

    private static IntentProviderTestBot intentProviderTestBot;

    @BeforeClass
    public static void setUpBeforeClass() {
        intentProviderTestBot = new IntentProviderTestBot();
    }

    private NLUServerIntentMapper mapper;

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() {
        mapper = new NLUServerIntentMapper(null, new NLUServerEntityReferenceMapper());
    }

    @Test(expected = NullPointerException.class)
    public void constructNullEntityReferenceMapper() {
        mapper = new NLUServerIntentMapper(getValidConfiguration(), null);
    }

    @Test
    public void constructValid() {
        mapper = new NLUServerIntentMapper(getValidConfiguration(), new NLUServerEntityReferenceMapper());
        assertThat(mapper).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void mapNull() throws IntentRecognitionProviderException {
        mapper = new NLUServerIntentMapper(getValidConfiguration(), new NLUServerEntityReferenceMapper());
        mapper.mapIntentDefinition(null);
    }

    @Test
    public void mapSimpleIntent() throws IntentRecognitionProviderException {
        mapper = new NLUServerIntentMapper(getValidConfiguration(), new NLUServerEntityReferenceMapper());
        Intent intent = mapper.mapIntentDefinition(intentProviderTestBot.getSimpleIntent());
        assertCorrectMappingForIntentDefinition(intentProviderTestBot.getSimpleIntent(), intent);
    }

    @Test
    public void mapSystemEntityIntent() throws IntentRecognitionProviderException {
        mapper = new NLUServerIntentMapper(getValidConfiguration(), new NLUServerEntityReferenceMapper());
        Intent intent = mapper.mapIntentDefinition(intentProviderTestBot.getSystemEntityIntent());
        assertCorrectMappingForIntentDefinitionWithEntityReference(intentProviderTestBot.getSystemEntityIntent(),
                intent);
    }

    @Test
    public void mapMappingEntityIntent() throws IntentRecognitionProviderException {
        mapper = new NLUServerIntentMapper(getValidConfiguration(), new NLUServerEntityReferenceMapper());
        Intent intent = mapper.mapIntentDefinition(intentProviderTestBot.getMappingEntityIntent());
        assertCorrectMappingForIntentDefinitionWithEntityReference(intentProviderTestBot.getMappingEntityIntent(),
                intent);
    }



    private void assertCorrectMappingForIntentDefinition(IntentDefinition intentDefinition, Intent nluServerIntent) {
        assertThat(nluServerIntent).isNotNull();
        assertThat(nluServerIntent.getName()).isEqualTo(intentDefinition.getName());
        assertThat(nluServerIntent.getTrainingSentences()).isNotEmpty();
        for (String trainingPhrase : intentDefinition.getTrainingSentences()) {
            assertIntentHasTrainingPhrase(nluServerIntent, trainingPhrase);
        }
    }

    private void assertCorrectMappingForIntentDefinitionWithEntityReference(IntentDefinition intentDefinition,
                                                                            Intent nluServerIntent) {
        assertCorrectMappingForIntentDefinition(intentDefinition, nluServerIntent);
        // for (ContextParameter parameter : intentDefinition.getParameters()) {
       //     assertIntentContainsParameter(nluServerIntent, parameter);
       // }
    }

    private void assertIntentHasTrainingPhrase(Intent intent, String trainingPhrase) {
        boolean found = false;
        for (String intentTrainingPhrase : intent.getTrainingSentences()) {
            if (intentTrainingPhrase.equals(trainingPhrase)) {
                found = true;
            }
        }
        assertThat(found).isTrue();
    }

    private NLUServerConfiguration getValidConfiguration() {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(NLUServerConfiguration.BOT_NAME, "BOTPROJECT");
        configuration.addProperty(NLUServerConfiguration.URL, "BOTURL");
        return new NLUServerConfiguration(configuration);
    }
}
