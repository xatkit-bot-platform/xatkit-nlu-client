package com.xatkit.core.recognition.nluserver;

import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NLUServerConfigurationTest {

    /**
     * The {@link Configuration} value to set to specify that your bot is using Xatkit's own NLU Server.
     * <p>
     * This value should be affected to
     * {@link com.xatkit.core.recognition.IntentRecognitionProviderFactory#INTENT_PROVIDER_KEY}.
     */
    public static final String NLUSERVER_INTENT_PROVIDER = NLUServerIntentRecognitionProvider.class.getName();


    private static String BOT_NAME = "BOTPROJECT";
    private static String BOT_URL = "BOTURL";

    private Configuration baseConfiguration;

    private NLUServerConfiguration configuration;

    @Before
    public void setUp() {
        //Initializing a valid configuration with the mandatory properties
        this.baseConfiguration = new BaseConfiguration();
        this.baseConfiguration.addProperty(NLUServerConfiguration.BOT_NAME, BOT_NAME);
        this.baseConfiguration.addProperty(NLUServerConfiguration.URL, BOT_URL);
    }

    @Test(expected = NullPointerException.class)
    public void constructNullConfiguration() {
        configuration = new NLUServerConfiguration(null);
    }

    /*
     * IllegalArgumentException expected because the configuration must contain a project id.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructEmptyConfiguration() {
        /*
         * Use new BaseConfiguration() to make sure the configuration is empty.
         */
        configuration = new NLUServerConfiguration(new BaseConfiguration());
    }

    @Test
    public void constructWithBotName() {
        configuration = new NLUServerConfiguration(baseConfiguration);
        assertThat(configuration.getBotName()).isEqualTo(BOT_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructWithoutBotName() {
        /*
         * Use new BaseConfiguration() to make sure the base configuration does not contain the project id.
         */
        configuration = new NLUServerConfiguration(new BaseConfiguration());
    }

    @Test
    public void constructWithLanguageCode() {
        baseConfiguration.addProperty(NLUServerConfiguration.LANGUAGE_CODE, "LANGUAGE");
        configuration = new NLUServerConfiguration(baseConfiguration);
        assertThat(configuration.getLanguageCode()).isEqualTo("LANGUAGE");
    }

    @Test
    public void constructWithoutLanguageCode() {
        configuration = new NLUServerConfiguration(baseConfiguration);
        assertThat(configuration.getLanguageCode()).isEqualTo("en");
    }

    @Test
    public void constructWithConfidenceThreshold() {
        baseConfiguration.addProperty(NLUServerConfiguration.CONFIDENCE_THRESHOLD, 10.3f);
        configuration = new NLUServerConfiguration(baseConfiguration);
        assertThat(configuration.getConfidenceThreshold()).isEqualTo(10.3f);
    }

    @Test
    public void constructWithoutConfidenceThreshold() {
        configuration = new NLUServerConfiguration(baseConfiguration);
        assertThat(configuration.getConfidenceThreshold()).isEqualTo(0);
    }

    @Test
    public void constructWithForceOverwrite() {
        baseConfiguration.addProperty(NLUServerConfiguration.FORCE_OVERWRITE, true);
        configuration = new NLUServerConfiguration(baseConfiguration);
        assertThat(configuration.isForceOverwrite()).isTrue();
    }

    @Test
    public void constructWithoutForceOverwrite() {
        configuration = new NLUServerConfiguration(baseConfiguration);
        assertThat(configuration.isForceOverwrite()).isFalse();
    }


}
