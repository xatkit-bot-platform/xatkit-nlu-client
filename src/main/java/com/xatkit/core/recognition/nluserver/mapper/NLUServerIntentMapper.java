package com.xatkit.core.recognition.nluserver.mapper;


import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.intent.ContextParameter;
import com.xatkit.intent.IntentDefinition;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;

import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;

/**
 * Maps {@link IntentDefinition} instances to DialogFlow {@link Intent}s.
 * <p>
 * This class is used to translate generic {@link IntentDefinition}s to platform-specific construct representing
 * DialogFlow intents.
 */
public class NLUServerIntentMapper {

    /**
     * The {@link NLUServerConfiguration}.
     * <p>
     * This configuration is used to retrieve the DialogFlow project ID, and use it to generate the {@link Intent} name.
     */
    private NLUServerConfiguration configuration;

    /**
     * The {@link DialogFlowEntityReferenceMapper} used to map accesses to {@link com.xatkit.intent.EntityDefinition}s.
     * <p>
     * These accesses exist in {@link IntentDefinition} that create a context, and set a parameter with an entity
     * (system, mapping, or composite).
     */
    private DialogFlowEntityReferenceMapper dialogFlowEntityReferenceMapper;

    /**
     * Constructs a {@link NLUServerIntentMapper} with the provided {@code configuration} and {@code
     * dialogFlowEntityReferenceMapper}.
     *
     * @param configuration                   the {@link DialogFlowConfiguration}
     * @param dialogFlowEntityReferenceMapper the {@link DialogFlowEntityReferenceMapper} used to map accesses to
     *                                        {@link com.xatkit.intent.EntityDefinition}s
     * @throws NullPointerException if the provided {@code configuration} or {@code dialogFlowEntityReferenceMapper}
     *                              is {@code null}
     */
    public NLUServerIntentMapper(@NonNull DialogFlowConfiguration configuration,
                                 @NonNull DialogFlowEntityReferenceMapper dialogFlowEntityReferenceMapper) {
        this.configuration = configuration;
        this.dialogFlowEntityReferenceMapper = dialogFlowEntityReferenceMapper;
    }

    /**
     * Maps the provided {@link IntentDefinition} to a DialogFlow {@link Intent}.
     * <p>
     * This method sets the name of the created intent, its training sentences, and the context(s) associated to its
     * parameters. Note that this method does not check whether access {@link com.xatkit.intent.EntityDefinition}s
     * actually exist in the DialogFlow agent.
     *
     * @param intentDefinition the {@link IntentDefinition} to map
     * @return the created {@link Intent}
     * @throws NullPointerException               if the provided {@code intentDefinition} is {@code null}
     * @throws IntentRecognitionProviderException if the mapper could not create an {@link Intent} from the provided
     *                                            {@code intentDefinition}
     */
    public Intent mapIntentDefinition(@NonNull IntentDefinition intentDefinition)
            throws IntentRecognitionProviderException {
        checkNotNull(intentDefinition.getName(), "Cannot map the %s with the provided name %s",
                IntentDefinition.class.getSimpleName(), intentDefinition.getName());
        Intent.Builder builder = Intent.newBuilder()
                .setDisplayName(adaptIntentDefinitionNameToDialogFlow(intentDefinition.getName()));
        List<Intent.TrainingPhrase> trainingPhrases = createTrainingPhrases(intentDefinition);
        builder.addAllTrainingPhrases(trainingPhrases);
        List<String> inContextNames = createInContextNames(intentDefinition);
        builder.addAllInputContextNames(inContextNames);
        List<Context> outContexts = createOutContexts(intentDefinition);
        builder.addAllOutputContexts(outContexts);
        List<Intent.Parameter> parameters = createParameters(intentDefinition);
        builder.addAllParameters(parameters);
        /*
         * We need to set an empty list for messages.
         */
        builder.addAllMessages(new ArrayList<>());
        return builder.build();
    }


    /**
     * Adapts the provided {@code intentDefinitionName} by replacing its {@code _} by spaces.
     * <p>
     *
     * @param name the {@link IntentDefinition} name to adapt
     * @return the adapted {@code intentDefinitionName}
     * @throws NullPointerException if the provided {@code name} is {@code null}
     */
    private String adaptIntentDefinitionNameToDialogFlow(@NonNull String name) {
        return name.replaceAll("_", " ");
    }

    /**
     * Creates the {@link com.google.cloud.dialogflow.v2.Intent.TrainingPhrase}s for the provided {@code
     * intentDefinition}.
     *
     * @param intentDefinition the {@link IntentDefinition} to create the
     *                         {@link com.google.cloud.dialogflow.v2.Intent.TrainingPhrase}s from
     * @return the created {@link com.google.cloud.dialogflow.v2.Intent.TrainingPhrase}s
     * @throws NullPointerException if the provided {@code intentDefinition} is {@code null}
     */
    private List<Intent.TrainingPhrase> createTrainingPhrases(@NonNull IntentDefinition intentDefinition) {
        List<Intent.TrainingPhrase> trainingPhrases = new ArrayList<>();
        for (String trainingSentence : intentDefinition.getTrainingSentences()) {
            trainingPhrases.add(createTrainingPhrase(trainingSentence, intentDefinition.getParameters()));
        }
        return trainingPhrases;
    }

    /**
     * Creates a single {@link com.google.cloud.dialogflow.v2.Intent.TrainingPhrase} from the provided {@code
     * trainingSentence} and {@code outContexts}.
     * <p>
     * This method looks for {@link com.xatkit.intent.EntityDefinition} accesses in the provided {@code
     * trainingSentence} and checks them against the provided {@code outContexts} (by checking the context
     * parameter's text fragment). These {@link com.xatkit.intent.EntityDefinition} accesses are then translated into
     * references using the {@link DialogFlowEntityReferenceMapper}.
     *
     * @param trainingSentence the {@link IntentDefinition}'s training sentence to create a
     *                         {@link com.google.cloud.dialogflow.v2.Intent.TrainingPhrase} from
     * @param parameters       the {@link ContextParameter} containing the entities referenced in the {@code
     *                         trainingSentence}
     * @return the created DialogFlow's {@link com.google.cloud.dialogflow.v2.Intent.TrainingPhrase}
     * @throws NullPointerException if the provided {@code trainingSentence} or {@code outContexts} {@link List} is
     *                              {@code null}, or if one of the {@link ContextParameter}'s name from the provided
     *                              {@code outContexts} is {@code null}
     * @see DialogFlowEntityReferenceMapper
     */
    private Intent.TrainingPhrase createTrainingPhrase(@NonNull String trainingSentence,
                                                       @NonNull List<ContextParameter> parameters) {
        if (parameters.isEmpty()) {
            return Intent.TrainingPhrase.newBuilder().addParts(Intent.TrainingPhrase.Part.newBuilder().setText(
                    trainingSentence).build()).build();
        } else {
            /*
             * First mark all the context parameter literals with #<literal>#. This pre-processing allows to easily
             * split the training sentence into TrainingPhrase parts, that are bound to their concrete entity when
             * needed, and sent to the DialogFlow API.
             * We use this two-step process for simplicity. If the performance of TrainingPhrase creation become an
             * issue we can reshape this method to avoid this pre-processing phase.
             */
            String preparedTrainingSentence = trainingSentence;
            for (ContextParameter parameter : parameters) {
                for (String textFragment : parameter.getTextFragments()) {
                    if (preparedTrainingSentence.contains(textFragment)) {
                        preparedTrainingSentence = preparedTrainingSentence.replace(textFragment, "#"
                                + textFragment + "#");
                    }
                }
            }
            /*
             * Process the pre-processed String and bind its entities.
             */
            String[] splitTrainingSentence = preparedTrainingSentence.split("#");
            Intent.TrainingPhrase.Builder trainingPhraseBuilder = Intent.TrainingPhrase.newBuilder();
            for (int i = 0; i < splitTrainingSentence.length; i++) {
                String sentencePart = splitTrainingSentence[i];
                Intent.TrainingPhrase.Part.Builder partBuilder = Intent.TrainingPhrase.Part.newBuilder().setText(
                        sentencePart);
                for (ContextParameter parameter : parameters) {
                    if (parameter.getTextFragments().contains(sentencePart)) {
                        checkNotNull(parameter.getName(), "Cannot build the training sentence \"%s\", the parameter "
                                        + "for the fragment \"%s\" does not define a name", trainingSentence,
                                sentencePart);
                        checkNotNull(parameter.getEntity(), "Cannot build the training sentence \"%s\", the parameter"
                                        + " for the fragment \"%s\" does not define an entity", trainingSentence,
                                sentencePart);
                        String dialogFlowEntity =
                                dialogFlowEntityReferenceMapper.getMappingFor(parameter.getEntity()
                                        .getReferredEntity());
                        partBuilder.setEntityType(dialogFlowEntity).setAlias(parameter.getName());
                    }
                }
                trainingPhraseBuilder.addParts(partBuilder.build());
            }
            return trainingPhraseBuilder.build();
        }
    }

    /**
     * Creates the DialogFlow input {@link Context} names for the provided {@code intentDefinition}.
     * <p>
     * This method creates an input {@link Context} for every {@link IntentDefinition}. This means that these intents
     * can be matched iff the input context is set in the DialogFlow session.
     * <p>
     * This method returns an empty {@link List} if the provided {@code intentDefinition} is a top-level intent.
     *
     * @param intentDefinition the {@link IntentDefinition} to create the DialogFlow input {@link Context}s from
     * @return the created {@link List} of DialogFlow {@link Context} identifiers
     * @throws NullPointerException if the provided {@code intentDefinition} is {@code null}
     */
    private List<String> createInContextNames(@NonNull IntentDefinition intentDefinition) {
        List<String> results = new ArrayList<>();
        ContextName contextName = ContextName.of(this.configuration.getProjectId(),
                SessionName.of(this.configuration.getProjectId(), "setup").getSession(),
                "Enable" + intentDefinition.getName());
        results.add(contextName.toString());
        return results;
    }

    /**
     * Creates the DialogFlow output {@link Context}s from the provided {@code intentDefinition}.
     *
     * @param intentDefinition the {@link IntentDefinition} to create the DialogFlow output {@link Context}s from
     * @return the created {@link List} of DialogFlow {@link Context}s
     * @throws NullPointerException               if the provided {@code intentDefinition} is {@code null}
     * @throws IntentRecognitionProviderException if there is no training sentence containing a provided {@code
     *                                            intentDefinition}'s parameter fragment
     */
    private List<Context> createOutContexts(@NonNull IntentDefinition intentDefinition)
            throws IntentRecognitionProviderException {
        NLUServerCheckingUtils.checkParameters(intentDefinition);
        ContextName contextName = ContextName.of(this.configuration.getProjectId(),
                SessionName.of(this.configuration.getProjectId(), "setup").getSession(), "Xatkit");
        // TODO check if 2 is fine here
        Context dialogFlowContext = Context.newBuilder().setName(contextName.toString()).setLifespanCount(2).build();
        // TODO should we return a single context if it's always a singleton list?
        return Collections.singletonList(dialogFlowContext);
    }

    /**
     * Creates the DialogFlow context parameters from the provided Xatkit {@code intentDefinition}.
     * <p>
     * Note that this method does not check whether the referred entities are deployed in the DialogFlow agent.
     *
     * @param intentDefinition the {@link IntentDefinition} to create the parameters from
     * @return the {@link List} of DialogFlow context parameters
     * @throws NullPointerException if the provided {@code contexts} {@link List} is {@code null}, or if one of the
     *                              provided {@link ContextParameter}'s name is {@code null}
     */
    private List<Intent.Parameter> createParameters(@NonNull IntentDefinition intentDefinition) {
        List<Intent.Parameter> results = new ArrayList<>();
        for (ContextParameter contextParameter : intentDefinition.getParameters()) {
            checkNotNull(contextParameter.getName(), "Cannot create the %s from the provided %s %s, the name %s is "
                    + "invalid", Intent.Parameter.class.getSimpleName(), ContextParameter.class.getSimpleName(),
                    contextParameter, contextParameter.getName());
            String dialogFlowEntity =
                    dialogFlowEntityReferenceMapper.getMappingFor(contextParameter.getEntity().getReferredEntity());
            /*
             * DialogFlow parameters are prefixed with a '$'.
             */
            Intent.Parameter parameter = Intent.Parameter.newBuilder().setDisplayName(contextParameter.getName())
                    .setEntityTypeDisplayName(dialogFlowEntity).setValue("$" + contextParameter.getName()).build();
            Optional<Intent.Parameter> parameterAlreadyRegistered =
                    results.stream().filter(r -> r.getDisplayName().equals(parameter.getDisplayName())).findAny();
            if (parameterAlreadyRegistered.isPresent()) {
                /*
                 * Don't register the parameter if it has been added to the list, this means that we have a
                 * parameter initialized with different fragments, and this is already handled when constructing
                 * the training sentence.
                 * If the parameter is added the agent seems to work fine, but there is an error message
                 * "Parameter name must be unique within the action" in the corresponding intent page.
                 */
                Log.warn("Parameter {0} is defined multiple times", parameter.getDisplayName());
            } else {
                results.add(parameter);
            }
        }
//        }
        return results;
    }
}
