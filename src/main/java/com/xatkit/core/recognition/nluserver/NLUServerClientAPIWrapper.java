package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.mapper.dsl.BotData;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Classification;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import fr.inria.atlanmod.commons.log.Log;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import lombok.NonNull;
import lombok.Value;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the HTTP calls to access the NLUServer server.
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

    private NLUServerConfiguration configuration;

    private BotData bot;

    /**
     * Initializes the NLUServer client using the provided {@code configuration}.
     *
     * @param configuration the {@link NLUServerConfiguration} containing the credentials file path
     * @throws IntentRecognitionProviderException if the provided {@code configuration} does not
     *                                            contain a valid url
     */
    public NLUServerClientAPIWrapper(@NonNull NLUServerConfiguration configuration, BotData bot) throws IntentRecognitionProviderException {

        this.bot = bot;
        this.configuration = configuration;
        Unirest.config().defaultBaseUrl(configuration.getUrl());
    }

    /**
     * Deploy the bot on the NLUServer available in the configuration URL
     * @throws RuntimeException if the provided {@code configuration} does not
     *                                            contain a valid url
     */
    private boolean deployBot() {
        boolean isDeployed = false;
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", bot.getBotId());
        fields.put("force_overwrite", configuration.isForceOverwrite());
        HttpResponse<JsonNode> response = Unirest.post("/bot/new/")
                .fields(fields)
                .asJson();
        if (response.isSuccess()) {
            this.bot.setUUID(response.getBody().getObject().get("uuid").toString());
            HttpResponse<JsonNode> response_initialization = Unirest.post("/bot/{botname}/initialize/")
                    .routeParam("botname",bot.getBotId())
                    .body(botToJSONBot())
                    .asJson();
            if (response_initialization.isSuccess()) {
                isDeployed = true;
            }
        }
        return isDeployed;
    }

    /**
     * Trains the bot on the NLUServer available in the configuration URL
     * @throws RuntimeException if the provided {@code configuration} does not
     * contain a valid url or there is not bot ready to be trained with the right id
     */
    private boolean trainBot() {

        boolean isTrained = false;
        Map<String, Object> configurationFields = new HashMap<>();
        configurationFields.put("country", configuration.getLanguageCode());
        configurationFields.put("num_words", configuration.getNumWords());
        configurationFields.put("lower", configuration.isLower());
        configurationFields.put("oov_token", configuration.getOovToken());
        configurationFields.put("num_epochs", configuration.getNumEpochs());
        configurationFields.put("embedding_dim", configuration.getEmbeddingDim());
        configurationFields.put("input_max_num_tokens", configuration.getMaxNumTokens());
        configurationFields.put("stemmer", configuration.isStemmer());

        HttpResponse<JsonNode> response = Unirest.post("/bot/{botname}/train/")
                .routeParam("botname",bot.getBotId())
                .fields(configurationFields)
                .asJson();
        if (response.isSuccess()) {
            isTrained = true;
        } else {
            Log.warn("Error during bot training {0}", response.getStatusText());
        }
        return isTrained;
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
        Prediction prediction = new Prediction();

        Map<String, Object> fields = new HashMap<>();
        fields.put("utterance", input);
        fields.put("context", nluContext.getName());

        HttpResponse<JsonNode> response = Unirest.post("/bot/{botname}/predict/")
                .routeParam("botname",bot.getBotId())
                .fields(fields)
                .asJson();

        kong.unirest.json.JSONObject predictionResult = response.getBody().getObject();
        JSONArray matched_utterances = predictionResult.getJSONArray("matched_utterances");
        JSONArray prediction_values = predictionResult.getJSONArray("prediction_values");
        JSONArray intents = predictionResult.getJSONArray("intents");

        for (int i=0; i< matched_utterances.length();i++) {
            Classification c = new Classification();
            c.setIntent(this.bot.getIntent(intents.getString(i)));
            c.setScore(prediction_values.getFloat(i));
            c.setMatchedUtterance((matched_utterances.getString(i)));
            prediction.addClassification(c);
        }
        return prediction;
    }

    private JSONObject botToJSONBot() {
       JSONObject jsonObject = new JSONObject();
       jsonObject.append("name", bot.getBotId());

       JSONArray jsonContexts = new JSONArray();
       jsonObject.append("contexts", jsonContexts);
       for (NLUContext c: bot.getNluContexts())
       {
         JSONObject contextJSON = new JSONObject();
         contextJSON.append("name", c.getName());
         JSONArray contextIntentsJSON = new JSONArray();
         contextJSON.append("intents", contextIntentsJSON);
         for (Intent i: c.getIntents() )
         {
             JSONObject intentJSON = new JSONObject();
             intentJSON.append("name", i.getName() );
             JSONArray intentSentencesJSON = new JSONArray();
             intentSentencesJSON.put(i.getTrainingSentences());
             intentJSON.append("training_sentences", intentSentencesJSON);
             contextIntentsJSON.put(intentJSON);
         }
         jsonContexts.put(contextJSON);
       }
       return jsonObject;
    }
}
