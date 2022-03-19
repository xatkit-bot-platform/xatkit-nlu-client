package com.xatkit.core.recognition.nluserver.mapper;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.intent.EventDefinition;
import com.xatkit.intent.IntentDefinition;
import com.xatkit.execution.State;
import lombok.NonNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;

/**
 * Maps {@link IntentDefinition} instances to DialogFlow {@link Intent}s.
 * <p>
 * This class is used to translate generic {@link IntentDefinition}s to platform-specific construct representing
 * DialogFlow intents.
 */
public class NLUServerStateMapper {

    /**
     * The {@link NLUServerConfiguration}.
     * <p>
     * This configuration is used to retrieve the DialogFlow project ID, and use it to generate the {@link Intent} name.
     */
    private final NLUServerConfiguration configuration;

    /**
     * Constructs a {@link NLUServerStateMapper} with the provided {@code configuration} and {@code
     * dialogFlowEntityReferenceMapper}.
     *
     * @param configuration                   the {@link NLUServerConfiguration}
     * @throws NullPointerException if the provided {@code configuration} is {@code null}
     */
    public NLUServerStateMapper(@NonNull NLUServerConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Maps the provided {@link State} to a Xatkit NLUServer {@link NLUContext}.
     * <p>
     *
     * @param state the {@link State} to map
     * @return the created {@link }
     * @throws NullPointerException               if the provided {@code state} is {@code null}
     * @throws IntentRecognitionProviderException if the mapper could not create an {@link NLUContext} from the provided
     *                                            {@code state}
     */
    public NLUContext mapStateDefinition(@NonNull State state)
            throws IntentRecognitionProviderException {
        checkNotNull(state.getName(), "Cannot map the %s with the provided name %s",
                State.class.getSimpleName(), state.getName());
        NLUContext nluContext = new NLUContext(adaptStateDefinitionNameToNLUServer(state.getName()));

        nluContext.addIntentNames(getLinkedIntentNames(state.getAllAccessedIntents()));
        return nluContext;
    }


    /**
     * Adapts the provided {@code intentDefinitionName} by replacing its {@code _} by spaces.
     * <p>
     *
     * @param name the {@link State} name to adapt
     * @return the adapted {@code intentDefinitionName}
     * @throws NullPointerException if the provided {@code name} is {@code null}
     */
    private String adaptStateDefinitionNameToNLUServer(@NonNull String name) {
        return name.replaceAll("_", " ");
    }

    /**
     * Registers the names of the intents that could be matched from this State to later transform them into actual the
     * {@link Intent} references.
     * @see NLUServerIntentMapper
     * @param linkedIntents the {@link IntentDefinition} accessible from the {@link State}
     */
    private List<String> getLinkedIntentNames(@NonNull Collection<IntentDefinition> linkedIntents) {
       return (linkedIntents.stream().map(EventDefinition::getName).collect(Collectors.toList()));
    }


}
