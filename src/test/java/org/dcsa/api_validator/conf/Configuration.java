package org.dcsa.api_validator.conf;

import io.restassured.http.ContentType;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;

public class Configuration {
    public final static String ROOT_URI = System.getenv("API_ROOT_URI");
    public static String accessToken;

    @BeforeSuite(alwaysRun = true)
    public static void retrieveAccessToken() {
        Reporter.log("Running configuration");
        accessToken = given().with()
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
                .formParam("client_secret", System.getenv("client_secret"))
                .formParam("client_id", System.getenv("client_id"))
                .formParam("grant_type", "client_credentials")
                .formParam("audience", System.getenv("audience"))
                .urlEncodingEnabled(true)
                .when()
                .post(System.getenv("OAuthTokenUri")).jsonPath().getString("access_token");

    }
}
