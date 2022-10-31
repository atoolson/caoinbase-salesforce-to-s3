package com.andrewtoolson.model;

/**
 * Taken from https://github.com/shalabh-jaiswal/aws-lambda-patterns/blob/master/src/main/java/us/shalabh/alp/model/HttpResponse.java
 */

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class HttpResponse {
    private Integer statusCode;
    private Map<String, String> headers = new HashMap<>();
    private String body;
}