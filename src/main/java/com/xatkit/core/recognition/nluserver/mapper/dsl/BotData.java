package com.xatkit.core.recognition.nluserver.mapper.dsl;

import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import kong.unirest.json.JSONArray;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.json.JSONObject;

import java.util.List;

@Data
@Builder
public class BotData {

    public BotData(String botId) {
        this.botId = botId;
    }

    @NonNull
    private String botId;

    // Internal UUID assigned by the server, not used by now
    private String UUID;

    @NonNull
    private NLUServerConfiguration config;

    @NonNull
    private List<NLUContext> nluContexts;

    @NonNull
    private List<Intent> intents;

    private List<EntityType> entities;

    public boolean containsIntent(String name) {
        return (this.intents.stream().filter(i -> i.getName().equals(name)).findAny().orElse(null) == null);
    }

    public boolean containsNLUContext(String name) {
        return (this.nluContexts.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null) == null);
    }

    public Intent getIntent(String name) {
        return (this.intents.stream().filter(i -> i.getName().equals(name)).findAny().orElse(null));
    }

    public NLUContext getNluContext(String name) {
        return (this.nluContexts.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null));
    }


}
