package com.xatkit.core.recognition.nluserver.mapper.dsl;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a Intent part of a {@link NLUContext}.
 */
public class Intent {

    private final String name;

    /**
     * List of training sentences to classify the {@link Intent}.
     */
    private List<String> trainingSentences;


    /**
     * List of entities used in the definition of the intent.
     */
    private List<EntityParameter> parameters;

    public Intent(String name) {
        this.name = name;
        this.trainingSentences = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    /**
     * @return the trainingSentences
     */
    public List<String> getTrainingSentences() {
        return this.trainingSentences;
    }

    /**
     * @param trainingSentences the trainingSentences to set
     */
    public void setTrainingSentences(List<String> trainingSentences) {
        this.trainingSentences = trainingSentences;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the parameters
     */
    public List<EntityParameter> getParameters() {
        return this.parameters;
    }

    public void addParameter(EntityParameter p) {
        this.parameters.add(p);
    }

    /**
     * @param t the training sentence to add
     * @return the modified {@link Intent}
     */
    public Intent addTrainingSentence(String t) {
        this.trainingSentences.add(t);
        return this;
    }


}
