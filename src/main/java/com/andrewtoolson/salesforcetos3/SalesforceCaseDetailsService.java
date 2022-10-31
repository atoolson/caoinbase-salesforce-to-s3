package com.andrewtoolson.salesforcetos3;

import com.andrewtoolson.exception.BadAccessTokenException;
import com.andrewtoolson.model.SalesforceCaseDetails;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import software.amazon.awssdk.http.HttpStatusCode;

import java.io.IOException;

/**
 * This class interfaces with the Salesforce API to retrieve data around cases.
 */
public class SalesforceCaseDetailsService {
    private final CloseableHttpClient client = HttpClients.createDefault();
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create();

    public SalesforceCaseDetails getCaseDetails(String caseId, String bearer) throws IOException {
        HttpUriRequest get = RequestBuilder.get()
                .setUri("https://self454-dev-ed.my.salesforce.com/services/data/v56.0/sobjects/Case/" + caseId)
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearer)
                .build();


        CloseableHttpResponse response = client.execute(get);

        String json = EntityUtils.toString(response.getEntity());
        if (response.getStatusLine().getStatusCode() == HttpStatusCode.UNAUTHORIZED) {
            throw new BadAccessTokenException(json);
        }

        return gson.fromJson(json, SalesforceCaseDetails.class);
    }

}
