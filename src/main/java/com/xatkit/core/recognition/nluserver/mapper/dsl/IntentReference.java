package com.xatkit.core.recognition.nluserver.mapper.dsl;


import java.util.List;

/**
 * A reference to a bot intent that can be matched from a NLUContext
 * @see NLUContext#addAllIntentReferences(List)
 */
public class IntentReference {

    private String name;

    private Intent intent;

    public IntentReference(String intentName) {
        this.name = intentName;
    }

    public String getName() {
        return this.name;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

}
