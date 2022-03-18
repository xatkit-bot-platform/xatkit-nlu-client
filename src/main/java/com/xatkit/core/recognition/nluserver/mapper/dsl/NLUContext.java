package com.xatkit.core.recognition.nluserver.mapper.dsl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
            throw new NullPointerException("The intent to add to a context cannot be null");
        }
    }

    public void addIntentNames(List<String> intentNames) {
        this.intentNames.addAll(intentNames);
    }

}
