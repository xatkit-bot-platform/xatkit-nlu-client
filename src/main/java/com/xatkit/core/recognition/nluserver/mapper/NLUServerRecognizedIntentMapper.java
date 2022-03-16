package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.EventDefinitionRegistry;
import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Classification;
import com.xatkit.intent.BaseEntityDefinition;
import com.xatkit.intent.ContextParameter;
import com.xatkit.intent.ContextParameterValue;
import com.xatkit.intent.IntentDefinition;
import com.xatkit.intent.IntentFactory;
import com.xatkit.intent.RecognizedIntent;
import fr.inria.atlanmod.commons.log.Log;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xatkit.core.recognition.IntentRecognitionProvider.DEFAULT_FALLBACK_INTENT;
import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
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


    /**
     * Build a context parameter value from the provided protobuf {@link Value}.
     * <p>
     * The returned value is assignable to {@link ContextParameterValue#setValue(Object)}, and is either a
     * {@link String}, or a {@link Map} for nested struct {@link Value}s.
     *
     * @param fromValue the protobuf {@link Value} to translate
     * @return the context parameter value
     * @throws NullPointerException if the provided {@code fromValue} is {@code null}
     */

    /* TOOD
    private Object buildParameterValue(@NonNull Value fromValue) {
        if (fromValue.getKindCase().equals(Value.KindCase.STRUCT_VALUE)) {
            Map<String, Object> parameterMap = new HashMap<>();
            fromValue.getStructValue().getFieldsMap().forEach((key, value) -> {
                if (!key.contains(".original")) {
                    Object adaptedValue = buildParameterValue(value);
                    parameterMap.put(key, adaptedValue);
                }
            });
            return parameterMap;
        } else {
            return convertParameterValueToString(fromValue);
        }
    }
    */

    /**
     * Converts the provided {@code value} into a {@link String}.
     * <p>
     * This method converts protobuf's {@link Value}s returned by DialogFlow into {@link String}s that can be
     * assigned to {@link ContextParameterValue}s.
     *
     * @param value the protobuf {@link Value} to convert
     * @return the {@link String} representation of the provided {@code value}.
     * @throws NullPointerException if the provided {@code value} is {@code null}
     */
    /*
    protected String convertParameterValueToString(@NonNull Value value) {
        switch (value.getKindCase()) {
            case STRING_VALUE:
                return value.getStringValue();
            case NUMBER_VALUE:
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("0.###", decimalFormatSymbols);
                decimalFormat.setGroupingUsed(false);
                return decimalFormat.format(value.getNumberValue());
            case BOOL_VALUE:
                return Boolean.toString(value.getBoolValue());
            case NULL_VALUE:
                return "null";
            default:
                // Includes LIST_VALUE and STRUCT_VALUE

                Log.error("Cannot convert the provided value {0}", value);
                return "";
        }
    }*/
}
