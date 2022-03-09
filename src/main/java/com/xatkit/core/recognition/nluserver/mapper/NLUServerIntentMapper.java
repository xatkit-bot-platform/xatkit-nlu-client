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
     * The {@link NLUServerEntityReferenceMapper} used to map accesses to {@link com.xatkit.intent.EntityDefinition}s.
     * <p>
     * These accesses exist in {@link IntentDefinition} that create a context, and set a parameter with an entity
     * (system, mapping, or composite).
     */
    private NLUServerEntityReferenceMapper nluServerEntityReferenceMapper;

    /**
     * Constructs a {@link NLUServerIntentMapper} with the provided {@code configuration} and {@code
     * dialogFlowEntityReferenceMapper}.
     *
     * @param configuration                   the {@link NLUServerConfiguration}
     * @param nluServerEntityReferenceMapper the {@link NLUServerEntityReferenceMapper} used to map accesses to
     *                                        {@link com.xatkit.intent.EntityDefinition}s
     * @throws NullPointerException if the provided {@code configuration} or {@code dialogFlowEntityReferenceMapper}
     *                              is {@code null}
     */
    public NLUServerIntentMapper(@NonNull NLUServerConfiguration configuration,
                                 @NonNull NLUServerEntityReferenceMapper nluServerEntityReferenceMapper) {
        this.configuration = configuration;
        this.nluServerEntityReferenceMapper = nluServerEntityReferenceMapper;
    }

    /**
     * Maps the provided {@link IntentDefinition} to a Xatkit NLUServer {@link Intent}.
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
        Intent nluIntent = new Intent(adaptIntentDefinitionNameToNLUServer(intentDefinition.getName()));

        nluIntent.addAllTrainingSentences(createTrainingPhrases(intentDefinition));

       /* List<String> inContextNames = createInContextNames(intentDefinition);
        builder.addAllInputContextNames(inContextNames);
        List<Context> outContexts = createOutContexts(intentDefinition);
        builder.addAllOutputContexts(outContexts);
        List<Intent.Parameter> parameters = createParameters(intentDefinition);
        builder.addAllParameters(parameters);
        */
        return nluIntent;
    }


    /**
     * Adapts the provided {@code intentDefinitionName} by replacing its {@code _} by spaces.
     * <p>
     *
     * @param name the {@link IntentDefinition} name to adapt
     * @return the adapted {@code intentDefinitionName}
     * @throws NullPointerException if the provided {@code name} is {@code null}
     */
    private String adaptIntentDefinitionNameToNLUServer(@NonNull String name) {
        return name.replaceAll("_", " ");
    }

    /**
     * Creates the {@link com.google.cloud.dialogflow.v2.Intent.TrainingPhrase}s for the provided {@code
     * intentDefinition}.
     *
     * @param intentDefinition the {@link IntentDefinition} to create the
     *                         {@link String} Training Phrases from
     * @return the created {@link String} Training Phrases
     * @throws NullPointerException if the provided {@code intentDefinition} is {@code null}
     */
    private List<String> createTrainingPhrases(@NonNull IntentDefinition intentDefinition) {
        List<String> trainingPhrases = new ArrayList<>();
        for (String trainingSentence : intentDefinition.getTrainingSentences()) {
            trainingPhrases.add(trainingSentence); // For now we stick to the version without parameters
           // trainingPhrases.add(createTrainingPhrase(trainingSentence, intentDefinition.getParameters()));
        }
        return trainingPhrases;
    }

    /**
     * Creates a single {@link String} TrainingPhrase from the provided {@code
     * trainingSentence} and {@code outContexts}.
     * <p>
     * This method looks for {@link com.xatkit.intent.EntityDefinition} accesses in the provided {@code
     * trainingSentence} and checks them against the provided {@code outContexts} (by checking the context
     * parameter's text fragment). These {@link com.xatkit.intent.EntityDefinition} accesses are then translated into
     * references using the {@link NLUServerEntityReferenceMapper}.
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
    /*private Intent.TrainingPhrase createTrainingPhrase(@NonNull String trainingSentence,
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

            String preparedTrainingSentence = trainingSentence;
            for (ContextParameter parameter : parameters) {
                for (String textFragment : parameter.getTextFragments()) {
                    if (preparedTrainingSentence.contains(textFragment)) {
                        preparedTrainingSentence = preparedTrainingSentence.replace(textFragment, "#"
                                + textFragment + "#");
                    }
                }
            }
            // Process the pre-processed String and bind its entities.
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
    */

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
    /* TODO
    private List<Intent.Parameter> createParameters(@NonNull IntentDefinition intentDefinition) {
        List<Intent.Parameter> results = new ArrayList<>();
        for (ContextParameter contextParameter : intentDefinition.getParameters()) {
            checkNotNull(contextParameter.getName(), "Cannot create the %s from the provided %s %s, the name %s is "
                    + "invalid", Intent.Parameter.class.getSimpleName(), ContextParameter.class.getSimpleName(),
                    contextParameter, contextParameter.getName());
            String dialogFlowEntity =
                    dialogFlowEntityReferenceMapper.getMappingFor(contextParameter.getEntity().getReferredEntity());
            // DialogFlow parameters are prefixed with a '$'.
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

                Log.warn("Parameter {0} is defined multiple times", parameter.getDisplayName());
            } else {
                results.add(parameter);
            }
        }
//        }
        return results;
    } */
}
