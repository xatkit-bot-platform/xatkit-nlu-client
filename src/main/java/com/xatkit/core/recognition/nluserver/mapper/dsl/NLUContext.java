package com.xatkit.core.recognition.nluserver.mapper.dsl;



import java.util.ArrayList;

public class NLUContext {

    String name;

    ArrayList<Intent> intents;

    public NLUContext(String name) {
        this.name = name;
        intents = new ArrayList<>();
    }

    public void addIntent(Intent i)
    {
        if (i!=null)
        {
            intents.add(i);
        }
        else {
            throw new NullPointerException("The intent to add to a context canont be null");
        }
    }


    public String getName() {
        return name;
    }

    public int numberIntents() {
        return intents.size();
    }

}
