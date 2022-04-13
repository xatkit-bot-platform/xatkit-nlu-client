package com.xatkit.core.recognition.nluserver.mapper.dsl;

public class MatchedParam {
    String paramName;
    String value;

    public MatchedParam(String paramName, String value) {
        this.paramName = paramName;
        this.value = value;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
