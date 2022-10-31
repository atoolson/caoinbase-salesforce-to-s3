package com.andrewtoolson.model;

import lombok.Data;

/**
 * Represents a subset of the data returned from the Salesforce API
 */
@Data
public class SalesforceCaseDetails {
    private String id;
    private String caseNumber;
    private String description;
    private String subject;
    private String origin;
    private String type;
    private String recordType = "Case";
}
