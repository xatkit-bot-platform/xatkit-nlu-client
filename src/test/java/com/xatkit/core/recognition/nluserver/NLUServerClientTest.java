package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.mapper.dsl.BotData;
import com.xatkit.test.util.VariableLoaderHelper;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.Before;
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
        this.validConfiguration = new NLUServerConfiguration(configuration);
        botData = new BotData("botName");
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


}
