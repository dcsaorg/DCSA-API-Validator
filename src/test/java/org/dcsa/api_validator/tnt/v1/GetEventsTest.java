package org.dcsa.api_validator.tnt.v1;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /events endpoint
 */

public class GetEventsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();
    ;


    @Test
    public void testEvents() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testEquipmentEventsQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "EQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/EquipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    //Finds all equipmentReferences, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testEquipmentReferenceQueryParam() {
        String json = given().

                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "EQUIPMENT,TRANSPORTEQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                extract().asString();

        List<String> equipmentReferences = JsonPath.from(json).getList("events.equipmentReference");
        for (String equipmentReference : equipmentReferences) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("equipmentReference", equipmentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("events.equipmentReference", everyItem(equalTo(equipmentReference)));
        }
    }

    @Test
    public void testTransportEventsQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "TRANSPORT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/TransportEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testShipmentEventsQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "SHIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/ShipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testTransportEquipmentEventsQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "TRANSPORTEQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/TransportEquipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testBillOfLadingNumberEventsQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("billOfLading", "BL32147109").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/ShipmentEventsSchema.json").
                        using(jsonSchemaFactory)).
                body("events.size()", greaterThan(0));

        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("billOfLading", "BL32147109").
                queryParam("eventType", "SHIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/ShipmentEventsSchema.json").
                        using(jsonSchemaFactory)).
                body("events.size()", greaterThan(0));

        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("billOfLading", "BL32147109").
                queryParam("eventType", "SHIPMENT,TRANSPORT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v1/ShipmentEventsSchema.json").
                        using(jsonSchemaFactory)).
                // eventType is an "OR"-style filter, so we should still see SHIPMENT events
                body("events.size()", greaterThan(0));

        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("billOfLading", "BL32147109").
                queryParam("eventType", "TRANSPORT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                // TRANSPORT events does not have a billOfLading number, so there cannot be any matching events.
                body("events.size()", equalTo(0));

        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("billOfLading", "AA32147122").
                queryParam("eventType", "SHIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                // Valid billOfLading id, but it has no events associated with it.
                body("events.size()", equalTo(0));
    }
}
