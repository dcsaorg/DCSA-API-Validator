package org.dcsa.api_validator.conf;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.json.JSONObject;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;

public class Configuration {
    public final static String ROOT_URI = "http://localhost:9090";
    public static String accessToken;

    @BeforeSuite
    public static void retrieveAccessToken() {

        accessToken = given().with()
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
                .formParam("client_secret", System.getenv("client_secret"))
                .formParam("client_id", System.getenv("client_id"))
                .formParam("grant_type", "client_credentials")
                .formParam("audience", System.getenv("audience"))
//                        .body("grant_type=client_credentials&client_id="+System.getenv("client_id")+"&client_secret="+System.getenv("client_secret")+"&audience=localhost")
                .urlEncodingEnabled(true)
                .when()
                .post(System.getenv("OAuthTokenUri")).jsonPath().getString("access_token");


        System.out.println("Oauth Token" + accessToken);

    }
}
