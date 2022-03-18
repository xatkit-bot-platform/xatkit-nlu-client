package com.xatkit.core.recognition.nluserver.utils;

import com.xatkit.execution.impl.StateImpl;
import com.xatkit.intent.IntentDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A mocked {@link com.xatkit.execution.State} with a configurable implementation of {@link #getAllAccessedIntents()}.
 * <p>
 * This mock can be configured with {@link #setIntents(List)}. The provided {@code intents} will be returned by the
 * next calls to {@link #getAllAccessedIntents()}.
 */
public class FakeState extends StateImpl {

    /**
     * The list of {@link IntentDefinition} to return when calling {@link #getAllAccessedIntents()}.
     */
    private List<IntentDefinition> intents = new ArrayList<>();

    /**
     * Sets the intents to return when calling {@link #getAllAccessedIntents()}.
     *
     * @param intents the intents to set
     */
    public void setIntents(List<IntentDefinition> intents) {
        this.intents = intents;
    }

    /**
     * Returns the preset intents provided in {@link #setIntents(List)}.
     * <p>
     * <b>Note</b>: this method returns an empty list if there is no enabled intent to return.
     *
     * @return the preset list of accessed intents
     */
    @Override
    public Collection<IntentDefinition> getAllAccessedIntents() {
        return intents;
    }
}
