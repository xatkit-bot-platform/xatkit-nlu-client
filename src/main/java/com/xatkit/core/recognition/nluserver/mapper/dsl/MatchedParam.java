package com.xatkit.core.recognition.nluserver.mapper.dsl;

import java.util.Map;

public class MatchedParam {
    String paramName;
    String value;
    Map<String, Object> info;

    public MatchedParam(String paramName, String value, Map<String, Object> info) {
        this.paramName = paramName;
        this.value = value;
        this.info = info;
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

    public Map<String, Object> getInfo() {
        return info;
    }

    public Object getInfoItem(String key) {
        return info.get(key);
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }
}
