package com.andrewtoolson.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * Taken from https://github.com/shalabh-jaiswal/aws-lambda-patterns/blob/master/src/main/java/us/shalabh/alp/model/HttpRequest.java
 *
 * A class that encapsulates the incoming http request data.
 */

@Data
@Accessors(chain = true)
public class HttpRequest
{

    private String resource;
    private String path;
    private String httpMethod;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryStringParameters = new HashMap<>();;
    private Map<String, String> pathParameters = new HashMap<>();;
    private Map<String, String> stageVariables = new HashMap<>();;
    private String body;
    private RequestContext requestContext;
    private Boolean isBase64Encoded;


    @Data
    public static class RequestContext {
        private String accountId;
        private String resourceId;
        private String stage;
        private String requestId;
        private Map<String, String> identity;
        private String resourcePath;
        private String httpMethod;
        private String apiId;
    }

}