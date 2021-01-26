package org.dcsa.api_validator.conf;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;

public class Configuration {
    public final static String ROOT_URI = System.getenv("API_ROOT_URI");
    public final static String CALLBACK_URI = System.getenv("CALLBACK_URI");
    public static String accessToken;
    private static String client_secret= System.getenv("client_secret");
    private static String client_id= System.getenv("client_id");
    private static String audience= System.getenv("audience");

    @BeforeSuite(alwaysRun = true)
    public static void enableLogging() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeSuite(alwaysRun = true)
    public static void retrieveAccessToken() {
        Reporter.log("Running configuration");
        if (client_secret==null || client_id==null || audience == null)
            accessToken="AuthDisabled"; //We set the accessToken to this string, so the tests don't complain about an empty accessToken. This is OK, if auth is disabled, it doesn't matter that the accessToken is bogus
        else {
            accessToken = given().with()
                    .contentType(ContentType.URLENC.withCharset("UTF-8"))
                    .formParam("client_secret", client_secret)
                    .formParam("client_id", client_id)
                    .formParam("grant_type", "client_credentials")
                    .formParam("audience", audience)
                    .urlEncodingEnabled(true)
                    .when()
                    .post(System.getenv("OAuthTokenUri")).jsonPath().getString("access_token");
        }
    }
}
