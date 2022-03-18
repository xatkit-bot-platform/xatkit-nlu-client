package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.EventDefinitionRegistry;
import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.IntentRecognitionProviderFactory;
import com.xatkit.core.recognition.IntentRecognitionProviderTest;
import com.xatkit.intent.CompositeEntityDefinition;
import com.xatkit.intent.EntityDefinition;
import com.xatkit.intent.MappingEntityDefinition;
import com.xatkit.test.util.VariableLoaderHelper;
import fr.inria.atlanmod.commons.log.Log;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class NLUServerIntentRecognitionProviderTest extends IntentRecognitionProviderTest<NLUServerIntentRecognitionProvider> {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        //    this.contextFactory = new NLUServerTestingStateContextFactory();
    }


    public static Configuration buildConfiguration() {
        return buildConfiguration(VariableLoaderHelper.getVariable(NLUServerConfiguration.BOT_NAME),
                VariableLoaderHelper
                .getVariable(NLUServerConfiguration.LANGUAGE_CODE_KEY));
    }

    private static Configuration buildConfiguration(String botName, String languageCode) {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(IntentRecognitionProviderFactory.INTENT_PROVIDER_KEY,
                NLUServerConfiguration.NLUSERVER_INTENT_PROVIDER);
        configuration.addProperty(NLUServerConfiguration.BOT_NAME, botName);
        configuration.addProperty(NLUServerConfiguration.LANGUAGE_CODE_KEY, languageCode);
        configuration.addProperty(NLUServerConfiguration.URL , VariableLoaderHelper
                .getVariable(NLUServerConfiguration.URL));
        return configuration;
    }

    @After
    public void tearDown() throws IntentRecognitionProviderException {

    }

    @Test(expected = NullPointerException.class)
    public void constructNullEventRegistry() {
        intentRecognitionProvider = new NLUServerIntentRecognitionProvider(null, buildConfiguration(), null);
    }

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() {
        intentRecognitionProvider = new NLUServerIntentRecognitionProvider(new EventDefinitionRegistry(), null, null);
    }

    /*
    @Test
    public void registerIntentDefinitionAlreadyRegistered() throws IntentRecognitionProviderException {
        intentRecognitionProvider = getIntentRecognitionProvider();
        registeredIntentDefinition = intentProviderTestBot.getSimpleIntent();
        intentRecognitionProvider.registerIntentDefinition(registeredIntentDefinition);
        assertThatThrownBy(() -> intentRecognitionProvider.registerIntentDefinition(registeredIntentDefinition)).isInstanceOf(IntentRecognitionProviderException.class);
    }

    @Test
    public void deleteEntityReferencedInIntent() throws IntentRecognitionProviderException {
        intentRecognitionProvider = getIntentRecognitionProvider();
        registeredEntityDefinitions.add(intentProviderTestBot.getMappingEntity());
        intentRecognitionProvider.registerEntityDefinition(intentProviderTestBot.getMappingEntity());
        registeredIntentDefinition = intentProviderTestBot.getMappingEntityIntent();
        intentRecognitionProvider.registerIntentDefinition(registeredIntentDefinition);
        assertThatThrownBy(() -> intentRecognitionProvider.deleteEntityDefinition(intentProviderTestBot.getMappingEntity())).isInstanceOf(IntentRecognitionProviderException.class);
    }
*/



    @Override
    protected NLUServerIntentRecognitionProvider getIntentRecognitionProvider() {
        return new NLUServerIntentRecognitionProvider(eventRegistry, buildConfiguration(), null);
    }


    // SET OF TESTS FROM THE TEST SUPERCLASS THAT ARE EITHER NOT SUPPORTED OR NOT WELL ADAPTED TO THE SEMANTICS OF
    // THIS PROVIDER


    /*
     * Composite entities are not supported by our connector for now.
     */
    @Override
    public void registerCompositeEntityReferencedEntitiesNotRegistered() throws IntentRecognitionProviderException {

    }

    /*
     * Composite entities are not supported by our connector for now.
     */
    @Override
    public void registerCompositeEntityReferencedEntitiesAlreadyRegistered() throws IntentRecognitionProviderException {
    }

    /*
     * Deleting intents is not supported by our connector for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void deleteExistingIntent() throws IntentRecognitionProviderException {
        super.deleteExistingIntent();
    }

    /*
     * Deleting entities is not supported by our connector for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void deleteEntityNotReferenced() throws IntentRecognitionProviderException {
        super.deleteEntityNotReferenced();
    }

    @Test
    public void getSimpleIntent() throws IntentRecognitionProviderException {
    }

    @Test
    public void getCompositeEntityIntent() throws IntentRecognitionProviderException {
    }

    @Test
    public void getSystemEntityIntent() throws IntentRecognitionProviderException {
    }

    @Test
    public void getMappingEntityIntent() throws IntentRecognitionProviderException {
    }
}
