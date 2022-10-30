package com.andrewtoolson.model;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
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