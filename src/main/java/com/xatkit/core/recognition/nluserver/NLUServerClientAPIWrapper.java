package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.mapper.dsl.BotData;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import com.xatkit.util.FileUtils;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import lombok.Value;

import java.util.concurrent.ExecutionException;

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

    private BotData bot;

    /**
     * Initializes the NLUServer client using the provided {@code configuration}.
     *
     * @param configuration the {@link NLUServerConfiguration} containing the credentials file path
     * @throws IntentRecognitionProviderException if the provided {@code configuration} does not
     *                                            contain a valid url
     */
    public NLUServerClientAPIWrapper(@NonNull NLUServerConfiguration configuration, BotData bot) throws IntentRecognitionProviderException {
        this.serverURL = configuration.getUrl();
        this.bot = bot;
    }

    /**
     * Deploy the bot on the NLUServer available in the configuration URL
     * @throws RuntimeException if the provided {@code configuration} does not
     *                                            contain a valid url
     */
    private boolean deployBot() {

        //if (!isDeployed) throw new RuntimeException("Unable to deploy the bot");
        return true;

    }

    /**
     * Trains the bot on the NLUServer available in the configuration URL
     * @throws RuntimeException if the provided {@code configuration} does not
     * contain a valid url or there is not bot ready to be trained with the right id
     */
    private boolean trainBot() {

        return true;
    }

    public boolean deployAndTrainBot() {
        boolean isDeployed, isTrained = false;
        isDeployed = deployBot();
        if(isDeployed) {
            isTrained = trainBot();
        }
        return isDeployed && isTrained;

    }
    /**
     * Shutdowns the NLUServer client.
     */
    public void shutdown() {
        //Nothing to do here
    }

    public boolean isShutdown() {
        return false; // as there is nothing to really shut down we always allow for operations on the server
    }

    public Prediction predict(NLUContext nluContext, String input) {
        Prediction prediction;

        return prediction;
    }

}
