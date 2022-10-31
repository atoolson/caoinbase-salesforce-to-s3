# salesforce-to-s3
 
This repo contains code that will copy Salesforce case data from the [configured Salesforce instance](https://self454-dev-ed.lightning.force.com/)
via a [Connected App](https://self454-dev-ed.lightning.force.com/lightning/setup/ConnectedApplication/page?address=%2Fapp%2Fmgmt%2Fforceconnectedapps%2FforceAppDetail.apexp%3FretURL%3D%252Fsetup%252FNavigationMenus%252Fhome%26connectedAppId%3D0H4Dn000000HO96KAG%26appLayout%3Dsetup%26tour%3D%26sfdcIFrameOrigin%3Dhttps%253A%252F%252Fself454-dev-ed.lightning.force.com%26sfdcIFrameHost%3Dweb%26nonce%3D3aed66839b997115235abe336eb715e8e17b1485503f7756ea15098f1fa83010%26ltn_app_id%3D06mDn000001CdNyIAK%26clc%3D1%26id%3D0CiDn000000TNQU)
to an AWS S3 bucket.

## Structure

### Authorization

I initially set out to use [OAuth 2.0 Client Credentials Flow for Server-to-Server Integration](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_client_credentials_flow.htm&type=5)
as that fit the problem spec better, but unfortunately that option [is not available](https://developer.salesforce.com/forums/?id=9060G000000I38vQAC)
in the Developer edition of Salesforce. So I opted to use [Web Server Flow for Web App Integration](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_web_server_flow.htm&type=5)
which introduced quite a bit of authorization logic.

When accessing the main endpoint you must have a `caseId` URL parameter. If an authorization token hasn't been retrieved
then the user is redirected to the Salesforce sign in portal, asked to authenticate and authorize the app, and then
returned to our app with an `authorization code` in the URL parameters. That code is used by our application to retrieve
an `access token` and store that token in memory for future requests. Once the access token is retrieved the user is 
redirected back to their initial URL, which will pull the details for the provided `caseId`, upload them to the S3 
bucket, and return then as a JSON payload.

### Code Layout

The code is broken up into three main parts:

* **Exceptions**  
There is only one exception, which is used when the app has a bad access token. If this exception is thrown then a new
access token is requested.
* **Models**  
There are just POJOs used to model the data structures returned or expected by the different APIs used in this application.
Much of the boilerplate constructors/getters/setters/builders are removed by using [Lombok](https://projectlombok.org/). 
We could have probably omitted these classes had we used the Salesforce Java SDK and Lambda HTTP library, but had we used
those then this exercise would have been trivially simple and not demonstrated much coding ability.
* **Services**  
This is where the integration logic lives. The `CaseToS3Controller` class receives the HTTP request, ensures an access
token exists, retrieves the case details, and uploads them to S3. The `SalesforceCaseDetailsService` is used to get those
case details, and the `S3Service` does the actual S3 uploading. `ConfigService` has a handful of configuration values.

## Examples

Clicking on any of the following Case Ids will execute the lambda function to copy that case to S3. You can then verify
the data by going to https://coinbase-salesforce-to-s3.s3.us-west-1.amazonaws.com/caseDetails/<CASE_ID>.json

* [500Dn000002JPo2IAG](https://ne2xddjtut7joxmjocltodf2c40jngkp.lambda-url.us-west-1.on.aws/?caseId=)
* [500Dn000002JPoGIAW](https://ne2xddjtut7joxmjocltodf2c40jngkp.lambda-url.us-west-1.on.aws/?caseId=)
* [500Dn000002JPoOIAW](https://ne2xddjtut7joxmjocltodf2c40jngkp.lambda-url.us-west-1.on.aws/?caseId=)

## How I'd Improve This App

1. Use dependency injection  
   Using dependency injection would allow better composition and separation of concerns, but it would have required using
   another library in the application. I opted to keep the code simple, but if the scope had increased much more I
   would trade some simplicity for scalability.

1. Use a better HTTP framework  
   I didn't want to create an API Gateway on top of my Lambda function as that would mean more components to manage, so I
   went with a simple `Function URL`. I had to provide a bit of my how HTTP logic (`HttpRequest` and `HttpResponse`) but for
   what I was doing I think that was a fair trade. Again, if there was more scope to this project then I would have gone
   with a more robust HTTP solution (something like [Serverless](https://www.serverless.com/)).

1. Use different authorization scheme
   It was unfortunate that the Client Credentials Flow wasn't available for my Salesforce instance since it would have been
   a better fit and simpler to implement. If this were a real project I would have reached out to Salesforce Customer
   Service to see if I could have the `API Only` option enabled.

1. Improved Security  
   This application caches the access token across requests which is a large security risk. Since this is a small sample
   application I didn't think it worth implementing security, but, again, in a real application I would use a different
   security model altogether. And the S3 bucket currently has reads enabled globally, which I wouldn't do in a real 
   environment.

1. Use the [Salesforce SDK](https://developer.salesforce.com/docs/marketing/marketing-cloud/guide/getting-started-with-the-java-sdk.html)  
   This would have made the whole project trivially easy, which I think would defeat the purpose of the exercise, so I
   implemented the authentication and REST APIs by hand.

1. Better testing  
   The tests that do exist in this repo were used to execute logic without deploying the code to AWS. There don't assert
   any results nor prevent regressions or even mock out dependency calls. In a real project these tests would be much 
   more robust at both of those tasks.

1. Error Handling (case not found)
   Right now there is error handling for bad access tokens, but many other error scenarios (network connectivity issues,
   invalid CaseId, etc) are not considered. In a real project I would have coded around each of these issues.

1. Bulk API  
   The prompt called out that the API should store details for a single case into S3. If I were building this for real
   world use then I would ask the partner team what they would use this for and if it made sense to support bulk operations.