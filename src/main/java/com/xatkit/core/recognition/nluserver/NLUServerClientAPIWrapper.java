package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.util.FileUtils;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Contains the clients used to access the DialogFlow API.
 * <p>
 * This class is initialized with a {@link DialogFlowConfiguration}, and setup the DialogFlow clients with the
 * credential file located at {@link DialogFlowConfiguration#getGoogleCredentialsPath()}.
 * <p>
 * If the provided {@link DialogFlowConfiguration} does not contain a path to a credentials file this class will try
 * to load it from environment variables.
 */
@Value
public class NLUServerClientAPIWrapper {

    /**
     * The client instance managing DialogFlow agent-related queries.
     * <p>
     * This client is used to compute project-level operations, such as the training of the underlying DialogFlow's
     * agent.
     */
    private AgentsClient agentsClient;


    /**
     * Initializes the DialogFlow clients using the provided {@code configuration}.
     * <p>
     * The created clients are setup with the credential file located at
     * {@link DialogFlowConfiguration#getGoogleCredentialsPath()}.
     * <p>
     * If the provided {@code configuration} does not define a credentials file path the created clients are
     * initialized from the credentials file path stored in the {@code GOOGLE_APPLICATION_CREDENTIALS} environment
     * variable.
     *
     * @param configuration the {@link DialogFlowConfiguration} containing the credentials file path
     * @throws IntentRecognitionProviderException if the provided {@code configuration} or {@code
     *                                            GOOGLE_APPLICATION_CREDENTIALS} environment variable does not
     *                                            contain a valid credentials file path
     */
    public NLUServerClientAPIWrapper(@NonNull DialogFlowConfiguration configuration) throws IntentRecognitionProviderException {
        CredentialsProvider credentialsProvider = getCredentialsProvider(configuration);
        AgentsSettings agentsSettings;
        IntentsSettings intentsSettings;
        EntityTypesSettings entityTypesSettings;
        SessionsSettings sessionsSettings;
        try {
            if (isNull(credentialsProvider)) {
                /*
                 * No credentials provided, using the GOOGLE_APPLICATION_CREDENTIALS environment variable.
                 */
                Log.warn("No credentials file provided, using GOOGLE_APPLICATION_CREDENTIALS environment variable");
                agentsSettings = AgentsSettings.newBuilder().build();
                intentsSettings = IntentsSettings.newBuilder().build();
                entityTypesSettings = EntityTypesSettings.newBuilder().build();
                sessionsSettings = SessionsSettings.newBuilder().build();
            } else {
                agentsSettings = AgentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
                intentsSettings = IntentsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
                entityTypesSettings = EntityTypesSettings.newBuilder().setCredentialsProvider(credentialsProvider)
                        .build();
                sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
            }
            this.agentsClient = AgentsClient.create(agentsSettings);
            this.sessionsClient = SessionsClient.create(sessionsSettings);
            this.intentsClient = IntentsClient.create(intentsSettings);
            this.entityTypesClient = EntityTypesClient.create(entityTypesSettings);
        } catch (IOException e) {
            throw new IntentRecognitionProviderException("An error occurred when initializing the DialogFlow clients,"
                    + " see attached exception", e);
        }
    }

    /**
     * Shutdowns the DialogFlow clients.
     */
    public void shutdown() {
        this.sessionsClient.shutdownNow();
        this.intentsClient.shutdownNow();
        this.entityTypesClient.shutdownNow();
        this.agentsClient.shutdownNow();
    }

    /**
     * Returns whether the DialogFlow clients are shutdown.
     * <p>
     * This method returns {@code true} if at least {@code 1} client is shutdown.
     *
     * @return {@code true} if the DialogFlow clients are shutdown, {@code false} otherwise
     */
    public boolean isShutdown() {
        return this.sessionsClient.isShutdown()
                && this.intentsClient.isShutdown()
                && this.entityTypesClient.isShutdown()
                && this.agentsClient.isShutdown();
    }

    /**
     * Shutdowns the DialogFlow clients if they haven't been shutdown yet.
     * <p>
     * This method logs a warning for each client closed this way, use {@link #shutdown()} to shutdown the clients
     * appropriately.
     */
    @Override
    protected void finalize() {
        if (!this.sessionsClient.isShutdown()) {
            Log.warn("DialogFlow session was not closed properly, calling automatic shutdown");
            this.sessionsClient.shutdownNow();
        }
        if (!this.intentsClient.isShutdown()) {
            Log.warn("DialogFlow Intent client was not closed properly, calling automatic shutdown");
            this.intentsClient.shutdownNow();
        }
        if (!this.entityTypesClient.isShutdown()) {
            Log.warn("DialogFlow EntityType client was not closed properly, calling automatic shutdown");
            this.entityTypesClient.shutdownNow();
        }
        if (!this.agentsClient.isShutdown()) {
            Log.warn("DialogFlow Agent client was not closed properly, calling automatic shutdown");
            this.agentsClient.shutdownNow();
        }
    }


}
