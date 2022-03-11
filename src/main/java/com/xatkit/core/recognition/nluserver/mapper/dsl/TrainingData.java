package com.xatkit.core.recognition.nluserver.mapper.dsl;

import com.xatkit.core.recognition.nluserver.NLUServerConfiguration;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class TrainingData {

    @NonNull
    private NLUServerConfiguration config;

    @NonNull
    private List<Intent> nluContexts;

    @NonNull
    private List<Intent> intents;

    private List<EntityType> entities;
}
