package org.dcsa.api_validator.tnt.v2;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /events endpoint
 */

// -- TODO: 1. ADD error response body for all 400-BadRequests tests
// -- TODO: 2. ENABLE ALL TESTS

public class GetEventsTest {

    // -- TODO: SHARE THIS IN CLASS SO THAT TEST CAN RUN SEPARATELY
    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();


    @Test
    public void testEvents() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
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
                body("size()", greaterThanOrEqualTo(0)).
                body("collect { it.eventType }", everyItem(equalTo("EQUIPMENT"))).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
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
                body("size()", greaterThanOrEqualTo(0)).
                body("collect { it.eventType }", everyItem(equalTo("TRANSPORT"))).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
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
                body("size()", greaterThanOrEqualTo(0)).
                body("collect { it.eventType }", everyItem(equalTo("SHIPMENT"))).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }


    //Finds all equipmentReferences, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testEquipmentReferenceQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "EQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> equipmentReferences = JsonPath.from(json).<String>getList("equipmentReference").stream().distinct().collect(Collectors.toList());
        for (String equipmentReference : equipmentReferences) {

            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("equipmentReference", equipmentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("equipmentReference", everyItem(equalTo(equipmentReference))).
                    body("collect { it.eventType }", everyItem(equalTo("EQUIPMENT")));
        }
    }

    // Test equipmentReferences, fails as formatting is wrong.
    @Test(enabled = false)
    public void testEquipmentReferenceQueryFalseFormat() {
            given().
                auth().
                oauth2(Configuration.accessToken).
                 // Specification -> maxLength: 15
                queryParam("equipmentReference", "dsfdsAPZU4812090APZU4812090APZU4812090").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    //Finds all ShipmentEventTypeCode, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testShipmentEventTypeCodeQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "SHIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body("collect { it.eventType }", everyItem(equalTo("SHIPMENT"))).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> ShipmentEventTypeCodes = JsonPath.from(json).<String>getList("shipmentEventTypeCode").stream().distinct().collect(Collectors.toList());
        ShipmentEventTypeCodes.forEach(ShipmentEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("shipmentEventTypeCode", ShipmentEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("shipmentEventTypeCode", everyItem(equalTo(ShipmentEventTypeCode))).
                    body("collect { it.eventType }", everyItem(equalTo("SHIPMENT")));
        });
    }

    // Test shipmentEventTypeCode, fails as formatting is wrong.
    @Test(enabled = false)
    public void testShipmentEventTypeCodeQueryFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: 15
                        queryParam("shipmentEventTypeCode", "[ABCDES,........]").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    //Finds all carrierBookingReference, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testCarrierBookingReferenceQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> CarrierBookingReferences = JsonPath.from(json).<String>getList("carrierBookingReference").stream().distinct().collect(Collectors.toList());
        CarrierBookingReferences.forEach(CarrierBookingReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierBookingReference", CarrierBookingReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierBookingReference", everyItem(equalTo(CarrierBookingReference)));

        });
    }

    // Test carrierBookingReference, fails as formatting is wrong.
    @Test(enabled = false)
    public void testCarrierBookingReferenceQueryFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: 35
                        queryParam("carrierBookingReference", "ABC709951ABC709951ABC709951ABC709951564").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    //Finds all BookingReference (DEPRECATED), and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testBookingReferenceQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> BookingReferences = JsonPath.from(json).<String>getList("bookingReference").stream().distinct().collect(Collectors.toList());
        BookingReferences.forEach(BookingReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("bookingReference", BookingReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("bookingReference", everyItem(equalTo(BookingReference)));

        });
    }

    //Finds all transportDocumentID, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testTransportDocumentIDQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> TransportDocumentIDs = JsonPath.from(json).<String>getList("transportDocumentID").stream().distinct().collect(Collectors.toList());
        TransportDocumentIDs.forEach(TransportDocumentID -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentID", TransportDocumentID).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentID", everyItem(equalTo(TransportDocumentID)));

        });
    }

    //Finds all TransportDocumentReference, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testTransportDocumentReferenceQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> TransportDocumentReferences = JsonPath.from(json).<String>getList("transportDocumentReference").stream().distinct().collect(Collectors.toList());
        TransportDocumentReferences.forEach(TransportDocumentReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentReference", TransportDocumentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentReference", everyItem(equalTo(TransportDocumentReference)));

        });
    }

    // Test TransportDocumentReference, fails as formatting is wrong.
    @Test(enabled = false)
    public void testTransportDocumentReferenceQueryFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: 20
                        queryParam("carrierBookingReference", "ABC709951ABC709951ABC709951ABC709951564").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    //Finds all transportDocumentTypeCode, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testTransportDocumentTypeCodeQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> TransportDocumentTypeCodes = JsonPath.from(json).<String>getList("transportDocumentTypeCode").stream().distinct().collect(Collectors.toList());
        TransportDocumentTypeCodes.forEach(TransportDocumentTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentTypeCode", TransportDocumentTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentTypeCode", everyItem(equalTo(TransportDocumentTypeCode)));

        });
    }

    // Test TransportDocumentReference, fails as formatting is wrong.
    @Test(enabled = false)
    public void testTransportDocumentTypeCodeFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: ENUM(3)
                        queryParam("transportDocumentTypeCode", "ABC7099").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    //Finds all TransportEventTypeCode, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testTransportEventTypeCodeQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "TRANSPORT").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> TransportEventTypeCodes = JsonPath.from(json).<String>getList("transportEventTypeCode").stream().distinct().collect(Collectors.toList());
        TransportEventTypeCodes.forEach(TransportEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportEventTypeCode", TransportEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportEventTypeCode", everyItem(equalTo(TransportEventTypeCode)));

        });
    }

    // Test TransportDocumentReference, fails as formatting is wrong.
    @Test(enabled = false)
    public void testTransportEventTypeCodeFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: ENUM(4)
                        queryParam("transportEventTypeCode", "ABC7099").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all TransportCallID, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testTransportCallIDQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "EQUIPMENT,TRANSPORT").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        System.out.println("sax");
        System.out.println(json);
        List<String> TransportCallIDs = JsonPath.from(json).<String>getList("transportCallID").stream().distinct().collect(Collectors.toList());
        System.out.println("sa");
        System.out.println(TransportCallIDs);
        TransportCallIDs.forEach(TransportCallID -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportCallID", TransportCallID).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportCallID", everyItem(equalTo(TransportCallID))).
                    body("transportCall.transportCallID", everyItem(equalTo(TransportCallID)));

        });
    }

    // Finds all VesselIMONumber, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testVesselIMONumberQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> VesselIMONumbers = JsonPath.from(json).<String>getList("vesselIMONumber").stream().distinct().collect(Collectors.toList());
        VesselIMONumbers.forEach(VesselIMONumber -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("vesselIMONumber", VesselIMONumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("vesselIMONumber", everyItem(equalTo(VesselIMONumber)));

        });
    }

    // Test VesselIMONumber, fails as formatting is wrong.
    @Test(enabled = false)
    public void testVesselIMONumberFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: 7
                        queryParam("vesselIMONumber", "ABsC70sss99").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all CarrierVoyageNumber, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testCarrierVoyageNumberQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> CarrierVoyageNumbers = JsonPath.from(json).<String>getList("carrierVoyageNumber").stream().distinct().collect(Collectors.toList());
        CarrierVoyageNumbers.forEach(CarrierVoyageNumber -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierVoyageNumber", CarrierVoyageNumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierVoyageNumber", everyItem(equalTo(CarrierVoyageNumber)));

        });
    }

    // Test CarrierVoyageNumber, fails as formatting is wrong.
    @Test(enabled = false)
    public void testCarrierVoyageNumberFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: 50
                        queryParam("carrierVoyageNumber", "").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all CarrierServiceCode, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testCarrierServiceCodeQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> CarrierServiceCodes = JsonPath.from(json).<String>getList("carrierServiceCode").stream().distinct().collect(Collectors.toList());
        CarrierServiceCodes.forEach(CarrierServiceCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierServiceCode", CarrierServiceCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierServiceCode", everyItem(equalTo(CarrierServiceCode)));

        });
    }

    // Test CarrierServiceCode, fails as formatting is wrong.
    @Test(enabled = false)
    public void testCarrierServiceCodeFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: 5
                        queryParam("carrierServiceCode", "ABsC70sss99").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    //Finds all equipmentEventTypeCode, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testEquipmentEventTypeCodeQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "EQUIPMENT").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> EquipmentEventTypeCodes = JsonPath.from(json).<String>getList("equipmentEventTypeCode").stream().distinct().collect(Collectors.toList());
        EquipmentEventTypeCodes.forEach(EquipmentEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("equipmentEventTypeCode", EquipmentEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("equipmentEventTypeCode", everyItem(equalTo(EquipmentEventTypeCode))).
                    body("collect { it.eventType }", everyItem(equalTo("EQUIPMENT")));
        });
    }

    // Test equipmentEventTypeCode, fails as formatting is wrong.
    @Test(enabled = false)
    public void testEquipmentEventTypeCodeFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> ENUM
                        queryParam("equipmentEventTypeCode", "[ABCDES,........]").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all EventCreatedDateTime, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testEventCreatedDateTimeQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> EventCreatedDateTimes = JsonPath.from(json).<String>getList("eventCreatedDateTime").stream().distinct().collect(Collectors.toList());
        EventCreatedDateTimes.forEach(EventCreatedDateTime -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("eventCreatedDateTime", EventCreatedDateTime).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("eventCreatedDateTime", everyItem(equalTo(EventCreatedDateTime)));

        });
    }

    // Test eventCreatedDateTime, all formats.
    @Test(enabled = false)
    public void testEventCreatedDateTimeFormats() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                        queryParam("eventCreatedDateTime:gte", "2019-04-01T14:12:56+01:00").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
        given().
                auth().
                oauth2(Configuration.accessToken).
                        queryParam("eventCreatedDateTime:gt", "=2019-04-01T14:12:56+01:00").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
        given().
                auth().
                oauth2(Configuration.accessToken).
                        queryParam("eventCreatedDateTime:lte", "2021-08-01T14:12:56+01:00").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
        given().
                auth().
                oauth2(Configuration.accessToken).
                        queryParam("eventCreatedDateTime:lt", "2021-04-01T14:12:56+01:00").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
        given().
                auth().
                oauth2(Configuration.accessToken).
                        queryParam("eventCreatedDateTime:eq", "2021-07-08T10:44:42.08724+02:00").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }


    // Test eventCreatedDateTime, fails as formatting is wrong.
    @Test(enabled = false)
    public void testEventCreatedDateTimeFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> maxLength: 5
                        queryParam("eventCreatedDateTime", "asaa").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all limit, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testLimitQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> Limits = JsonPath.from(json).<String>getList("limit").stream().distinct().collect(Collectors.toList());
        Limits.forEach(Limit -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("limit", Limit).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("limit", everyItem(equalTo(Limit)));

        });
    }

    // Test limit, fails as formatting is wrong.
    @Test(enabled = false)
    public void testLimitFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> minimum: 1
                        queryParam("eventCreatedDateTime", "0").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all Cursor, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testCursorQueryParam() {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();
        List<String> Cursors = JsonPath.from(json).<String>getList("cursor").stream().distinct().collect(Collectors.toList());
        Cursors.forEach(Cursor -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("cursor", Cursor).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("cursor", everyItem(equalTo(Cursor)));

        });
    }

    // Finds all API-Version, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testAPIVersionQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("API-Version", "v2").
                get(Configuration.ROOT_URI + "v2/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

}
