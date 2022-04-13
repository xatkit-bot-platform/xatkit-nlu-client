package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityParameter;
import com.xatkit.intent.ContextParameter;
import com.xatkit.intent.IntentDefinition;
import lombok.NonNull;

import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;

import java.util.ArrayList;
import java.util.List;

import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;

/**
 * Maps {@link IntentDefinition} instances to Xatkit's NLU Server {@link Intent}s.
 * <p>
 * This class is used to translate generic {@link IntentDefinition}s to platform-specific construct representing
 * NLUServer intents.
 */
public class NLUServerIntentMapper {

    /**
     * The {@link NLUServerConfiguration}.
     * <p>
     * This configuration is used to retrieve the NLUServer bot project configuration
     */
    private final NLUServerConfiguration configuration;

    /**
     * The {@link NLUServerEntityReferenceMapper} used to map accesses to {@link com.xatkit.intent.EntityDefinition}s.
     * <p>
     * These accesses exist in {@link IntentDefinition} that create a context, and set a parameter with an entity
     * (system, mapping, or composite).
     */
    private NLUServerEntityReferenceMapper nluServerEntityReferenceMapper;

    /**
     * Constructs a {@link NLUServerIntentMapper} with the provided {@code configuration} and {@code
     * nluServerEntityReferenceMapper}.
     *
     * @param configuration                   the {@link NLUServerConfiguration}
     * @param nluServerEntityReferenceMapper the {@link NLUServerEntityReferenceMapper} used to map accesses to
     *                                        {@link com.xatkit.intent.EntityDefinition}s
     * @throws NullPointerException if the provided {@code configuration} or {@code nluServerEntityReferenceMapper}
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
     * actually exist in the NLUServer client.
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

        nluIntent.setTrainingSentences(createTrainingPhrases(intentDefinition));

        List<EntityParameter> parameters = createParameters(intentDefinition);
        nluIntent.addAllParameters(parameters);

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
        //return name.replaceAll("_", " ");
        return name; // for now we didn't detect any need for name reformatting
    }

    /**
     * Creates the training phrases for the provided {@code
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
            trainingPhrases.add(trainingSentence); // Even if the training sentence has parameters, we don't care
            // about them and just use the raw training sentence (processing of the entities referenced in the
            // sentence takes place on the server side)
        }
        return trainingPhrases;
    }



    /**
     * Creates the {@link Intent} {@link EntityParameter}s  from the provided Xatkit {@code intentDefinition}.
     * <p>
     * @param intentDefinition the {@link IntentDefinition} to create the parameters from
     * @return the {@link List} of DialogFlow context parameters
     * @throws NullPointerException if the provided {@code contexts} {@link List} is {@code null}, or if one of the
     *                              provided {@link ContextParameter}'s name is {@code null}
     */
    private List<EntityParameter> createParameters(@NonNull IntentDefinition intentDefinition) {
        List<EntityParameter> results = new ArrayList<>();
        for (ContextParameter contextParameter : intentDefinition.getParameters()) {
            checkNotNull(contextParameter.getName(), "Cannot create the %s from the provided %s %s, the name %s is "
                            + "invalid", EntityParameter.class.getSimpleName(), ContextParameter.class.getSimpleName(),
                    contextParameter, contextParameter.getName());
            String entityTypeName =
                    nluServerEntityReferenceMapper.getMappingFor(contextParameter.getEntity().getReferredEntity());

            EntityParameter parameter =
                    new EntityParameter(contextParameter.getName(), contextParameter.getTextFragments().get(0),
                            entityTypeName);
            results.add(parameter);
        }

        return results;
    }
}
