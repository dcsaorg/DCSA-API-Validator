package org.dcsa.api_validator.ovs.v2;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /events endpoint
 */
// TODO: 1. Add reasons and Errors to the 400 bad request tests.
// TODO: 2. ENABLE ALL TESTS

public class GetEventsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();

    @Test(enabled = false)
    public void testTransportCalls() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test(enabled = false)
    public void testNotFound() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI+"/events/80d63706-7b93-4936-84fe-3ef9ef1946f0")
                .then()
                .assertThat().
                statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test(enabled = false)
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
                body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test(enabled = false)
    public void testOperationsEventsQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("eventType", "OPERATIONS").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test(enabled = false)
    public void testTransportEventTypeCodeQueryParam() {

        List<String> TransportEventTypeCodes = getListOfAnyAttribute("transportEventTypeCode");

        for (String TransportEventTypeCode : TransportEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/events/" + TransportEventTypeCode).
                    then().
                    body("transportEventTypeCode", is(TransportEventTypeCode)).
                    assertThat().body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").using(jsonSchemaFactory));
        }

    }
    @Test(enabled = false)
    public void testTransportEventTypeCodeQueryParamFalseFormat() {

        List<String> TransportEventTypeCodes = Arrays.asList("","2z","ABCS");

        for (String TransportEventTypeCode : TransportEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/events/" + TransportEventTypeCode).
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test(enabled = false)
    public void testTransportCallIdsQueryParam() {

        List<String> ids = getListOfAnyAttribute("transportCallID");

        for (String id : ids) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/events/" + id).
                    then().
                    body("transportCallID", is(id)).
                    assertThat().
                    statusCode(200).
                    body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").using(jsonSchemaFactory));
        }

    }

    @Test(enabled = false)
    public void testVesselIMONumberQueryParam() {

        List<String> VesselIMONumbers = getListOfAnyAttribute("vesselIMONumber");

        for (String VesselIMONumber : VesselIMONumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("vesselIMONumber", VesselIMONumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    body("vesselIMONumber", everyItem(equalTo(VesselIMONumber))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").using(jsonSchemaFactory));
        }
    }

    @Test(enabled = false)
    public void testVesselIMONumberQueryParamFalseFormat() {

        List<String> VesselIMONumbers = Arrays.asList("2weqqqqqqwz","ABCtrytytrySwqe");

        for (String VesselIMONumber : VesselIMONumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/events/" + VesselIMONumber).
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test(enabled = false)
    public void testCarrierVoyageNumberQueryParam() {

        List<String> CarrierVoyageNumbers = getListOfAnyAttribute("carrierVoyageNumber");

        for (String CarrierVoyageNumber : CarrierVoyageNumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierVoyageNumber", CarrierVoyageNumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(200).
                    body("carrierVoyageNumber", everyItem(equalTo(CarrierVoyageNumber))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").using(jsonSchemaFactory));
        }
    }


    @Test(enabled = false)
    public void testCarrierVoyageNumberQueryParamFalseFormat() {
        // specification -> maxLength:50
        List<String> CarrierVoyageNumbers =  Arrays.asList("ABCtrytytrySwqeABCtrytytrySwqeABCtryABCtrytytrySwqe" +
                "tytrySwqeABCtrytytrySwqeABCtrytytrySwqeABCtrytytrySwqeABCtrytytrySwqe");

        for (String CarrierVoyageNumber : CarrierVoyageNumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierVoyageNumber", CarrierVoyageNumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test(enabled = false)
    public void testCarrierServiceCodeQueryParam() {

        List<String> CarrierServiceCodes = getListOfAnyAttribute("vesselIMONumber");

        for (String CarrierServiceCode : CarrierServiceCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierServiceCode", CarrierServiceCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    body("carrierServiceCode", everyItem(equalTo(CarrierServiceCode))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").using(jsonSchemaFactory));
        }
    }

    @Test(enabled = false)
    public void testCarrierServiceCodeQueryParamFalseFormat() {

        List<String> CarrierServiceCodes = Arrays.asList("2weqqqqqqwz","ABCtrytytrySwqe");

        for (String CarrierServiceCode : CarrierServiceCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/events/" + CarrierServiceCode).
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test(enabled = false)
    public void testOperationsEventTypeCodeQueryParam() {

        List<String> OperationsEventTypeCodes = getListOfAnyAttribute("transportEventTypeCode");

        for (String OperationsEventTypeCode : OperationsEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/events/" + OperationsEventTypeCode).
                    then().
                    statusCode(200).
                    body("operationsEventTypeCodes", is(OperationsEventTypeCode)).
                    assertThat().body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").using(jsonSchemaFactory));
        }

    }

    @Test(enabled = false)
    public void testOperationsEventTypeCodeQueryParamFalseFormat() {

        List<String> OperationsEventTypeCodes = Arrays.asList("","2z","ABCS");

        for (String OperationsEventTypeCode : OperationsEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    get(Configuration.ROOT_URI + "/events/" + OperationsEventTypeCode).
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test(enabled = false)
    public void testEventCreatedDateTimeQueryParam() {

        List<String> EventCreatedDateTimes = getListOfAnyAttribute("EventCreatedDateTime");

        EventCreatedDateTimes.forEach(EventCreatedDateTime -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("eventCreatedDateTime", EventCreatedDateTime).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    statusCode(200).
                    body("eventCreatedDateTime", everyItem(equalTo(EventCreatedDateTime))).
                    assertThat().body(matchesJsonSchemaInClasspath("ovs/v2/TransportCallSchema.json").using(jsonSchemaFactory));
        });
    }

    // Testing eventCreatedDateTime (gte, gt, lte, lt, eq)
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    // Test eventCreatedDateTime, fails as formatting is wrong.
    @Test(enabled = false)
    public void testEventCreatedDateTimeFalseFormat() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                // Specification -> date-time
                        queryParam("eventCreatedDateTime", "asaa").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Test sorting:ASC (descending) and if order is respected.
    @Test(enabled = false)
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
    @Test(enabled = false)
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
    @Test(enabled = false)
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
    @Test(enabled = false)
    public void testSortDescEventDateTimeQueryParam() {

        List<String> eventCreatedDateTimesSorted = getListOfAnyAttribute("eventDateTime","sort","eventDateTime:DESC");
        int n = eventCreatedDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i-1) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(eventCreatedDateTimesSorted.get(i) , DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) >= 0);
        }
    }

    @Test(enabled = false)
    public void testLimitQueryParam() {

        List<String> Limits = getListOfAnyAttribute("limit");

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
                        queryParam("limit", "0").
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(400);
    }

    // Finds all cursors, and then uses them each of them as a query parameter, and verifies the response
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
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // API-Version as a header parameter, and verifies the response
    @Test(enabled = false)
    public void testAPIVersionQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                header("API-Version", "v2").
                get(Configuration.ROOT_URI + "/events").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    private List getListOfAnyAttribute(String attribute) {
        String json = given().

                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                body().asString();

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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();

        return JsonPath.from(json).getList(attribute).stream().distinct().collect(Collectors.toList());
    }
}
