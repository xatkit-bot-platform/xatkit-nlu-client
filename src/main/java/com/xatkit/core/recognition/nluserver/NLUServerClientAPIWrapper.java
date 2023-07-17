package com.xatkit.core.recognition.nluserver;

import com.xatkit.core.recognition.IntentRecognitionProviderException;
import com.xatkit.core.recognition.nluserver.mapper.dsl.BotData;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Classification;
import com.xatkit.core.recognition.nluserver.mapper.dsl.CustomEntityType;
import com.xatkit.core.recognition.nluserver.mapper.dsl.CustomEntityTypeEntry;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityParameter;
import com.xatkit.core.recognition.nluserver.mapper.dsl.EntityType;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Intent;
import com.xatkit.core.recognition.nluserver.mapper.dsl.IntentReference;
import com.xatkit.core.recognition.nluserver.mapper.dsl.MatchedParam;
import com.xatkit.core.recognition.nluserver.mapper.dsl.NLUContext;
import com.xatkit.core.recognition.nluserver.mapper.dsl.Prediction;
import fr.inria.atlanmod.commons.log.Log;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;


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
                BotDTO botDTO = new BotDTO(bot);

                initializationFields.put("name", botDTO.name);
                initializationFields.put("contexts", botDTO.contexts);
                initializationFields.put("entities", botDTO.entities);
                initializationFields.put("intents", botDTO.intents);

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
        configurationFields.put("region", configuration.getLanguageRegionCode());
        configurationFields.put("timezone", configuration.getTimezone());
        configurationFields.put("num_words", configuration.getNumWords());
        configurationFields.put("lower", configuration.isLower());
        configurationFields.put("oov_token", configuration.getOovToken());
        configurationFields.put("num_epochs", configuration.getNumEpochs());
        configurationFields.put("embedding_dim", configuration.getEmbeddingDim());
        configurationFields.put("input_max_num_tokens", configuration.getInputMaxNumTokens());
        configurationFields.put("stemmer", configuration.isStemmer());
        configurationFields.put("discard_oov_sentences", configuration.isDiscardOovSentences());
        configurationFields.put("check_exact_prediction_match", configuration.isCheckExactPredictionMatch());
        configurationFields.put("use_ner_in_prediction", configuration.isUseNerInPrediction());
        configurationFields.put("activation_last_layer", configuration.getActivationLastLayer());
        configurationFields.put("activation_hidden_layers", configuration.getActivationHiddenLayers());

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
            kong.unirest.json.JSONObject predictionDTO = response.getBody().getObject();
            JSONArray classificationsDTO = predictionDTO.getJSONArray("classifications");

            prediction = new Prediction();

            for (int i = 0; i < classificationsDTO.length(); i++) {
                JSONObject classificationDTO = classificationsDTO.getJSONObject(i);
                Classification c = new Classification();
                c.setIntent(this.bot.getIntent(classificationDTO.getString("intent")));
                c.setScore(classificationDTO.getFloat("score"));
                c.setMatchedUtterance(classificationDTO.getString("matched_utterance"));
                JSONArray matchedParamsDTO = classificationDTO.getJSONArray("matched_parameters");
                //Iterate over the map and add the params to the prediction
                for (int j = 0; j < matchedParamsDTO.length(); j++) {
                    JSONObject matchedParamDTO = matchedParamsDTO.getJSONObject(j);
                    MatchedParam p = new MatchedParam(matchedParamDTO.getString("name"),
                            matchedParamDTO.optString("value", ""),
                            matchedParamDTO.optJSONObject("info").toMap());
                    c.addMatchedParam(p);
                }
                prediction.addClassification(c);
            }
        } else {
            Log.warn("Error during bot prediction {0}", response.getStatusText() + response.getBody().toString());
        }
        return prediction;
    }

    private class CustomEntityEntryDTO {
        String value;
        List<String> synonyms = new ArrayList<>();
    }

    private class EntityTypeDTO {
        String name;
        List<CustomEntityEntryDTO> entries = new ArrayList<>();
        //We use a single class to represent all the possible entities, either custom or not
    }

    private class IntentParameterDTO {
        String fragment;
        String name;
        String entity;
    }

    private class IntentDTO {
        String name;
        List<String> training_sentences = new ArrayList<>();
        List<IntentParameterDTO> parameters = new ArrayList<>();
    }

    private class IntentReferenceDTO {
        String intent;
    }

    private class NLUContextDTO {
        String name;
        List<IntentReferenceDTO> intent_refs = new ArrayList<>();
    }

    private class BotDTO {
        String name;
        List<NLUContextDTO> contexts = new ArrayList<>();
        List<EntityTypeDTO> entities = new ArrayList<>();
        List<IntentDTO> intents = new ArrayList<>();

        BotDTO (BotData bot) {
            this.name = bot.getBotName();
            for (EntityType e : bot.getEntities()) {
                EntityTypeDTO eDTO = new EntityTypeDTO();
                eDTO.name = e.getName();
                if (e instanceof CustomEntityType) {
                    for (CustomEntityTypeEntry entry : ((CustomEntityType) e).getEntries()) {
                        CustomEntityEntryDTO entryDTO = new CustomEntityEntryDTO();
                        entryDTO.value = entry.getValue();
                        entryDTO.synonyms.addAll(entry.getSynonyms());
                        eDTO.entries.add(entryDTO);
                    }
                }
                this.entities.add(eDTO);
            }

            for (Intent i : bot.getIntents()) {
                IntentDTO intentDTO = new IntentDTO();
                intentDTO.name = i.getName();
                intentDTO.training_sentences.addAll(i.getTrainingSentences());
                for (EntityParameter ep : i.getParameters()) {
                    IntentParameterDTO erDTO = new IntentParameterDTO();
                    erDTO.name = ep.getName();
                    erDTO.fragment = ep.getFragment();
                    erDTO.entity = ep.getType().getName();
                    intentDTO.parameters.add(erDTO);
                }
                this.intents.add(intentDTO);
            }

            for (NLUContext c : bot.getNluContexts()) {
                NLUContextDTO cDTO = new NLUContextDTO();
                cDTO.name = c.getName();
                for (IntentReference ir : c.getIntentReferences()) {
                    IntentReferenceDTO irDTO = new IntentReferenceDTO();
                    irDTO.intent = ir.getName();
                    irDTO.intent = ir.getName();
                    cDTO.intent_refs.add(irDTO);
                }
                this.contexts.add(cDTO);
            }
        }
    }
}
