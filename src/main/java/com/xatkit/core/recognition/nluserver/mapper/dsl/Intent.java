package com.xatkit.core.recognition.nluserver.mapper.dsl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a Intent part of a {@link NLUContext}.
 */

@Data
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
     * @param t the training sentence to add
     * @return the modified {@link Intent}
     */
    public Intent addTrainingSentence(String t) {
        this.trainingSentences.add(t);
        return this;
    }


}
