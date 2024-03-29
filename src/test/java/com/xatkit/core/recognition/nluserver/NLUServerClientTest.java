package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.mapper.dsl.BotData;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import com.xatkit.test.util.VariableLoaderHelper;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class NLUServerClientTest {

    private NLUServerConfiguration validConfiguration;
    private BotData botData;

    private NLUServerClientAPIWrapper nluServerClientWrapper;

    @Before
    public void setUp() {
        Configuration configuration = new BaseConfiguration();
        configuration.addProperty(NLUServerConfiguration.BOT_NAME,
                VariableLoaderHelper.getVariable(NLUServerConfiguration.BOT_NAME));
        configuration.addProperty(NLUServerConfiguration.URL, VariableLoaderHelper
                .getVariable(NLUServerConfiguration.URL));
        configuration.addProperty(NLUServerConfiguration.FORCE_OVERWRITE, VariableLoaderHelper
                .getVariable(NLUServerConfiguration.FORCE_OVERWRITE));
        this.validConfiguration = new NLUServerConfiguration(configuration);
        botData = new BotData(validConfiguration.getBotName());
    }

    @After
    public void tearDown() {
        if(nonNull(nluServerClientWrapper)) {
            nluServerClientWrapper.shutdown();
        }
    }

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() throws IntentRecognitionProviderException {
        nluServerClientWrapper = new NLUServerClientAPIWrapper(null, null);
    }

    @Test
    public void constructValidConfiguration() throws IntentRecognitionProviderException {
        nluServerClientWrapper = new NLUServerClientAPIWrapper(validConfiguration, botData);
        assertThat(nluServerClientWrapper).isNotNull();
    }


    // TESTS TO RUN WITH A XATKIT NLU SERVER DEPLOYED IN THE URL PROVIDED IN THE TEST-VARIABLES.PROPERTIES FILE

    @Test
    @Ignore
    public void deployAndTrainEmptyBot() throws IntentRecognitionProviderException {
        nluServerClientWrapper = new NLUServerClientAPIWrapper(validConfiguration, botData);
        boolean success = false;
        success = nluServerClientWrapper.deployAndTrainBot();
        assertThat(botData.getUUID()).isNotNull(); // to chech the bot was deployed
        assertThat(success).isFalse(); // Empty bots generate an exception when attempting to train them
    }

    @Test
    @Ignore
    public void deployAndTrainSimpleBot() throws IntentRecognitionProviderException {
        initializeSimpleBotData(botData);
        nluServerClientWrapper = new NLUServerClientAPIWrapper(validConfiguration, botData);
        boolean success = false;
        success = nluServerClientWrapper.deployAndTrainBot();
        assertThat(success).isTrue();
    }

    @Test
    @Ignore
    public void predictExistingContext() throws IntentRecognitionProviderException {
        Prediction prediction;
        initializeSimpleBotData(botData);
        nluServerClientWrapper = new NLUServerClientAPIWrapper(validConfiguration, botData);
        boolean success = nluServerClientWrapper.deployAndTrainBot();
        assertThat(success).isTrue();
        prediction = nluServerClientWrapper.predict(botData.getNluContext("context1"), "he loves your dog");
        assertThat(prediction).isNotNull();
        assertThat(prediction.getClassifications().size()).isEqualTo(2);
        assertThat(prediction.getTopClassification().getIntent().getName()).isEqualTo("intent1Ccontext1");
    }

    private void initializeSimpleBotData(BotData bot) {
        NLUContext context1 = new NLUContext("context1");
        NLUContext context2 = new NLUContext("context2");
        Intent i1 = new Intent("intent1Ccontext1");
        i1.addTrainingSentence("I love your dog"); i1.addTrainingSentence("I love your cat");i1.addTrainingSentence(
                "You really love my dog");
        Intent i2 = new Intent("intent2Ccontext1");
        i2.addTrainingSentence("Hello"); i2.addTrainingSentence("Hi");
        context1.addIntentReference(i1); context1.addIntentReference(i2);
        Intent i3 = new Intent("intent1Context2");
        i3.addTrainingSentence("Yes");i3.addTrainingSentence("Absolutely");i3.addTrainingSentence("Yes!");
        context2.addIntentReference(i3);
        botData.addNLUContext(context1);
        botData.addNLUContext(context2);
        botData.addIntent(i1);
        botData.addIntent(i2);
        botData.addIntent(i3);
    }

}
