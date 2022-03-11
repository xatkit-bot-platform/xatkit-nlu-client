package com.xatkit.core.recognition.nluserver.mapper.dsl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NLUContext {

    private String name;

    private ArrayList<Intent> intents;

    private ArrayList<String> intentNames;

    public NLUContext(String name) {
        this.name = name;
        intents = new ArrayList<>();
        intentNames = new ArrayList<>();
    }

    public void addIntent(Intent i) {
        if (i!=null) {
            intents.add(i);
        } else {
            throw new NullPointerException("The intent to add to a context canont be null");
        }
    }

    public void addIntentNames(List<String> intentNames) {
        intentNames.addAll(intentNames);
    }

}
