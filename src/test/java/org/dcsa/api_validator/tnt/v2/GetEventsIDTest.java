package org.dcsa.api_validator.tnt.v2;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

/*
 * Tests related to the GET /events/{eventID} endpoint
 */

public class GetEventsIDTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();

    //Receives all eventIDs from GET /events, then queries them, validates the ID and that the event has one of the 4 event structures.
    @Test
    public void testEventIds() {
        Reporter.log("Running testEventIds ------------------------------------------------------------------------------------------------------------------------");
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().assertThat().statusCode(200).
                assertThat().body("size()", greaterThanOrEqualTo(0)).
                assertThat().body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json"));

    }
}
