package org.dcsa.api_validator.tnt.v2;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.List;
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

        List<String> ShipmentEventTypeCodes = getListOfAnyAttribute("shipmentEventTypeCode","eventType","SHIPMENT");

        ShipmentEventTypeCodes.forEach(ShipmentEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("shipmentEventTypeCode", ShipmentEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("shipmentEventTypeCode", everyItem(equalTo(ShipmentEventTypeCode))).
                    body("collect { it.eventType }", everyItem(equalTo("SHIPMENT"))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Test shipmentEventTypeCode, fails as formatting is wrong.
    @Test(enabled = false)
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
    @Test(enabled = false)
    public void testCarrierBookingReferenceQueryParam() {

      List<String> CarrierBookingReferences = getListOfAnyAttribute("carrierBookingReference");

      CarrierBookingReferences.forEach(CarrierBookingReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierBookingReference", CarrierBookingReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierBookingReference", everyItem(equalTo(CarrierBookingReference))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> BookingReferences = getListOfAnyAttribute("bookingReference");

        BookingReferences.forEach(BookingReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("bookingReference", BookingReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("bookingReference", everyItem(equalTo(BookingReference))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    //Finds all transportDocumentID, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testTransportDocumentIDQueryParam() {

        List<String> TransportDocumentIDs = getListOfAnyAttribute("transportDocumentID");

        TransportDocumentIDs.forEach(TransportDocumentID -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentID", TransportDocumentID).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentID", everyItem(equalTo(TransportDocumentID))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    //Finds all TransportDocumentReference, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testTransportDocumentReferenceQueryParam() {

        List<String> TransportDocumentReferences = getListOfAnyAttribute("transportDocumentReference");

        TransportDocumentReferences.forEach(TransportDocumentReference -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentReference", TransportDocumentReference).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentReference", everyItem(equalTo(TransportDocumentReference))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> TransportDocumentTypeCodes = getListOfAnyAttribute("transportDocumentTypeCode");

        TransportDocumentTypeCodes.forEach(TransportDocumentTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportDocumentTypeCode", TransportDocumentTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportDocumentTypeCode", everyItem(equalTo(TransportDocumentTypeCode))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> TransportEventTypeCodes = getListOfAnyAttribute("transportEventTypeCode","eventType","TRANSPORT");

        TransportEventTypeCodes.forEach(TransportEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportEventTypeCode", TransportEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportEventTypeCode", everyItem(equalTo(TransportEventTypeCode))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> TransportCallIDs = getListOfAnyAttribute("transportCallID","eventType","EQUIPMENT,TRANSPORT");

        TransportCallIDs.forEach(TransportCallID -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportCallID", TransportCallID).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("transportCallID", everyItem(equalTo(TransportCallID))).
                    body("transportCall.transportCallID", everyItem(equalTo(TransportCallID))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // Finds all VesselIMONumber, and then uses them each of them as a query parameter, and verifies the response
    @Test(enabled = false)
    public void testVesselIMONumberQueryParam() {

        List<String> VesselIMONumbers = getListOfAnyAttribute("vesselIMONumber");

        VesselIMONumbers.forEach(VesselIMONumber -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("vesselIMONumber", VesselIMONumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("vesselIMONumber", everyItem(equalTo(VesselIMONumber))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> CarrierVoyageNumbers = getListOfAnyAttribute("carrierVoyageNumber");

        CarrierVoyageNumbers.forEach(CarrierVoyageNumber -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierVoyageNumber", CarrierVoyageNumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierVoyageNumber", everyItem(equalTo(CarrierVoyageNumber))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> CarrierServiceCodes = getListOfAnyAttribute("carrierServiceCode");

        CarrierServiceCodes.forEach(CarrierServiceCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierServiceCode", CarrierServiceCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("carrierServiceCode", everyItem(equalTo(CarrierServiceCode))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> EquipmentEventTypeCodes = getListOfAnyAttribute("equipmentEventTypeCode","eventType","EQUIPMENT");

        EquipmentEventTypeCodes.forEach(EquipmentEventTypeCode -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("equipmentEventTypeCode", EquipmentEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("equipmentEventTypeCode", everyItem(equalTo(EquipmentEventTypeCode))).
                    body("collect { it.eventType }", everyItem(equalTo("EQUIPMENT"))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> EventCreatedDateTimes = getListOfAnyAttribute("eventCreatedDateTime");

        EventCreatedDateTimes.forEach(EventCreatedDateTime -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("eventCreatedDateTime", EventCreatedDateTime).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("eventCreatedDateTime", everyItem(equalTo(EventCreatedDateTime))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> Limits = getListOfAnyAttribute("limits");

        Limits.forEach(Limit -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("limit", Limit).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("limit", everyItem(equalTo(Limit))).
                    body(matchesJsonSchemaInClasspath("tnt/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
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

        List<String> Cursors = getListOfAnyAttribute("cursor");

        Cursors.forEach(Cursor -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("cursor", Cursor).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("cursor", everyItem(equalTo(Cursor))).
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

        return JsonPath.from(json).getList(attribute).stream().distinct().collect(Collectors.toList());
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

        return JsonPath.from(json).getList(attribute).stream().distinct().collect(Collectors.toList());
    }
}
