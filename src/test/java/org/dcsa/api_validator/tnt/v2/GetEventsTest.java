package org.dcsa.api_validator.tnt.v2;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

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
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
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
                body(matchesJsonSchemaInClasspath("tnt/v2/EquipmentEventsSchema.json").
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
                extract().body().asString();

        List<String> equipmentReferences = JsonPath.from(json).getList("equipmentReference");
        for (String equipmentReference : equipmentReferences) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("equipmentReference", equipmentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("equipmentReference", everyItem(equalTo(equipmentReference)));
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
                body(matchesJsonSchemaInClasspath("tnt/v2/TransportEventsSchema.json").
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
                body(matchesJsonSchemaInClasspath("tnt/v2/ShipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testEventQueryParamTransportDocumentID() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Just to ensure it does not crash; we do not test data with that ID though
                queryParam("transportDocumentID", "80d63706-7b93-4936-84fe-3ef9ef1946f0").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("tnt/v2/ShipmentEventsSchema.json").
                        using(jsonSchemaFactory));
    }

}
