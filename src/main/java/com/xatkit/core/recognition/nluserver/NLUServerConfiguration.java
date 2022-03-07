package com.xatkit.core.recognition.nluserver;

import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.configuration2.Configuration;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;

/**
 * Contains DialogFlow-related configuration.
 * <p>
 * This class can be initialized with a {@link Configuration} instance, and takes care of extracting the
 * DialogFlow-related properties.
 * <p>
 * The base {@link Configuration} used to initialize this class can be accessed through {@link #getBaseConfiguration()}.
 */
@SuppressWarnings("checkstyle:JavadocStyle")
@Value
public class NLUServerConfiguration {

    /**
     * The {@link Configuration} value to set to specify that your bot is using DialogFlow.
     * <p>
     * This value should be affected to
     * {@link com.xatkit.core.recognition.IntentRecognitionProviderFactory#INTENT_PROVIDER_KEY}.
     */
    public static final String NLUSERVER_INTENT_PROVIDER = NLUServerIntentRecognitionProvider.class.getName();

    /**
     * The {@link Configuration} key to store the unique identifier of the DialogFlow project.
     */
    public static final String BOT_NAME = "xatkit.nluserver.botname";

    /**
     * The {@link Configuration} key to store the code of the language processed by DialogFlow.
     */
    public static final String LANGUAGE_CODE_KEY = "xatkit.nluserver.language";

    /**
     * The default language processed by the NLU Server.
     */
    public static final String DEFAULT_LANGUAGE_CODE = "en-US";

    /**
     * Overwrite an existing bot with the same name if found
     * This is useful in development but dangerous in production as you can easily make a mistake and
     * kill a deployed bot
     */
    public static final String FORCE_OVERWRITE = "xatkit.nluserver.force_overwrite";


    /**
     * Max num of words to keep in the bot vocabulary
     */
    public static final String NUM_WORDS = "xatkit.nluserver.num_words";

    /**
     * Whether all sentences should be transformed to lowercase
     */
    public static final String LOWERCASE = "xatkit.nluserver.lowercase";

    /**
     * Symbol to use to signal out of vocabulary tokens
     */
    public static final String OOV_TOKEN = "xatkit.nluserver.oov_token";

    /**
     * Number of epochs to be run during training
     */
    public static final String NUM_EPOCHS = "xatkit.nluserver.num_epochs";

    /**
     * Number of embedding dimensions to be used when embedding the words
     */
    public static final String EMBEDDING_DIM = "xatkit.nluserver.embedding_dim";

    /**
     * Max length (in num tokens) of the sentences
     */
    public static final String MAX_NUM_TOKENS = "xatkit.nluserver.max_num_tokens";

    /**
     * Whether to use a stemmer
     */
    public static final String STEMMER = "xatkit.nluserver.stemmer";



    /**
     * The {@link Configuration} key to store the confidence threshold.
     * <p>
     * This threshold is used to accept/reject a matched intent based on its confidence. The default value is {@code
     * 0} (accept all intents).
     */
    public static final String CONFIDENCE_THRESHOLD_KEY = "xatkit.nluserver.confidence.threshold";

    /**
     * The base {@link Configuration} used to initialized the {@link NLUServerConfiguration}.
     */
    private Configuration baseConfiguration;

    /**
     * The unique identifier of the NLUServer project.
     *
     * @see #BOT_NAME
     */
    private String botId;

    /**
     * The language code of the DialogFlow project.
     *
     * @see #LANGUAGE_CODE_KEY
     */
    private String languageCode;


    /**
     * The flag to easily retrain deployed bots.
     *
     * @see #FORCE_OVERWRITE
     */
    private boolean forceOverwrite;

    /**
     * Max number of words to keep in the vocabulary.
     *
     * @see #NUM_WORDS
     */
    private int numWords;



    /**
     * @see #LOWERCASE
     */
    private boolean lower;

    /**
     * @see #NUM_EPOCHS
     */
    private int numEpochs;

    /**
     * @see #LOWERCASE
     */
    private String oovToken;

    /**
     * @see #EMBEDDING_DIM
     */
    private int embeddingDim;

    /**
     * @see #MAX_NUM_TOKENS
     */
    private int maxNumTokens;

    /**
     * @see #STEMMER
     */
    private boolean stemmer;

    /**
     * @see #CONFIDENCE_THRESHOLD_KEY
     */
     private float confidenceThreshold;



    /**
     * Initializes the {@link NLUServerConfiguration} with the provided {@code baseConfiguration}.
     *
     * @param baseConfiguration the {@link Configuration} to load the values from
     * @throws NullPointerException     if the provided {@code baseConfiguration} is {@code null}
     * @throws IllegalArgumentException if the provided {@code baseConfiguration} does not contain a
     *                                  {@link #BOT_NAME} value
     */
    public NLUServerConfiguration(@NonNull Configuration baseConfiguration) {
        this.baseConfiguration = baseConfiguration;
        checkArgument(baseConfiguration.containsKey(BOT_NAME), "The provided %s does not contain a value for "
                + "the mandatory property %s", Configuration.class.getSimpleName(), BOT_NAME);
        this.botId = baseConfiguration.getString(BOT_NAME);
        if (baseConfiguration.containsKey(LANGUAGE_CODE_KEY)) {
            languageCode = baseConfiguration.getString(LANGUAGE_CODE_KEY);
        } else {
            Log.warn("No language code provided, using the default one ({0})", DEFAULT_LANGUAGE_CODE);
            languageCode = DEFAULT_LANGUAGE_CODE;
        }

        this.forceOverwrite = baseConfiguration.getBoolean(FORCE_OVERWRITE, false);
        this.confidenceThreshold = baseConfiguration.getFloat(CONFIDENCE_THRESHOLD_KEY, 0);
        this.maxNumTokens = baseConfiguration.getInt(MAX_NUM_TOKENS, 30);
        this.stemmer = baseConfiguration.getBoolean(STEMMER, true);
        this.embeddingDim = baseConfiguration.getInt(EMBEDDING_DIM, 16);
        this.lower = baseConfiguration.getBoolean(LOWERCASE, true);
        this.oovToken = baseConfiguration.getString(OOV_TOKEN,"<OOV>");
        this.numEpochs = baseConfiguration.getInt(NUM_EPOCHS, 300);
        this.numWords = baseConfiguration.getInt(NUM_WORDS, 1000);


    }

}
