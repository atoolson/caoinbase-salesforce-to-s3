package com.andrewtoolson.salesforcetos3;

/**
 * This class makes it easy to read the configuration values set for this application
 */
public class ConfigService {
    public String getSalesforceClientId() {
        return System.getenv("SALESFORCE_CLIENT_ID");
    }

    public String getSalesforceClientSecret() {
        return System.getenv("SALESFORCE_CLIENT_SECRET");
    }

    public String getRootUrl() {
        return "https://ne2xddjtut7joxmjocltodf2c40jngkp.lambda-url.us-west-1.on.aws/";
    }

}
