package com.andrewtoolson.model;

import lombok.Data;
import software.amazon.awssdk.utils.StringUtils;

/**
 * A class to model the access token returned from the Salesforce API
 */
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
