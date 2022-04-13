package com.xatkit.core.recognition.nluserver.mapper.dsl;

import java.util.ArrayList;
import java.util.List;

public class Prediction {
    private List<Classification> classifications;

    private List<MatchedParam> matchedParams;

    public Prediction() {

        this.classifications = new ArrayList<>();
        this.matchedParams = new ArrayList<>();
    }

    public Classification getTopClassification() {
        Classification current = classifications.get(0);
        for (Classification c: classifications) {
            if (c.getScore() > current.getScore()) {
                current = c;
            }
        }
        return current;
    }

    public List<Classification> getClassifications(){
        return classifications;
    }

    public boolean isEmpty() {
        return classifications.isEmpty();
    }

    public void addClassification(Classification c) {
        classifications.add(c);
    }

    public void addMatchedParam(MatchedParam mp) {
        matchedParams.add(mp);
    }

    public List<MatchedParam> getMatchedParams() {
        return matchedParams;
    }

}
