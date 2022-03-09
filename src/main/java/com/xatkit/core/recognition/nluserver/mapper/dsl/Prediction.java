package com.xatkit.core.recognition.nluserver.mapper.dsl;

import java.util.List;

public class Prediction {
    private List<Classification> classifications;

    public Classification getTopClassification()
    {
        Classification current = classifications.get(0);
        for (Classification c: classifications)
        {
            if (c.getScore() > current.getScore())
            {
                current = c;
            }
        }
        return current;
    }


}

