package org.dcsa.api_validator.conf;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

import static io.restassured.RestAssured.given;

public class Configuration {
    public final static String ROOT_URI = System.getenv("API_ROOT_URI");
    public final static String CALLBACK_URI = System.getenv("CALLBACK_URI");
    private final static int CALLBACK_LISTEN_PORT = readPortFromEnv("CALLBACK_LISTEN_PORT", "4567");
    public static final boolean MAY_USE_POST_EVENTS_ENDPOINT = getEnvBool("DCSA_API_VALIDATOR_MAY_USE_POST_EVENTS_ENDPOINT", false);
    public static String accessToken;
    private static String client_secret= System.getenv("client_secret");
    private static String client_id= System.getenv("client_id");
    private static String audience= System.getenv("audience");

    public static int getCallbackListenPort() {
        if (CALLBACK_LISTEN_PORT == -1) {
            throw new IllegalStateException("CALLBACK_LISTEN_PORT was explicitly disabled, but the test needs it");
        }
        return CALLBACK_LISTEN_PORT;
    }

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

    private static boolean getEnvBool(String name, boolean defaultValue) {
        String envValue = System.getenv(name);
        boolean value = defaultValue;
        if (envValue != null) {
            value = Boolean.parseBoolean(envValue);
        }
        return value;
    }

    private static int readPortFromEnv(String name, String defaultText) {
        String portText = System.getenv(name);
        int p = -1;
        if (portText == null) {
            portText = defaultText;
        }
        if (portText.equalsIgnoreCase("NONE")) {
            return -1;
        }
        try {
            p = Integer.parseInt(portText);
            if (p < 1 || p > 65535) {
                System.err.println("Invalid port number specified in environment variable " + name
                        + ": It must be in the range 1-65535 but was " + p);
                System.exit(1);
            }

        } catch (NumberFormatException e) {
            System.err.println("Invalid port number specified in environment variable " + name
                    + ": Could not be parsed as int: " + e.getLocalizedMessage());
            System.exit(1);
        }
        return p;
    }
}
