package com.xatkit.core.recognition.nluserver;

import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.configuration2.Configuration;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;

/**
 * Contains NLUServer-related configuration.
 * <p>
 * This class can be initialized with a {@link Configuration} instance, and takes care of extracting the
 * NLUServer-related properties.
 * <p>
 * The base {@link Configuration} used to initialize this class can be accessed through {@link #getBaseConfiguration()}.
 */
@SuppressWarnings("checkstyle:JavadocStyle")
@Value
public class NLUServerConfiguration {


    /**
     * The base {@link Configuration} used to initialize the {@link NLUServerConfiguration}.
     */
    private Configuration baseConfiguration;

    /**
     * The {@link Configuration} value to set to specify that your bot is using NLUServerFlow.
     * <p>
     * This value should be affected to
     * {@link com.xatkit.core.recognition.IntentRecognitionProviderFactory#INTENT_PROVIDER_KEY}.
     */
    public static final String NLUSERVER_INTENT_PROVIDER = NLUServerIntentRecognitionProvider.class.getName();

    /**
     * The {@link Configuration} key to store the unique identifier of the NLUServer project.
     */
    public static final String BOT_NAME = "xatkit.nluserver.botname";

    /**
     * Overwrite an existing bot with the same name if found
     * This is useful in development but dangerous in production as you can easily make a mistake and
     * kill a deployed bot
     */
    public static final String FORCE_OVERWRITE = "xatkit.nluserver.force_overwrite";

    /**
     * The {@link Configuration} key to store the confidence threshold.
     * <p>
     * This threshold is used to accept/reject a matched intent based on its confidence. The default value is {@code
     * 0} (accept all intents).
     */
    public static final String CONFIDENCE_THRESHOLD = "xatkit.nluserver.confidence.threshold";

    /**
     * The url of the Xatkit NLU Server
     */
    public static final String URL = "xatkit.nluserver.url";

    /**
     * The {@link Configuration} key to store the code of the language processed by NLUServer.
     */
    public static final String LANGUAGE_CODE = "xatkit.nluserver.language";

    /**
     * The {@link Configuration} key to store the code of the language processed by NLUServer.
     */
    public static final String LANGUAGE_REGION_CODE = "xatkit.nluserver.language.region";

    /**
     * The {@link Configuration} key to store the timezone processed by NLUServer.
     */
    public static final String TIMEZONE = "xatkit.nluserver.timezone";

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
    public static final String INPUT_MAX_NUM_TOKENS = "xatkit.nluserver.input_max_num_tokens";

    /**
     * Whether to use a stemmer
     */
    public static final String STEMMER = "xatkit.nluserver.stemmer";

    /**
     * Whether to automatically assign zero probabilities to sentences with all tokens being oov ones
     */
    public static final String DISCARD_OOV_SENTENCES = "xatkit.nluserver.discard_oov_sentences";

    /**
     * Whether to check for exact match between the sentence to predict and one of the training sentences
     */
    public static final String CHECK_EXACT_PREDICTION_MATCH = "xatkit.nluserver.check_exact_prediction_match";

    /**
     * Whether to run Named Entity Recognition in the intent recognition process.
     */
    public static final String USE_NER_IN_PREDICTION = "xatkit.nluserver.ner.use_ner_in_prediction";

    /**
     * The activation function of the last layer
     */
    public static final String ACTIVATION_LAST_LAYER = "xatkit.nluserver.activation_last_layer";

    /**
     * The activation function of the hidden layers
     */
    public static final String ACTIVATION_HIDDEN_LAYERS = "xatkit.nluserver.activation_hidden_layers";

    /**
     * The unique identifier of the NLUServer project.
     *
     * @see #BOT_NAME
     */
    private String botName;

    /**
     * The flag to easily retrain deployed bots.
     *
     * @see #FORCE_OVERWRITE
     */
    private boolean forceOverwrite;

    /**
     * @see #CONFIDENCE_THRESHOLD
     */
    private float confidenceThreshold;

    /**
     * @see #URL
     */
    private String url;

    /**
     * The language code of the Xatkit NLUServer project.
     *
     * @see #LANGUAGE_CODE
     */
    private String languageCode;

    /**
     * The language region code of the Xatkit NLUServer project.
     *
     * @see #LANGUAGE_CODE
     */
    private String languageRegionCode;

    /**
     * The timezone of the Xatkit NLUServer project.
     *
     * @see #TIMEZONE
     */
    private String timezone;

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
     * @see #LOWERCASE
     */
    private String oovToken;

    /**
     * @see #NUM_EPOCHS
     */
    private int numEpochs;

    /**
     * @see #EMBEDDING_DIM
     */
    private int embeddingDim;

    /**
     * @see #INPUT_MAX_NUM_TOKENS
     */
    private int inputMaxNumTokens;

    /**
     * @see #STEMMER
     */
    private boolean stemmer;

    /**
     * @see #DISCARD_OOV_SENTENCES
     */
    private boolean discardOovSentences;

    /**
     * @see #CHECK_EXACT_PREDICTION_MATCH
     */
    private boolean checkExactPredictionMatch;

    /**
     * @see #USE_NER_IN_PREDICTION
     */
    private boolean useNerInPrediction;

    /**
     * @see #ACTIVATION_LAST_LAYER
     */
    private String activationLastLayer;

    /**
     * @see #ACTIVATION_HIDDEN_LAYERS
     */
    private String activationHiddenLayers;

    /**
     * The default language processed by the NLU Server.
     */
    public static final String DEFAULT_LANGUAGE_CODE = "en";

    /**
     * The default language region code processed by the NLU Server.
     */
    public static final String DEFAULT_LANGUAGE_REGION_CODE = "";

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
        checkArgument(baseConfiguration.containsKey(URL), "The provided %s does not contain a value for "
                + "the mandatory property %s", Configuration.class.getSimpleName(), URL);

        this.botName = baseConfiguration.getString(BOT_NAME);
        this.forceOverwrite = baseConfiguration.getBoolean(FORCE_OVERWRITE, false);
        this.confidenceThreshold = baseConfiguration.getFloat(CONFIDENCE_THRESHOLD, 0.3f);
        this.url = baseConfiguration.getString(URL);

        if (baseConfiguration.containsKey(LANGUAGE_CODE)) {
            languageCode = baseConfiguration.getString(LANGUAGE_CODE);
        } else {
            Log.warn("No language code provided, using the default one ({0})", DEFAULT_LANGUAGE_CODE);
            languageCode = DEFAULT_LANGUAGE_CODE;
        }

        if (baseConfiguration.containsKey(LANGUAGE_REGION_CODE)) {
            languageRegionCode = baseConfiguration.getString(LANGUAGE_REGION_CODE);
        } else {
            Log.warn("No language region code provided, using the default one ({0})", DEFAULT_LANGUAGE_REGION_CODE);
            languageRegionCode = DEFAULT_LANGUAGE_REGION_CODE;
        }

        this.timezone = baseConfiguration.getString(TIMEZONE, "Europe/Madrid");
        this.numWords = baseConfiguration.getInt(NUM_WORDS, 1000);
        this.lower = baseConfiguration.getBoolean(LOWERCASE, true);
        this.oovToken = baseConfiguration.getString(OOV_TOKEN,"<OOV>");
        this.numEpochs = baseConfiguration.getInt(NUM_EPOCHS, 300);
        this.embeddingDim = baseConfiguration.getInt(EMBEDDING_DIM, 16);
        this.inputMaxNumTokens = baseConfiguration.getInt(INPUT_MAX_NUM_TOKENS, 30);
        this.stemmer = baseConfiguration.getBoolean(STEMMER, true);
        this.discardOovSentences = baseConfiguration.getBoolean(DISCARD_OOV_SENTENCES, true);
        this.checkExactPredictionMatch  = baseConfiguration.getBoolean(CHECK_EXACT_PREDICTION_MATCH, true);
        this.useNerInPrediction = baseConfiguration.getBoolean(USE_NER_IN_PREDICTION, true);
        this.activationLastLayer = baseConfiguration.getString(ACTIVATION_LAST_LAYER, "sigmoid");
        this.activationHiddenLayers = baseConfiguration.getString(ACTIVATION_HIDDEN_LAYERS, "tanh");

    }

}
