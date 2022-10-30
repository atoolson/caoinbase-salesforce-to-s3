package com.andrewtoolson.model;

import lombok.Data;
import software.amazon.awssdk.utils.StringUtils;

@Data
public class SalesforceAccessToken {
    private String accessToken;
    private String signature;
    private String scope;
    private String instanceUrl;
    private String id;
    private String tokenType;
    private long issuedAt;

    private String error;
    private String errorDescription;

    public boolean hasError() {
        return !StringUtils.isBlank(error);
    }
}
