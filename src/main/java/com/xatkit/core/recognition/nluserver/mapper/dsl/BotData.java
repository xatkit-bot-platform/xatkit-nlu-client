package com.xatkit.core.recognition.nluserver.mapper.dsl;

import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class BotData {

    @NonNull
    private String botName = null;

    // Internal UUID assigned by the server, not used by now
    private String UUID;

    //@NonNull
    //private NLUServerConfiguration config;

    @NonNull
    private List<NLUContext> nluContexts;

    @NonNull
    private List<Intent> intents;

    private List<EntityType> entities;

    public BotData(String botName) {
        this.botName = botName;
        this.intents = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.nluContexts = new ArrayList<>();
    }

    public boolean containsIntent(String name) {
        return (this.intents.stream().filter(i -> i.getName().equals(name)).findAny().orElse(null) != null);
    }

    public boolean containsNLUContext(String name) {
        return (this.nluContexts.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null) != null);
    }

    public Intent getIntent(String name) {
        return (this.intents.stream().filter(i -> i.getName().equals(name)).findAny().orElse(null));
    }

    public EntityType getEntityType(String name) {
        return (this.entities.stream().filter(i -> i.getName().equals(name)).findAny().orElse(null));
    }

    public void addIntent(Intent i) {
        this.intents.add(i);
    }

    public void addNLUContext(NLUContext nluContext) {
        this.nluContexts.add(nluContext);
    }

    public NLUContext getNluContext(String name) {
        return (this.nluContexts.stream().filter(c -> c.getName().equals(name)).findAny().orElse(null));
    }

    public void addEntityType(EntityType entityType) {
        this.entities.add(entityType);
    }

}
