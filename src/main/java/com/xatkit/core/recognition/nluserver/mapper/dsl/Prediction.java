package com.xatkit.core.recognition.nluserver.mapper.dsl;

import java.util.ArrayList;
import java.util.List;

public class Prediction {
    private List<Classification> classifications;

    public Prediction() {
        this.classifications = new ArrayList<>();
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

}
