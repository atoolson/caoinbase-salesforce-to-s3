package com.andrewtoolson.salesforcetos3;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.andrewtoolson.exception.BadAccessTokenException;
import com.andrewtoolson.model.HttpRequest;
import com.andrewtoolson.model.HttpResponse;
import com.andrewtoolson.model.SalesforceAccessToken;
import com.andrewtoolson.model.SalesforceCaseDetails;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.utils.StringUtils;

import java.io.IOException;
import java.util.Map;

public class CaseToS3Controller implements RequestHandler<HttpRequest, HttpResponse> {

    private SalesforceAccessToken token = null;
    private final SalesforceCaseDetailsService caseDetailsService = new SalesforceCaseDetailsService();
    private final S3Service s3Service = new S3Service();
    private final ConfigService configService = new ConfigService();

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    @Override
    public HttpResponse handleRequest(HttpRequest input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("--- just arrived ---");

        // first run
        if (token == null) {
            return refreshToken(input, logger);
        }

        String caseId = input.getQueryStringParameters().get("caseId");
        if (StringUtils.isBlank(caseId) || "null".equals(caseId)) {
            // TODO list all case ids
            return new HttpResponse()
                .setStatusCode(HttpStatusCode.BAD_REQUEST)
                .setBody("caseId must be provided as a query param");
        }

        String json = null;

        try {
            SalesforceCaseDetails caseDetails = caseDetailsService.getCaseDetails(caseId, token.getAccessToken());
            json = gson.toJson(caseDetails);
        } catch (BadAccessTokenException e) {
            return refreshToken(input, logger);
        } catch (IOException e) {
            return new HttpResponse()
                    .setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                    .setBody("error retrieving case details");
        }

        s3Service.storeInS3(json, "caseDetails/" + caseId + ".json");

        return new HttpResponse()
                .setStatusCode(HttpStatusCode.OK)
                .setBody(json);
    }

    private HttpResponse refreshToken(HttpRequest request, LambdaLogger logger) {
        // do we have a code request variable?
        if (request.getQueryStringParameters().containsKey("code")) {
            logger.log("has code param. refreshing token.");
            String code = request.getQueryStringParameters().get("code");
            return refreshToken(code, logger, request);
        }

        // there is no code param. redirect.
        return new HttpResponse()
                .setStatusCode(HttpStatusCode.MOVED_TEMPORARILY)
                .setHeaders(Map.of("Location", "https://self454-dev-ed.lightning.force.com/services/oauth2/authorize?" +
                        "client_id=" + configService.getSalesforceClientId() + "&" +
                        "redirect_uri=" + configService.getRootUrl() + "&" +
                        "state=" + request.getQueryStringParameters().get("caseId") + "&" +
                        "response_type=code"));
    }

    public HttpResponse refreshToken(String code, LambdaLogger logger, HttpRequest input) {
        HttpPost post = new HttpPost("https://login.salesforce.com/services/oauth2/token");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("grant_type",  "authorization_code");
        builder.addTextBody("code", code);
        builder.addTextBody("client_id",  configService.getSalesforceClientId());
        builder.addTextBody("client_secret", configService.getSalesforceClientSecret());
        builder.addTextBody("redirect_uri",  configService.getRootUrl());
        logger.log("going to sent request");


        CloseableHttpClient client = HttpClients.createDefault();
        post.setEntity(builder.build());
        try {
            CloseableHttpResponse response = client.execute(post);
            String bodyAsString = EntityUtils.toString(response.getEntity());
            token = gson.fromJson(bodyAsString, SalesforceAccessToken.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);

        if (!token.hasError()) {
            // successfully got the token. redirect back to the main URL.
            String caseId = input.getQueryStringParameters().get("state");
            response.setHeaders(Map.of(HttpHeaders.LOCATION, configService.getRootUrl() + "?caseId=" + caseId));
            response.setStatusCode(HttpStatusCode.MOVED_TEMPORARILY);
        } else {
            response.setBody("there was an error getting an access code. Please visit " + configService.getRootUrl() + " and try again.");
        }

        return response;
    }
}