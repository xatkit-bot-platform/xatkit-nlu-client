package com.xatkit.core.recognition.nluserver.mapper.dsl;


import lombok.Data;

@Data
public class Classification {

    private Intent intent;

    private Float score;

}
