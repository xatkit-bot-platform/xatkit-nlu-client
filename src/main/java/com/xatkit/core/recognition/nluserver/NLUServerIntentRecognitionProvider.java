package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.EventDefinitionRegistry;
import com.xatkit.core.XatkitException;
import com.xatkit.core.recognition.AbstractIntentRecognitionProvider;
import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.RecognitionMonitor;
import com.xatkit.core.recognition.nluserver.mapper.NLUServerEntityMapper;
import com.xatkit.core.recognition.nluserver.mapper.NLUServerEntityReferenceMapper;
import com.xatkit.core.recognition.nluserver.mapper.NLUServerIntentMapper;
import com.xatkit.core.recognition.nluserver.mapper.NLUServerStateMapper;
import com.xatkit.core.recognition.nluserver.mapper.NLUServerRecognizedIntentMapper;
import com.xatkit.core.recognition.nluserver.mapper.dsl.BotData;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Classification;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityParameter;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityType;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.execution.ExecutionFactory;
import com.xatkit.execution.State;
import com.xatkit.execution.StateContext;
import com.xatkit.intent.BaseEntityDefinition;
import com.xatkit.intent.CompositeEntityDefinition;
import com.xatkit.intent.CustomEntityDefinition;
import com.xatkit.intent.EntityDefinition;
import com.xatkit.intent.IntentDefinition;
import com.xatkit.intent.IntentFactory;
import com.xatkit.intent.RecognizedIntent;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;
import static java.util.Objects.nonNull;

/**
 * An {@link AbstractIntentRecognitionProvider} bound to the NLUServer API.
 * <p>
 * This class is used to easily setup a connection to Xatkit's own NLUServer implementation. The behavior of this
 * connector can be
 * customized in the Xatkit {@link Configuration}, see {@link NLUServerConfiguration} for more information on the
 * configuration options.
 */
public class NLUServerIntentRecognitionProvider extends AbstractIntentRecognitionProvider {

    /**
     * The {@link NLUServerConfiguration} extracted from the provided {@link Configuration}.
     */
    private final NLUServerConfiguration configuration;

    /**
     * The clients used to access the NLUServer API.
     */
    private NLUServerClientAPIWrapper nluServerClientWrapper;

    /**
     * Represents the bot project name.
     * <p>
     * This attribute is used to compute bot-level operations
     * @see #trainMLEngine()
     */
    private final String botName;

    private BotData bot;

    /**
     * The {@link RecognitionMonitor} used to track intent matching information.
     */
    @Nullable
    private final RecognitionMonitor recognitionMonitor;

    /**
     * The mapper creating a NLUServer {@link Intent}s from {@link IntentDefinition} instances.
     */
    private NLUServerIntentMapper nluServerIntentMapper;

    /**
     * The mapper creating NLUServer {@link EntityType}s from {@link EntityDefinition} instances.
     */
    private NLUServerEntityMapper nluServerEntityMapper;

    /**
     * The mapper creating NLUServer {@link EntityType}s from {@link EntityDefinition} instances.
     */
    private NLUServerStateMapper nluServerStateMapper;

    /**
     * The mapper creating NLUServer entity references from {@link EntityDefinition} references.
     * <p>
     * These references are typically used to refer to {@link EntityType}s in {@link Intent}'s training sentences.
     */
    private NLUServerEntityReferenceMapper nluServerEntityReferenceMapper;

    /**
     * The mapper creating {@link RecognizedIntent}s from {@link Prediction} instances returned by the NLUServer.
     */
    private NLUServerRecognizedIntentMapper nluServerRecognizedIntentMapper;

    /**
     * Constructs a {@link NLUServerIntentRecognitionProvider} with the provided {@code eventRegistry}, {@code
     * configuration}, and {@code
     * recognitionMonitor}.
     * <p>
     * The behavior of this class can be customized in the provided {@code configuration}. See
     * {@link NLUServerConfiguration} for more information on the configuration options.
     *
     * @param eventRegistry      the {@link EventDefinitionRegistry} containing the events defined in the current bot
     * @param configuration      the {@link Configuration} holding the NLUServer project ID and language code
     * @param recognitionMonitor the {@link RecognitionMonitor} instance storing intent matching information
     * @throws NullPointerException if the provided {@code eventRegistry}, {@code configuration} or one of the mandatory
     *                              {@code configuration} value is {@code null}.
     * @throws XatkitException      if an internal error occurred while creating the NLUServer connector
     * @see NLUServerConfiguration
     */
    public NLUServerIntentRecognitionProvider(@NonNull EventDefinitionRegistry eventRegistry,
                                              @NonNull Configuration configuration,
                                              @Nullable RecognitionMonitor recognitionMonitor) {
        Log.info("Starting Xatkit's NLU Server connector");
        this.configuration = new NLUServerConfiguration(configuration);
        this.botName = this.configuration.getBotName();
        this.bot = new BotData(this.botName);
        try {
            this.nluServerClientWrapper = new NLUServerClientAPIWrapper(this.configuration, this.bot);
        } catch (IntentRecognitionProviderException e) {
            throw new XatkitException("An error occurred when creating the NLU Server client, see attached "
                    + "exception", e);
        }
        this.nluServerStateMapper = new NLUServerStateMapper(this.configuration);
        this.nluServerEntityReferenceMapper = new NLUServerEntityReferenceMapper();
        this.nluServerIntentMapper = new NLUServerIntentMapper(this.configuration,
                this.nluServerEntityReferenceMapper);
        this.nluServerEntityMapper = new NLUServerEntityMapper(this.nluServerEntityReferenceMapper);
        this.nluServerRecognizedIntentMapper = new NLUServerRecognizedIntentMapper(this.configuration, eventRegistry);
        this.recognitionMonitor = recognitionMonitor;

    }

    /**
     * {@inheritDoc}
     * <p>
     * This method reuses the information contained in the provided {@link IntentDefinition} to create a new
     * NLUServer {@link Intent} and add it to the current project.
     *
     * @param intentDefinition the {@link IntentDefinition} to register to the NLUServer project
     * @throws NullPointerException if the provided {@code intentDefinition} is {@code null}
     * @see NLUServerIntentMapper
     */
    @Override
    public void registerIntentDefinition(@NonNull IntentDefinition intentDefinition)
            throws IntentRecognitionProviderException {
        checkNotNull(intentDefinition.getName(), "Cannot register the %s with the provided name %s",
                IntentDefinition.class.getSimpleName());
        if (this.bot.containsIntent(intentDefinition.getName())) {
            throw new IntentRecognitionProviderException(MessageFormat.format("Intent {0} already exists in the agent"
                    + " and will not be updated", intentDefinition.getName()));
        }
        Log.debug("Registering NLUServer intent {0}", intentDefinition.getName());
        Intent intent = nluServerIntentMapper.mapIntentDefinition(intentDefinition);
        this.bot.addIntent(intent);

    }


    /**
     *  {@inheritDoc}
     * <p>
     * This method reuses the information contained in the provided {@link State} to create a new
     *  {@link } and add it to the current project.
     *
     * @param state the {@link State} to register to the NLUServer project
     * @throws NullPointerException if the provided {@code intentDefinition} is {@code null}
     * @see NLUServerIntentMapper
     */
    @Override
    public void registerState(@NonNull State state) throws IntentRecognitionProviderException {
        checkNotNull(state.getName(), "Cannot register the %s with the provided name %s",
                State.class.getSimpleName());
        if (this.bot.containsNLUContext(state.getName())) {
            throw new IntentRecognitionProviderException(MessageFormat.format("State {0} already exists in the agent"
                    + " and will not be updated", state.getName()));
        }
        if (state.getAllAccessedIntents().isEmpty()) {
            throw new IntentRecognitionProviderException(MessageFormat.format("State {0} has no outgoing intents, it "
                    + "won't be registered", state.getName()));
        }
        Log.debug("Registering NLUServer state {0}", state.getName());
        NLUContext nluContext = nluServerStateMapper.mapStateDefinition(state);
        this.bot.addNLUContext(nluContext);

    }


    /**
     * {@inheritDoc}
     * <p>
     * This method reuses the information contained in the provided {@link EntityDefinition} to create a new
     * NLUServer {@link EntityType} and add it to the current project.
     *
     * @param entityDefinition the {@link EntityDefinition} to register to the NLUServer project
     * @throws NullPointerException if the provided {@code entityDefinition} is {@code null}
     */
    public void registerEntityDefinition(@NonNull EntityDefinition entityDefinition)
            throws IntentRecognitionProviderException {
        if (entityDefinition instanceof BaseEntityDefinition) {
            Log.debug("Registering NLU server {0} {1}", BaseEntityDefinition.class.getSimpleName(),
                    entityDefinition.getName());
            EntityType entityType = nluServerEntityMapper.mapEntityDefinition(entityDefinition);
            this.bot.addEntityType(entityType);
        } else if (entityDefinition instanceof CustomEntityDefinition) {
            Log.debug("Registering NLU server {0} {1}", CustomEntityDefinition.class.getSimpleName(),
                    entityDefinition.getName());
            EntityType entityType = nluServerEntityMapper.mapEntityDefinition(entityDefinition);
            this.bot.addEntityType(entityType);

        }
    }


    /**
     * Registers the {@link EntityDefinition}s referred by the provided {@code compositeEntityDefinition}.
     * <p>
     * Note that this method only registers {@link CustomEntityDefinition}s referred from the provided {@code
     * compositeEntityDefinition}. {@link BaseEntityDefinition}s are already registered since they are part of the
     * platform.
     *
     * @param compositeEntityDefinition the {@link CompositeEntityDefinition} to register the referred
     *                                  {@link EntityDefinition}s of
     * @throws NullPointerException if the provided {@code compositeEntityDefinition} is {@code null}
     * @see #registerEntityDefinition(EntityDefinition)
     */
    private void registerReferencedEntityDefinitions(@NonNull CompositeEntityDefinition compositeEntityDefinition) {
        /*
        for (CompositeEntityDefinitionEntry entry : compositeEntityDefinition.getEntries()) {
            for (EntityDefinition referredEntityDefinition : entry.getEntities()) {
                if (referredEntityDefinition instanceof CustomEntityDefinition) {
                    // Only register CustomEntityDefinitions, the other ones are already part of the system.

                    try {
                        this.registerEntityDefinition(referredEntityDefinition);
                    } catch (IntentRecognitionProviderException e) {
                        // Simply log a warning here, the entity may have been registered before.
                        Log.warn(e.getMessage());
                    }
                }
            }
        }*/
    }

    @Override
    public void deleteEntityDefinition(@NonNull EntityDefinition entityDefinition) throws IntentRecognitionProviderException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteIntentDefinition(@NonNull IntentDefinition intentDefinition) throws IntentRecognitionProviderException {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method checks every second whether the underlying ML Engine has finished its training. Note that this
     * method is blocking as long as the ML Engine training is not terminated, and may not terminate if an issue
     * occurred on the NLUServer side.
     */
    @Override
    public void trainMLEngine() throws IntentRecognitionProviderException {
        checkNotShutdown();
        prepareTrainingData();
        Log.info("Starting NLUServer agent training (this may take a few minutes)");
        boolean isDone = false;
        try {
            isDone = this.nluServerClientWrapper.deployAndTrainBot();
        } catch (RuntimeException e) {
            throw new IntentRecognitionProviderException("An error occurred during the NLUServer agent training", e);
        }
        if (!isDone) {
            throw new IntentRecognitionProviderException("Failed to deploy and train the NLUServer agent");
        }
        Log.info("NLUServer agent trained, intent matching will be available in a few seconds");
    }

    /**
     * We link the states ({@link NLUContext}) with the {@link Intent}s accessible from them based on the previous
     * registered names
     *
     * We also direclty reference the EntityTypes from the context (as needed by the NLUServer)
     * and
     */
    private void prepareTrainingData() {
        for (NLUContext c: bot.getNluContexts()) {
            for (String i: c.getIntentNames()) {
                c.addIntent(bot.getIntent(i));
            }
            for (Intent i: c.getIntents()) {
                for (EntityParameter p: i.getParameters()) {
                    p.setType(bot.getEntityType(p.getTypeName()));
                    c.addEntityType(p.getType());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException               if the provided {@code input} or {@code context} is {@code null}
     * @throws IntentRecognitionProviderException if an error occurred when accessing the intent provider
     */
    @Override
    protected RecognizedIntent getIntentInternal(@NonNull String input, @NonNull StateContext context)
            throws IntentRecognitionProviderException {
        checkNotShutdown();
        checkArgument(!input.isEmpty(), "Cannot retrieve the intent from empty string");

        try {
            RecognizedIntent recognizedIntent;

            //We assume the nluContexts and states have the same name, @see NLUServerStateMapper
            Prediction prediction = this.nluServerClientWrapper.predict(bot.getNluContext(context.getState().getName()),
                    input);

            if (prediction.isEmpty()) {
                recognizedIntent = IntentFactory.eINSTANCE.createRecognizedIntent();
                recognizedIntent.setDefinition(DEFAULT_FALLBACK_INTENT);
                recognizedIntent.setRecognitionConfidence(0);
                recognizedIntent.setMatchedInput(input);
            } else if ( prediction.getTopClassification().getScore() < configuration.getConfidenceThreshold()) {
                Classification topClassification = prediction.getTopClassification();
                recognizedIntent = IntentFactory.eINSTANCE.createRecognizedIntent();
                recognizedIntent.setDefinition(DEFAULT_FALLBACK_INTENT);
                recognizedIntent.setRecognitionConfidence(topClassification.getScore());
                recognizedIntent.setMatchedInput(topClassification.getMatchedUtterance());
            } else {
                List<RecognizedIntent> recognizedIntents =
                        nluServerRecognizedIntentMapper.mapRecognitionResult(prediction);
                recognizedIntent = getBestCandidate(recognizedIntents, context);
            }

            if (nonNull(recognitionMonitor)) {
                recognitionMonitor.logRecognizedIntent(context, recognizedIntent);
            }
            return recognizedIntent;
        } catch (Exception e) {
            throw new IntentRecognitionProviderException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() throws IntentRecognitionProviderException {
        checkNotShutdown();
        if (nonNull(this.recognitionMonitor)) {
            this.recognitionMonitor.shutdown();
        }
        this.nluServerClientWrapper.shutdown();
    }

    /**
     * Throws a {@link IntentRecognitionProviderException} if the provided
     * {@link NLUServerIntentRecognitionProvider} is shutdown.
     * <p>
     * This method is typically called in methods that need to interact with the NLUServer API, and cannot complete
     * if the connector is shutdown.
     *
     * @throws IntentRecognitionProviderException if the provided {@code NLUServerIntentRecognitionProvider} is
     *                                            shutdown
     * @throws NullPointerException               if the provided {@code NLUServerIntentRecognitionProvider} is
     *                                            {@code null}
     */
    private void checkNotShutdown() throws IntentRecognitionProviderException {
        if (this.isShutdown()) {
            throw new IntentRecognitionProviderException("Cannot perform the operation, the NLUServer is "
                    + "shutdown");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public RecognitionMonitor getRecognitionMonitor() {
        return recognitionMonitor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return this.nluServerClientWrapper.isShutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StateContext createContext(@NonNull String sessionId) {
        /*
         * FIXME duplicated code from RegExIntentRecognitionProvider
         */
        StateContext stateContext = ExecutionFactory.eINSTANCE.createStateContext();
        stateContext.setContextId(sessionId);
        stateContext.setConfiguration(ConfigurationConverter.getMap(configuration.getBaseConfiguration()));
        return stateContext;
    }
}
