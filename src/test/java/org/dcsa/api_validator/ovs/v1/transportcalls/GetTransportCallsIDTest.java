package org.dcsa.api_validator.ovs.v1.transportcalls;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;


public class GetTransportCallsIDTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();

    @Test
    public void testTransportCallIds() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-calls").
                body().asString();

        List<String> ids = JsonPath.from(json).getList("transportCallID");
        for (String id : ids) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/transport-calls/" + id).
                    then().
                    body("transportCallID", is(id)).
                    assertThat().body(matchesJsonSchemaInClasspath("ovs/v1/TransportCallSchema.json").using(jsonSchemaFactory));
        }

    }

    //Ensures a bad request is thrown when the path parameter is not an UUID. If API implementers don't use UUIDs, then this test should be removed
    @Test
    public void testIncorrectUUID() {
        given().
                auth().
                oauth2(Configuration.accessToken).
        get(Configuration.ROOT_URI+"/transport-calls/NOT-AN-UUID")
                .then()
                .assertThat().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void testNotFoundUUID() {
        given().
                auth().
                oauth2(Configuration.accessToken).
        get(Configuration.ROOT_URI+"/transport-calls/80d63706-7b93-4936-84fe-3ef9ef1946f0")
                .then()
                .assertThat().
                statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
