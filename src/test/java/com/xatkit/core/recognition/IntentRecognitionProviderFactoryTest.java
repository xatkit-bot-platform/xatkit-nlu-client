package com.xatkit.core.recognition;

import com.xatkit.AbstractXatkitTest;
import com.xatkit.core.EventDefinitionRegistry;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.recognition.nluserver.NLUServerIntentRecognitionProvider;
import com.xatkit.core.recognition.nluserver.NLUServerIntentRecognitionProviderTest;
import com.xatkit.core.server.XatkitServer;
import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntentRecognitionProviderFactoryTest extends AbstractXatkitTest {

    private IntentRecognitionProvider provider;

    private XatkitBot xatkitBot;

    @Before
    public void setUp() {
        xatkitBot = mock(XatkitBot.class);
        when(xatkitBot.getEventDefinitionRegistry()).thenReturn(new EventDefinitionRegistry());
        when(xatkitBot.getXatkitServer()).thenReturn(mock(XatkitServer.class));
    }

    @After
    public void tearDown() {
        if(nonNull(provider) && !provider.isShutdown()) {
            try {
                provider.shutdown();
            } catch(IntentRecognitionProviderException e) {
                /*
                 * Nothing to do, the provider will be re-created anyways.
                 */
            }
        }
    }

    @Test
    public void getIntentRecognitionProviderNLUServerProperties() {
        provider = IntentRecognitionProviderFactory.getIntentRecognitionProvider(xatkitBot,
                NLUServerIntentRecognitionProviderTest.buildConfiguration());
        assertThat(provider).as("Not null IntentRecognitionProvider").isNotNull();
        assertThat(provider).as("IntentRecognitionProvider is a NLUServerIntentRecognitionProvider").isInstanceOf(NLUServerIntentRecognitionProvider.class);
        assertThat(provider.getPreProcessors()).as("PreProcessor list is empty").isEmpty();
        assertThat(provider.getPostProcessors()).as("PostProcessor list is empty").isEmpty();
    }


}
