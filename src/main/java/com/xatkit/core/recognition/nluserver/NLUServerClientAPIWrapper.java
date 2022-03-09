package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.util.FileUtils;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import lombok.Value;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Contains the clients used to access the DialogFlow API.
 * <p>
 * This class is initialized with a {@link NLUServerConfiguration}.
 */
@Value
public class NLUServerClientAPIWrapper {

    /**
     * The client instance managing the interaction with the deployed NLU server.
     * <p>
     * This client is used to execute project-level operations, such as the training of the underlying DialogFlow's
     * agent.
     */
    private String serverURL;


    /**
     * Initializes the NLUServer client using the provided {@code configuration}.
     *
     * @param configuration the {@link NLUServerConfiguration} containing the credentials file path
     * @throws IntentRecognitionProviderException if the provided {@code configuration} does not
     *                                            contain a valid url
     */
    public NLUServerClientAPIWrapper(@NonNull NLUServerConfiguration configuration) throws IntentRecognitionProviderException {
        this.serverURL = configuration.getUrl();
    }

    /**
     * Shutdowns the NLUServer client.
     */
    public void shutdown() {
        //Nothing to od here
    }

    public boolean isShutdown()
    {
        return true; // as there is nothing to really shut down
    }

}
