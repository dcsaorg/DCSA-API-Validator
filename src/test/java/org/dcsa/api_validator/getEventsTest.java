package org.dcsa.api_validator;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /events endpoint
 */

public class getEventsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();
    ;


    @Test
    public void testEvents() {
        get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testEquipmentEventsQueryParam() {
        given().
                queryParam("eventType", "EQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("EquipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    //Finds all equipmentReferences, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testEquipmentReferenceQueryParam() {
        String json = given().
                queryParam("eventType", "EQUIPMENT,TRANSPORTEQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                body().asString();

        List<String> equipmentReferences = JsonPath.from(json).getList("events.equipmentReference");
        for (String equipmentReference : equipmentReferences) {
            given().
                    queryParam("equipmentReference", equipmentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    body("events.equipmentReference", everyItem(equalTo(equipmentReference)));
        }
    }

    @Test
    public void testTransportEventsQueryParam() {
        given().
                queryParam("eventType", "TRANSPORT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("TransportEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testShipmentEventsQueryParam() {
        given().
                queryParam("eventType", "SHIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("ShipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testTransportEquipmentEventsQueryParam() {
        given().
                queryParam("eventType", "TRANSPORTEQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("TransportEquipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }
}
