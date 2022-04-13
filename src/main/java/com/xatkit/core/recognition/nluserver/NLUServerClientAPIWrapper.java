package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.mapper.dsl.BotData;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Classification;
import com.xatkit.core.recognition.nluserver.mapper.dsl.CustomEntityType;
import com.xatkit.core.recognition.nluserver.mapper.dsl.CustomEntityTypeEntry;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityParameter;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityType;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.core.recognition.nluserver.mapper.dsl.MatchedParam;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import fr.inria.atlanmod.commons.log.Log;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Contains the HTTP calls to access the NLUServer server.
 * <p>
 * This class is initialized with a {@link NLUServerConfiguration}.
 */

public class NLUServerClientAPIWrapper {

    /**
     * The client instance managing the interaction with the deployed NLU server.
     */

    private final NLUServerConfiguration configuration;

    private final BotData bot;

    private boolean iamshutdown;

    /**
     * Initializes the NLUServer client using the provided {@code configuration}.
     *
     * @param configuration the {@link NLUServerConfiguration} containing the credentials file path
     * @throws IntentRecognitionProviderException if the provided {@code configuration} does not
     *                                            contain a valid url
     */
    public NLUServerClientAPIWrapper(NLUServerConfiguration configuration, BotData bot) throws IntentRecognitionProviderException {

        if (isNull(bot) || isNull(configuration)) {
            throw new IntentRecognitionProviderException("An error occurred when initializing the NLUServer client: "
                    + "bot and configuration cannot be null");
        } else {
            this.bot = bot;
            this.configuration = configuration;

        }
        iamshutdown = false;
        Unirest.config().defaultBaseUrl(configuration.getUrl());
    }


    /**
     * Deploy the bot on the NLUServer available in the configuration URL.
     * @return true if the bot was successfully deployed
     */
    private boolean deployBot() {
        boolean isDeployed = false;

        Map<String, Object> fields = new HashMap<>();
        fields.put("name", bot.getBotName());
        fields.put("force_overwrite", configuration.isForceOverwrite());

        HttpResponse<JsonNode> response
                    = Unirest.post("/bot/new/")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(fields).asJson();
        if (response.getStatus() == 200) {
            this.bot.setUUID(response.getBody().getObject().get("uuid").toString());
            try {


                Map<String, Object> initializationFields = new HashMap<>();
                ArrayList<NLUContextDTO> nluContextDTOs = botToContextList();

                initializationFields.put("name", bot.getBotName());
                initializationFields.put("contexts", nluContextDTOs);


                HttpResponse<JsonNode> responseInitialization = Unirest.post("/bot/{botname}/initialize/")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .routeParam("botname", bot.getBotName())
                        .body(initializationFields)
                        .asJson();
                if (responseInitialization.getStatus() == 200) {
                    isDeployed = true;
                } else {
                    Log.warn("Error during bot initialization: {0}", responseInitialization.getStatusText());
                }
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            Log.warn("Error during bot creation: {0}", response.getStatusText());
        }
        return isDeployed;
    }

    /**
     * Trains the bot on the NLUServer available in the configuration URL
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
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .routeParam("botname", bot.getBotName())
                .body(configurationFields)
                .asJson();
        if (response.isSuccess()) {
            isTrained = true;
        } else {
            Log.warn("Error during bot training {0}", response.getStatusText() + response.getBody().toString());
        }
        return isTrained;
    }

    public boolean deployAndTrainBot() {
        boolean isDeployed;
        boolean isTrained = false;
        isDeployed = deployBot();
        if (isDeployed) {
            isTrained = trainBot();
        }
        return isDeployed && isTrained;

    }
    /**
     * Shutdowns the NLUServer client.
     */
    public void shutdown() {
        this.iamshutdown = true;
    }

    public boolean isShutdown() {
        return iamshutdown;
    }

    public Prediction predict(NLUContext nluContext, String input) {
        Prediction prediction = null;

        Map<String, Object> fields = new HashMap<>();
        fields.put("utterance", input);
        fields.put("context", nluContext.getName());

        HttpResponse<JsonNode> response = Unirest.post("/bot/{botname}/predict/")
                .routeParam("botname", bot.getBotName())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(fields)
                .asJson();

        if (response.isSuccess()) {
            kong.unirest.json.JSONObject predictionResult = response.getBody().getObject();
            JSONArray matchedUtterances = predictionResult.getJSONArray("matched_utterances");
            JSONArray predictionValues = predictionResult.getJSONArray("prediction_values");
            JSONArray intents = predictionResult.getJSONArray("intents");
            JSONObject matchedParams = predictionResult.getJSONObject("matched_params");


            prediction = new Prediction();

            for (int i = 0; i < predictionValues.length(); i++) {
                Classification c = new Classification();
                c.setIntent(this.bot.getIntent(intents.getString(i)));
                c.setScore(predictionValues.getFloat(i));
                c.setMatchedUtterance((matchedUtterances.getString(i)));
                prediction.addClassification(c);
            }

            Map<String, Object> mapParams = matchedParams.toMap();
            //Iterate over the map and add the params to the prediction
            for (Map.Entry<String, Object> entry : mapParams.entrySet()) {
                MatchedParam p = new MatchedParam(entry.getKey(), entry.getValue().toString());
                prediction.addMatchedParam(p);
            }
        } else {
            Log.warn("Error during bot prediction {0}", response.getStatusText() + response.getBody().toString());
        }
        return prediction;
    }

    private class NLUContextDTO {
        String name;
        ArrayList<IntentDTO> intents = new ArrayList<>();
        //name syntax to map the  Python names in the server
        ArrayList<EntityTypeDTO> entities = new ArrayList<>();
    }

    //We use a single class to represent all the possible entites, either custom or not
    private class EntityTypeDTO {
        String name;
        List<CustomEntityEntryDTO> entries = new ArrayList<>();
    }

    private class CustomEntityEntryDTO {
        String value;
        ArrayList<String> synonyms = new ArrayList<>();
    }

    private class IntentDTO {
        String name;
        List<String> training_sentences = new ArrayList<>();
        List<EntityReferenceDTO> entity_parameters = new ArrayList<>();
    }

    private class EntityReferenceDTO {
        String fragment;
        String name;
        EntityTypeDTO entity;
    }

    private ArrayList<NLUContextDTO> botToContextList() {

        Map<String, EntityTypeDTO> mapEntityTypes = new HashMap<>();
        ArrayList<NLUContextDTO> nluContextDTOs = new ArrayList<>();

        for (NLUContext c: bot.getNluContexts()) {
            NLUContextDTO cDTO = new NLUContextDTO();
            cDTO.name = c.getName();

            if (c.getEntities() != null) {

                for (EntityType e : c.getEntities()) {
                    EntityTypeDTO etDTO = new EntityTypeDTO();
                    etDTO.name = e.getName();
                    if (e instanceof CustomEntityType) {
                        for (CustomEntityTypeEntry entry : ((CustomEntityType) e).getEntries()) {
                            CustomEntityEntryDTO entryDTO = new CustomEntityEntryDTO();
                            entryDTO.value = entry.getValue();
                            entryDTO.synonyms.addAll(entry.getSynonyms());
                            etDTO.entries.add(entryDTO);
                        }
                        cDTO.entities.add(etDTO);
                        mapEntityTypes.put(etDTO.name, etDTO);
                    }
                }
            }

            for (Intent i: c.getIntents()) {
                IntentDTO iDTO = new IntentDTO();
                iDTO.name = i.getName();
                iDTO.training_sentences = i.getTrainingSentences();
                cDTO.intents.add(iDTO);
                for (EntityParameter er: i.getParameters()) {
                    EntityReferenceDTO erDTO = new EntityReferenceDTO();
                    erDTO.fragment = er.getFragment();
                    erDTO.name = er.getName();
                    erDTO.entity = mapEntityTypes.get(er.getType().getName());
                    iDTO.entity_parameters.add(erDTO);
                }
            }

            nluContextDTOs.add(cDTO);
        }
        return nluContextDTOs;
    }


}
