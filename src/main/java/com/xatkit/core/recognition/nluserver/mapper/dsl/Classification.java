package com.xatkit.core.recognition.nluserver.mapper.dsl;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Classification {

    private Intent intent;

    private Float score;

    private String matchedUtterance;

    private List<MatchedParam> matchedParams = new ArrayList<>();

    public void addMatchedParam(MatchedParam mp) {
        matchedParams.add(mp);
    }

}
