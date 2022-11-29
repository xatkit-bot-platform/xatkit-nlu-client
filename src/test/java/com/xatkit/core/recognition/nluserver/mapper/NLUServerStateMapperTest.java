package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.core.recognition.nluserver.mapper.dsl.IntentReference;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.core.recognition.nluserver.utils.FakeState;
import com.xatkit.dsl.DSL;
import com.xatkit.execution.State;
import com.xatkit.intent.IntentDefinition;
import com.xatkit.test.bot.IntentProviderTestBot;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NLUServerStateMapperTest {

    private static IntentProviderTestBot intentProviderTestBot;

    private FakeState state;


    @BeforeClass
    public static void setUpBeforeClass() {
        intentProviderTestBot = new IntentProviderTestBot();
    }

    private NLUServerStateMapper mapper;

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() {
        mapper = new NLUServerStateMapper(null);
    }

    @Test
    public void constructValid() {
        mapper = new NLUServerStateMapper(getValidConfiguration());
        assertThat(mapper).isNotNull();
    }

    @Test(expected = NullPointerException.class)
    public void mapNull() throws IntentRecognitionProviderException {
        mapper = new NLUServerStateMapper(getValidConfiguration());
        mapper.mapStateDefinition(null);
    }

    @Test
    public void mapSimpleState() throws IntentRecognitionProviderException {
        state = new FakeState();
        state.setName("FakeState");
        state.setIntents(createTwoSimpleIntents());
        mapper = new NLUServerStateMapper(getValidConfiguration());
        NLUContext nluContext = mapper.mapStateDefinition(state);
        assertCorrectMappingForState(state, nluContext);
    }

    //We check the context has registered the intent names, the transformation of such intent names to actual Intents
    // takes place in the NLUServerIntentRecognitionProvider#prepareTrainingData method
    private void assertCorrectMappingForState(State state, NLUContext nluContext) {
        assertThat(nluContext).isNotNull();
        assertThat(nluContext.getName()).isEqualTo(state.getName());
        assertThat(nluContext.getIntentReferences()).isNotEmpty();
        for (IntentDefinition intentDefinition : state.getAllAccessedIntents()) {
            assertNLUContextHasIntentReference(nluContext, intentDefinition);
        }
    }


    private void assertNLUContextHasIntentReference(NLUContext context, IntentDefinition intentDefinition) {
        boolean found = false;
        for (IntentReference intentRef : context.getIntentReferences()) {
            if (intentRef.getName().equals(intentDefinition.getName())) {
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


    private List<IntentDefinition> createTwoSimpleIntents() {
        List<IntentDefinition> intentDefinitions = new ArrayList<>();
        intentDefinitions.add(DSL.intent("SimpleIntent").trainingSentence("Greetings").getIntentDefinition());
        intentDefinitions.add(DSL.intent("SecondSimpleIntent").trainingSentence("Contact").getIntentDefinition());
        return intentDefinitions;
    }




}
