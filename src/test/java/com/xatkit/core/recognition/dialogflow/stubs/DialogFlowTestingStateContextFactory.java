package com.xatkit.core.recognition.dialogflow.stubs;

import com.xatkit.core.recognition.dialogflow.DialogFlowStateContext;
import com.xatkit.execution.StateContext;
import com.xatkit.stubs.TestingStateContext;
import com.xatkit.stubs.TestingStateContextFactory;
import lombok.NonNull;

public class DialogFlowTestingStateContextFactory extends TestingStateContextFactory {

    public DialogFlowTestingStateContextFactory() {
        super();
    }

    @Override
    public TestingStateContext wrap(@NonNull StateContext stateContext) {
        if(stateContext instanceof DialogFlowStateContext) {
            return new DialogFlowTestingStateContext((DialogFlowStateContext) stateContext);
        } else {
            return super.wrap(stateContext);
        }
    }
}
