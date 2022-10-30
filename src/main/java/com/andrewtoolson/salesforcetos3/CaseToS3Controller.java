package com.andrewtoolson.salesforcetos3;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.andrewtoolson.model.HttpRequest;
import com.andrewtoolson.model.HttpResponse;
import com.andrewtoolson.model.SalesforceAccessToken;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import software.amazon.awssdk.http.HttpStatusCode;

import java.io.IOException;
import java.util.Map;

public class CaseToS3Controller implements RequestHandler<HttpRequest, HttpResponse> {

    private static final String ROOT_URL = "https://ne2xddjtut7joxmjocltodf2c40jngkp.lambda-url.us-west-1.on.aws/";

    private SalesforceAccessToken token = null;

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public HttpResponse handleRequest(HttpRequest input, Context context) {
        // get the case details
        // if auth denied
        // get auth code
        // get access code
        // get case details
        // still error? throw
        if (true)
        return refreshToken(input, context.getLogger());



        LambdaLogger logger = context.getLogger();

        // log execution details
        logger.log("ENVIRONMENT VARIABLES: " + System.getenv());
        logger.log("CONTEXT: " + context);

        // process event
        logger.log("INPUT: " + input);

        String json = gson.toJson(input);


        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setBody(json);

        return response;
    }

    private HttpResponse refreshToken(HttpRequest request, LambdaLogger logger) {
        // do we have a code request variable?
        if (request.getQueryStringParameters().containsKey("code")) {
            logger.log("has code param. refreshing token.");
            String code = request.getQueryStringParameters().get("code");
            HttpResponse response = refreshToken(code, logger);

            // if we could successfully get the access token from the code, then redirect back to the main URL
            // if we couldn't then we need to redirect to the Oauth page
            if (!token.hasError()) {
                logger.log("no error after refreshing. returning response.");
                return response;
            }
        }

        // there is no code param. redirect.
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.MOVED_TEMPORARILY);
        response.setHeaders(Map.of("Location", "https://self454-dev-ed.lightning.force.com/services/oauth2/authorize?" +
                        "client_id=3MVG9ux34Ig8G5eor4b9EEsp7EnHtw67aL7CeXtZCGZtEdyRvKpnBALz2aBst4kR4KY8W6pG0K8lWUJFTCj41&" +
                        "redirect_uri=" + ROOT_URL + "&" +
                        "response_type=code"));
        return response;
    }

    public HttpResponse refreshToken(String code, LambdaLogger logger) {
        HttpPost post = new HttpPost("https://login.salesforce.com/services/oauth2/token");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addTextBody("grant_type",  "authorization_code");
        builder.addTextBody("code", code);
        builder.addTextBody("client_id",  "3MVG9ux34Ig8G5eor4b9EEsp7EnHtw67aL7CeXtZCGZtEdyRvKpnBALz2aBst4kR4KY8W6pG0K8lWUJFTCj41");
        builder.addTextBody("client_secret",  "0B121CAB31D2D330E0A9435F044C22EB192233E18CCA21F20FCE4429E279FB68");
        builder.addTextBody("redirect_uri",  ROOT_URL);
        logger.log("going to sent request");


        CloseableHttpClient client = HttpClients.createDefault();
        post.setEntity(builder.build());
        try {
            CloseableHttpResponse response = client.execute(post);
            String bodyAsString = EntityUtils.toString(response.getEntity());
            logger.log("request body is: " + bodyAsString);
            token = gson.fromJson(bodyAsString, SalesforceAccessToken.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setBody("successfully refreshed token; " + token);
        return response;
    }
}