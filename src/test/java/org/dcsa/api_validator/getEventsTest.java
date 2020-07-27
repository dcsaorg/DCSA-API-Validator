package org.dcsa.api_validator;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.get;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/*
 * Tests related to the GET /events endpoint
 */

public class getEventsTest {

    @Test
    public void testEvents() {

        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();


        get(Configuration.ROOT_URI + "/events").then().assertThat().body(matchesJsonSchemaInClasspath("EventsSchema.json").using(jsonSchemaFactory));


    }
}
