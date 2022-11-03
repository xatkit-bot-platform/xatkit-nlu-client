package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.EventDefinitionRegistry;
import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Classification;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.core.recognition.nluserver.mapper.dsl.MatchedParam;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import com.xatkit.intent.ContextParameter;
import com.xatkit.intent.ContextParameterValue;
import com.xatkit.intent.IntentDefinition;
import com.xatkit.intent.IntentFactory;
import com.xatkit.intent.RecognizedIntent;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xatkit.core.recognition.IntentRecognitionProvider.DEFAULT_FALLBACK_INTENT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Maps NLUServer {@link Prediction} to {@link RecognizedIntent}s.
 * <p>
 * This class allows to wrap {@link Prediction} instances into generic {@link RecognizedIntent} that can be
 * manipulated by the core components.
 */
public class NLUServerRecognizedIntentMapper {

    /**
     * The {@link NLUServerConfiguration}.
     * <p>
     * This configuration is used to check whether the created {@link RecognizedIntent} has a confidence level higher
     * than the set threshold. If not the returned {@link RecognizedIntent}'s definition is set to
     * {@link com.xatkit.core.recognition.IntentRecognitionProvider#DEFAULT_FALLBACK_INTENT}.
     */
    private NLUServerConfiguration configuration;

    /**
     * The Xatkit {@link EventDefinitionRegistry} used to retrieve the {@link RecognizedIntent}'s definitions.
     */
    private EventDefinitionRegistry eventRegistry;

    /**
     * Constructs a {@link NLUServerRecognizedIntentMapper} with the provided {@code configuration} and {@code eventRegistry}.
     *
     * @param configuration the {@link NLUServerConfiguration}
     * @param eventRegistry the {@link EventDefinitionRegistry} used to retrieve the {@link RecognizedIntent}'s
     *                      definitions
     * @throws NullPointerException if the provided {@code configuration} or {@code eventRegistry} is {@code null}
     */
    public NLUServerRecognizedIntentMapper(@NonNull NLUServerConfiguration configuration,
                                           @NonNull EventDefinitionRegistry eventRegistry) {
        this.configuration = configuration;
        this.eventRegistry = eventRegistry;
    }



    /**
     * Transforms the NLUServer {@code prediction} to {@link RecognizedIntent}s.
     * <p>
     * Xatkit's NLUServer returns a classification of the agent's intents for a given input. This method transform
     * each
     * classification entry to a {@link RecognizedIntent}, but does not select which one to keep and use in the bot's
     * state machine.
     *
     * @param prediction the NLUServer recognition result
     * @return the {@link RecognizedIntent}s corresponding to the provided {@code recognitionResult}
     * @throws NullPointerException if the provided {@code recognitionResult} is {@code null}
     */
    public List<RecognizedIntent> mapRecognitionResult(@NonNull Prediction prediction) {
        List<Classification> classifications = prediction.getClassifications();
        classifications =
                classifications.stream().filter(c -> c.getScore() > configuration.getConfidenceThreshold()).collect(Collectors.toList());
        List<RecognizedIntent> recognizedIntents = new ArrayList<>();
        for (Classification classification : classifications) {
            RecognizedIntent recognizedIntent = IntentFactory.eINSTANCE.createRecognizedIntent();
            IntentDefinition intentDefinition = convertNLUServerIntentToIntentDefinition(classification.getIntent());
            recognizedIntent.setDefinition(intentDefinition);
            recognizedIntent.setRecognitionConfidence(classification.getScore());
            recognizedIntent.setMatchedInput(classification.getMatchedUtterance());
            recognizedIntents.add(recognizedIntent);
        }
        return recognizedIntents;
    }



    /**
     * Converts the provided {@code matched params} to {@link ContextParameterValue}.
     * <p>
     * This method discards the ones that do not correspond to a {@link ContextParameter} of the provided {@code
     * intentDefinition}.
     * <p>
     * This method is not invoked when calling #mapRecognitionResult(Prediction)} because it is not
     * necessary to map the {@link ContextParameterValue}s of all the extracted {@link RecognizedIntent}s
     *
     * It's only call once the getBestCandiate has been executed and we have a top ntent
     * @param intentDefinition  the {@link IntentDefinition} containing the {@link ContextParameter}s to instantiate
     * @param matchedParams the {@link MatchedParam} instances returned by XatkitNLUServer
     * @return the created {@link ContextParameter}s
     * @throws NullPointerException if the provided {@code intentDefinition} or {@code extractedEntities} is {@code
     *                              null}
     */
    public List<ContextParameterValue> mapParameterValues(@NonNull IntentDefinition intentDefinition,
                                                          @NonNull List<MatchedParam> matchedParams) {
        List<ContextParameterValue> contextParameterValues = new ArrayList<>();

        for (MatchedParam matchedParam : matchedParams) {
            String paramName = matchedParam.getParamName();
            String paramValue = matchedParam.getValue();
            //We get the contextParameter object with our param name
            ContextParameter contextParameter = intentDefinition.getParameter(paramName);
            if (nonNull(contextParameter)) {
                //If the parameter is actually part of the intent definition
                ContextParameterValue contextParameterValue =
                        IntentFactory.eINSTANCE.createContextParameterValue();
                contextParameterValue.setContextParameter(contextParameter);
                contextParameterValue.setValue(paramValue);
                contextParameterValues.add(contextParameterValue);
            }
        }
        return contextParameterValues;
    }


    /**
     * Reifies the provided NLUServer {@code intent} into an Xatkit {@link IntentDefinition}.
     * <p>
     * This method looks in the {@link EventDefinitionRegistry} for an {@link IntentDefinition} associated to the
     * provided {@code intent}'s name and returns it. If there is no such {@link IntentDefinition} the
     * {@code DEFAULT_FALLBACK_INTENT} is returned.
     *
     * @param intent the DialogFlow {@link Intent} to retrieve the Xatkit {@link IntentDefinition} from
     * @return the {@link IntentDefinition} associated to the provided {@code intent}
     * @throws NullPointerException if the provided {@code intent} is {@code null}
     */
    private IntentDefinition convertNLUServerIntentToIntentDefinition(@NonNull Intent intent) {
        IntentDefinition result = eventRegistry.getIntentDefinition(intent.getName());
        if (isNull(result)) {
            Log.warn("Cannot retrieve the {0} with the provided name {1}, returning the Default Fallback Intent",
                    IntentDefinition.class.getSimpleName(), intent.getName());
            result = DEFAULT_FALLBACK_INTENT;
        }
        return result;
    }


}
