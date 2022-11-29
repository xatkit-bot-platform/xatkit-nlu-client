package com.xatkit.core.recognition.nluserver.mapper.dsl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NLUContext {

    private String name;

    private List<IntentReference> intentReferences;

    public NLUContext(String name) {
        this.name = name;
        intentReferences = new ArrayList<>();
    }

    public void addAllIntentReferences(List<IntentReference> intentRefs) {
        this.intentReferences.addAll(intentRefs);
    }

    public void addIntentReference(Intent intent) {
        IntentReference intentReference = new IntentReference(intent.getName());
        intentReference.setIntent(intent);
        intentReferences.add(intentReference);
    }

}
