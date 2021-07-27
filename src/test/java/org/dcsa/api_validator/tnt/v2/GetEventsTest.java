package org.dcsa.api_validator.tnt.v2;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /events endpoint
 */

// -- TODO: 1. ADD error response body for all 400-BadRequests tests
// -- TODO: 2. ENABLE ALL TESTS
// -- TODO: 3. Refactor as done in OVS/V2/Events using getListOfAnyAttribute function.


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

        List<String> equipmentReferences = getListOfAnyAttribute("equipmentReference","eventType","EQUIPMENT");

        for (String equipmentReference : equipmentReferences) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("equipmentReference", equipmentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("equipmentReference", everyItem(equalTo(equipmentReference))).
                    body("collect { it.eventType }", everyItem(equalTo("EQUIPMENT"))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        }
    }

    // Test equipmentReferences, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
     * Test disabled as the maxlength is not respected. Should return 400 according to API specification.
     * TODO: 1. Respect the MaxLength and enable test.
     */
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
    @Test
    public void testShipmentEventTypeCodeQueryParam() {

        List<String> shipmentEventTypeCodes = getListOfAnyAttribute("shipmentEventTypeCode","eventType","SHIPMENT");

        shipmentEventTypeCodes.forEach(shipmentEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("shipmentEventTypeCode", shipmentEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("shipmentEventTypeCode", everyItem(equalTo(shipmentEventTypeCode))).
                    body("collect { it.eventType }", everyItem(equalTo("SHIPMENT"))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test shipmentEventTypeCode, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
     * Test disabled as ENUM is not respected. listed in specification.
     * Should return 400 according to API specification.
     * TODO: 1. Respect the ENUM and enable test.
     * */
    public void testShipmentEventTypeCodeQueryFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> ENUM
                        queryParam("shipmentEventTypeCode", "[ABCDES,........]").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    //Finds all carrierBookingReference, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testCarrierBookingReferenceQueryParam() {

      List<String> carrierBookingReferences = getListOfAnyAttribute("carrierBookingReference");

        carrierBookingReferences.forEach(carrierBookingReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierBookingReference", carrierBookingReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierBookingReference", everyItem(equalTo(carrierBookingReference))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test carrierBookingReference, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
    * Test disabled as code does not respect the 35 max length listed in specification.
    * Should return 400 according to API specification.
     * TODO: 1. Respect the MaxLength and enable test.
    * */
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
    @Test
    public void testBookingReferenceQueryParam() {

        List<String> bookingReferences = getListOfAnyAttribute("bookingReference");

        bookingReferences.forEach(bookingReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("bookingReference", bookingReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("bookingReference", everyItem(equalTo(bookingReference))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    //Finds all transportDocumentID, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testTransportDocumentIDQueryParam() {

        List<String> transportDocumentIDs = getListOfAnyAttribute("transportDocumentID");

        transportDocumentIDs.forEach(transportDocumentID -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentID", transportDocumentID).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentID", everyItem(equalTo(transportDocumentID))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    //Finds all TransportDocumentReference, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testTransportDocumentReferenceQueryParam() {

        List<String> transportDocumentReferences = getListOfAnyAttribute("transportDocumentReference");

        transportDocumentReferences.forEach(transportDocumentReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentReference", transportDocumentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentReference", everyItem(equalTo(transportDocumentReference))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test TransportDocumentReference, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
     * Test disabled as code does not respect the 20 max length listed in specification.
     * Should return 400 according to API specification.
     * TODO: 1. Respect the MaxLength and enable test.
     * */
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

    //Finds all transportDocumentTypeCode, then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    // // MAJOR: RETURNS 500-response, should return 200.
    // TODO: LOOK INTO THIS!
    public void testTransportDocumentTypeCodeQueryParam() {

        List<String> transportDocumentTypeCodes = getListOfAnyAttribute("transportDocumentTypeCode");

        transportDocumentTypeCodes.forEach(transportDocumentTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentTypeCode", transportDocumentTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentTypeCode", everyItem(equalTo(transportDocumentTypeCode))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test TransportDocumentReference, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    // MAJOR: RETURNS 500-response, should return 400.
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
    @Test
    public void testTransportEventTypeCodeQueryParam() {

        List<String> transportEventTypeCodes = getListOfAnyAttribute("transportEventTypeCode","eventType","TRANSPORT");

        transportEventTypeCodes.forEach(transportEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportEventTypeCode", transportEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportEventTypeCode", everyItem(equalTo(transportEventTypeCode))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test TransportDocumentReference, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
     * Test disabled as code does not respect the 20 max length listed in specification.
     * Should return 400 according to API specification.
     * TODO: 1. Respect the MaxLength and enable test.
     * */
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

        List<String> transportCallIDs = getListOfAnyAttribute("transportCallID","eventType","EQUIPMENT,TRANSPORT");

        transportCallIDs.forEach(transportCallID -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportCallID", transportCallID).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportCallID", everyItem(equalTo(transportCallID))).
                    body("transportCall.transportCallID", everyItem(equalTo(transportCallID))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Finds all VesselIMONumber, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testVesselIMONumberQueryParam() {

        List<String> vesselIMONumbers = getListOfAnyAttribute("vesselIMONumber");

        vesselIMONumbers.forEach(vesselIMONumber -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("vesselIMONumber", vesselIMONumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("vesselIMONumber", everyItem(equalTo(vesselIMONumber))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test VesselIMONumber, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
     * Test disabled as code does not respect the 7 max length listed in specification.
     * Should return 400 according to API specification.
     * TODO: 1. Respect the MaxLength and enable test.
     * */
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
    @Test
    public void testCarrierVoyageNumberQueryParam() {

        List<String> carrierVoyageNumbers = getListOfAnyAttribute("carrierVoyageNumber");

        carrierVoyageNumbers.forEach(carrierVoyageNumber -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierVoyageNumber", carrierVoyageNumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierVoyageNumber", everyItem(equalTo(carrierVoyageNumber))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test CarrierVoyageNumber, response should be 400 as formatting is wrong.
    @Test
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
    @Test
    public void testCarrierServiceCodeQueryParam() {

        List<String> carrierServiceCodes = getListOfAnyAttribute("carrierServiceCode");

        carrierServiceCodes.forEach(carrierServiceCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierServiceCode", carrierServiceCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierServiceCode", everyItem(equalTo(carrierServiceCode))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test CarrierServiceCode, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
    * Test disabled as the maxlength is not respected. Should return 400 according to API specification.
    * TODO: 1. Respect the MaxLength and enable test.
     */
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
    @Test
    public void testEquipmentEventTypeCodeQueryParam() {

        List<String> equipmentEventTypeCodes = getListOfAnyAttribute("equipmentEventTypeCode","eventType","EQUIPMENT");

        equipmentEventTypeCodes.forEach(equipmentEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("equipmentEventTypeCode", equipmentEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("equipmentEventTypeCode", everyItem(equalTo(equipmentEventTypeCode))).
                    body("collect { it.eventType }", everyItem(equalTo("EQUIPMENT"))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test equipmentEventTypeCode, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
     * Test disabled as the ENUM is not respected. Should return 400 according to API specification.
     * TODO: 1. Respect ENUM and enable test.
     */
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
    @Test
    public void testEventCreatedDateTimeQueryParam() {

        List<String> eventCreatedDateTimes = getListOfAnyAttribute("eventCreatedDateTime");

        eventCreatedDateTimes.forEach(eventCreatedDateTime -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("eventCreatedDateTime", eventCreatedDateTime).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("eventCreatedDateTime", everyItem(equalTo(eventCreatedDateTime))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test eventCreatedDateTime, all formats.
    @Test
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
                        queryParam("eventCreatedDateTime:gt", "2019-04-01T14:12:56+01:00").
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


    // Test eventCreatedDateTime, response should be 400 as formatting is wrong.
    @Test
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
    @Test
    public void testlimitQueryParam() {

        List<String> limits = getListOfAnyAttribute("limits");

        limits.forEach(limit -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("limit", limit).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("limit", everyItem(equalTo(limit))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test limit, response should be 400 as formatting is wrong.
    @Test(enabled = false)
    /*
     * Test disabled as the MINIMUM is not respected. Should return 400 according to API specification.
     * TODO: 1. Respect MINIMUM and enable test.
     */
    public void testlimitFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> minimum: 1
                        queryParam("limit", "0").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all cursor, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testcursorQueryParam() {

        List<String> cursors = getListOfAnyAttribute("cursor");

        cursors.forEach(cursor -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("cursor", cursor).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("cursor", everyItem(equalTo(cursor))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test sorting:ASC (descending) and if order is respected.
    @Test
    public void testSortAscEventCreatedDateTimeQueryParam() {

        List<String> eventCreatedDateTimesSorted = getListOfAnyAttribute("eventCreatedDateTime","sort","eventCreatedDateTime:ASC");
        int n = eventCreatedDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i-1) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) <= 0);
        }
    }

    // Test sorting:DESC (descending) and if order is respected.
    @Test
    public void testSortDescEventCreatedDateTimeQueryParam() {

        List<String> eventCreatedDateTimesSorted = getListOfAnyAttribute("eventCreatedDateTime","sort","eventCreatedDateTime:DESC");
        int n = eventCreatedDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i-1) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) >= 0);
        }
    }

    // Test sorting:ASC (descending) and if order is respected.
    @Test
    public void testSortAscEventDateTimeQueryParam() {

        List<String> eventCreatedDateTimesSorted = getListOfAnyAttribute("eventDateTime","sort","eventDateTime:ASC");
        int n = eventCreatedDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i-1) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) <= 0);
        }
    }

    // Test sorting:DESC (descending) and if order is respected.
    @Test
    public void testSortDescEventDateTimeQueryParam() {

        List<String> eventCreatedDateTimesSorted = getListOfAnyAttribute("eventDateTime","sort","eventDateTime:DESC");
        int n = eventCreatedDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i-1) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) >= 0);
        }
    }
    
    // Finds all API-Version, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testAPIVersionQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                header("API-Version", "2").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    private List getListOfAnyAttribute(String attribute) {
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

        return JsonPath.from(json).getList(attribute).stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    private List getListOfAnyAttribute(String attribute, String queryParam, String queryParamValue) {
        String json = given().

                auth().
                oauth2(Configuration.accessToken).
                queryParam(queryParam, queryParamValue).
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();

        return JsonPath.from(json).getList(attribute).stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }
}
