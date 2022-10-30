package com.andrewtoolson.model;


import lombok.Data;

import java.util.Map;

@Data
public class HttpResponse {
    private Integer statusCode;
    private Map<String, String> headers;
    private String body;
}