package org.dcsa.api_validator.ovs.v1.portcallevents;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class GetPortCallEventsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();


    @Test
    public void testPortCallEvents(){

        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("ovs/v1/OperationsEvents.json").
                using(jsonSchemaFactory));

    }

}
