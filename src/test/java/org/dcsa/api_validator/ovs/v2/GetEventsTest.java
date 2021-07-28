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
import java.util.Objects;
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

    @Test
    public void testTransportCalls() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                then().
                assertThat().
                statusCode(200).
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testNotFound() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI+"/events/80d63706-7b93-4936-84fe-3ef9ef1946f0")
                .then()
                .assertThat().
                statusCode(HttpStatus.SC_NOT_FOUND);
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    @Test
    public void testTransportEventTypeCodeQueryParam() {

        List<String> transportEventTypeCodes = getListOfAnyAttribute("transportEventTypeCode");

        for (String transportEventTypeCode : transportEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportEventTypeCode", transportEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(200).
                    body("transportEventTypeCode", is(transportEventTypeCode)).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").using(jsonSchemaFactory));
        }

    }
    @Test
    public void testTransportEventTypeCodeQueryParamFalseFormat() {

        List<String> transportEventTypeCodes = Arrays.asList("","2z","ABCS");

        for (String transportEventTypeCode : transportEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportEventTypeCode", transportEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test
    public void testTransportCallIdsQueryParam() {

        List<String> ids = getListOfAnyAttribute("transportCallID");

        for (String id : ids) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("transportCallID", id).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    body("transportCallID", is(id)).
                    assertThat().
                    statusCode(200).
                    body("vesselIMONumber", everyItem(equalTo(id))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").using(jsonSchemaFactory));
        }

    }

    @Test
    public void testVesselIMONumberQueryParam() {

        List<String> vesselIMONumbers = getListOfAnyAttribute("vesselIMONumber");

        for (String vesselIMONumber : vesselIMONumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("vesselIMONumber", vesselIMONumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    body("vesselIMONumber", everyItem(equalTo(vesselIMONumber))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").using(jsonSchemaFactory));
        }
    }

    @Test
    public void testVesselIMONumberQueryParamFalseFormat() {

        List<String> vesselIMONumbers = Arrays.asList("2weqqqqqqwz","ABCtrytytrySwqe");

        for (String vesselIMONumber : vesselIMONumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("vesselIMONumber",vesselIMONumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test
    public void testCarrierVoyageNumberQueryParam() {

        List<String> carrierVoyageNumbers = getListOfAnyAttribute("carrierVoyageNumber");

        for (String carrierVoyageNumber : carrierVoyageNumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierVoyageNumber", carrierVoyageNumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(200).
                    body("carrierVoyageNumber", everyItem(equalTo(carrierVoyageNumber))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").using(jsonSchemaFactory));
        }
    }


    @Test
    public void testCarrierVoyageNumberQueryParamFalseFormat() {
        // specification -> maxLength:50
        List<String> carrierVoyageNumbers =  Arrays.asList("ABCtrytytrySwqeABCtrytytrySwqeABCtryABCtrytytrySwqe" +
                "tytrySwqeABCtrytytrySwqeABCtrytytrySwqeABCtrytytrySwqeABCtrytytrySwqe");

        for (String carrierVoyageNumber : carrierVoyageNumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierVoyageNumber", carrierVoyageNumber).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test
    public void testCarrierServiceCodeQueryParam() {

        List<String> carrierServiceCodes = getListOfAnyAttribute("carrierServiceCode");

        for (String carrierServiceCode : carrierServiceCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierServiceCode", carrierServiceCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    body("carrierServiceCode", everyItem(equalTo(carrierServiceCode))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").using(jsonSchemaFactory));
        }
    }

    @Test
    public void testCarrierServiceCodeQueryParamFalseFormat() {

        List<String> carrierServiceCodes = Arrays.asList("2weqqqqqqwz","ABCtrytytrySwqe");

        for (String carrierServiceCode : carrierServiceCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("carrierServiceCode", carrierServiceCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test
    public void testOperationsEventTypeCodeQueryParam() {

        List<String> operationsEventTypeCodes = getListOfAnyAttribute("transportEventTypeCode");

        for (String operationsEventTypeCode : operationsEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("operationsEventTypeCode", operationsEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(200).
                    body("operationsEventTypeCodes", is(operationsEventTypeCode)).
                    assertThat().body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").using(jsonSchemaFactory));
        }

    }

    @Test
    public void testOperationsEventTypeCodeQueryParamFalseFormat() {

        List<String> operationsEventTypeCodes = Arrays.asList("","2z","ABCS");

        for (String operationsEventTypeCode : operationsEventTypeCodes) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("operationsEventTypeCode", operationsEventTypeCode).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(400);
        }
    }

    @Test
    public void testEventCreatedDateTimeQueryParam() {

        List<String> eventCreatedDateTimes = getListOfAnyAttribute("EventCreatedDateTime");

        eventCreatedDateTimes.forEach(eventCreatedDateTime -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("eventCreatedDateTime", eventCreatedDateTime).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(200).
                    body("eventCreatedDateTime", everyItem(equalTo(eventCreatedDateTime))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").using(jsonSchemaFactory));
        });
    }

    // Testing eventCreatedDateTime (gte, gt, lte, lt, eq)
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
    @Test
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

    @Test
    public void testLimitQueryParam() {

        List<String> limits = getListOfAnyAttribute("limit");

       limits.forEach(limit -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("limit", limit).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(200).
                    body("limit", everyItem(equalTo(limit))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                   using(jsonSchemaFactory));
        });
    }

    // Test limit, fails as formatting is wrong.
    @Test
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
    @Test
    public void testCursorQueryParam() {

        List<String> cursors = getListOfAnyAttribute("cursor");

        cursors.forEach(cursor -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("cursor", cursor).
                    get(Configuration.ROOT_URI + "/events").
                    then().
                    assertThat().
                    statusCode(200).
                    body("cursor", everyItem(equalTo(cursor))).
                    body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                            using(jsonSchemaFactory));
        });
    }

    // API-Version as a header parameter, and verifies the response
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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory));
    }

    private List getListOfAnyAttribute(String attribute) {
        String json = given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").
                body().asString();

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
                body(matchesJsonSchemaInClasspath("ovs/v2/EventsSchema.json").
                        using(jsonSchemaFactory)).
                extract().body().asString();

        return JsonPath.from(json).getList(attribute).stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }
}
