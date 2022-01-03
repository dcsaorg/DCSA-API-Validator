package org.dcsa.api_validator.bkg.v1.bookings;

import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.bookingconfirmations.BookingTestConfiguration.BOOKING_REQUEST_SUMMARIES_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BOOKING_SUMMARIES_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.JSON_SCHEMA_VALIDATOR;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/*
 * Tests related to the GET /booking-summaries endpoint
 */

public class GetBookingRequestSummariesTest {

    @Test
    public void testBookingRequests() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(JSON_SCHEMA_VALIDATOR);
    }

    // Dependent on carrierBookingRequestReference value being same in test data
    @Test
    public void testForCarrierBookingRequestReference() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("carrierBookingRequestReference", "CARRIER_BOOKING_REQUEST_REFERENCE_01").
                get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(JSON_SCHEMA_VALIDATOR);

    }

    // check for exportDeclarationReference if (isExportDeclarationRequired = true)
    @Test
    public void testForExportDeclarationReference() {
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> responses =
                (List<Map<?, ?>>) given().
                        auth().
                        oauth2(Configuration.accessToken).
                        get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                        then().
                        assertThat().
                        statusCode(200).
                        body("size()", greaterThanOrEqualTo(0)).
                        body(JSON_SCHEMA_VALIDATOR).
                        extract().body().as(List.class);

        responses.stream().filter(response -> getBool(response, "isExportDeclarationRequired"))
                .map(response -> response.get("exportDeclarationReference")).forEach(Assert::assertNotNull);

    }

    // check for importLicenseReference if (isImportLicenseRequired = true)
    @Test
    public void testForImportLicenseReference() {
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> responses =
                (List<Map<?, ?>>) given().
                        auth().
                        oauth2(Configuration.accessToken).
                        get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                        then().
                        assertThat().
                        statusCode(200).
                        body("size()", greaterThanOrEqualTo(0)).
                        body(JSON_SCHEMA_VALIDATOR).
                        extract().body().as(List.class);

        responses.stream().filter(response -> getBool(response, "isImportLicenseRequired"))
                .map(response -> response.get("importLicenseReference")).forEach(Assert::assertNotNull);

    }

    // Mandatory if (vessel/voyage is not provided)
    @Test
    public void testForExpectedDepartureTime() {
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> responses =
                (List<Map<?, ?>>) given().
                        auth().
                        oauth2(Configuration.accessToken).
                        get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                        then().
                        assertThat().
                        statusCode(200).
                        body("size()", greaterThanOrEqualTo(0)).
                        body(JSON_SCHEMA_VALIDATOR).
                        extract().body().as(List.class);

        responses.stream().filter(response -> response.get("vesselIMONumber") == null)
                .map(response -> response.get("expectedDepartureDate")).forEach(Assert::assertNotNull);
    }

    // Test for documentStatus filtering - value: RECE
    @Test
    public void testDocumentStatusQueryParam() {

        List<String> documentStatuses = getListOfAnyAttribute("documentStatus", "documentStatus", "RECE");
        assert (!documentStatuses.isEmpty());
        int n = documentStatuses.size();

        for (int i = 0; i < n; i++) {
            Assert.assertTrue(documentStatuses.get(i).equals("RECE"));
        }
    }

    // Test sorting:DESC (descending) and if order is respected. for submissionDateTime
    @Test
    public void testSortAscSubmissionDateTimeQueryParam() {

        List<String> submissionDateTimesSorted = getListOfAnyAttribute("submissionDateTime", "sort", "submissionDateTime:ASC");
        assert (!submissionDateTimesSorted.isEmpty());
        int n = submissionDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(submissionDateTimesSorted.get(0)).isBefore(OffsetDateTime.parse(submissionDateTimesSorted.get(n -1))));
    }

    // Test sorting:DESC (descending) and if order is respected. for submissionDateTime
    @Test
    public void testSortDescSubmissionDateTimeQueryParam() {

        List<String> submissionDateTimesSorted = getListOfAnyAttribute("submissionDateTime", "sort", "submissionDateTime:DESC");
        assert (!submissionDateTimesSorted.isEmpty());
        int n = submissionDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(submissionDateTimesSorted.get(0)).isAfter(OffsetDateTime.parse(submissionDateTimesSorted.get(n -1))));
    }

    // Test sorting:ASC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortAscBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestCreatedDateTime", "sort", "bookingRequestCreatedDateTime:ASC");
        assert (!bookingRequestDateTimesSorted.isEmpty());
        int n = bookingRequestDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(0)).isBefore(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(n -1))));
    }

    // Test sorting:DESC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortDescBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestCreatedDateTime", "sort", "bookingRequestCreatedDateTime:DESC");
        assert (!bookingRequestDateTimesSorted.isEmpty());
        int n = bookingRequestDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(0)).isAfter(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(n -1))));
    }

    @Test
    public void testLimitQueryParam() {

        List<Integer> limits = Arrays.asList(1, 2, 5, 6);

        limits.forEach(limit -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("limit", limit).
                    get(Configuration.ROOT_URI + BOOKING_REQUEST_SUMMARIES_PATH).
                    then().
                    assertThat().
                    statusCode(200).
                    body("size()", equalTo(limit)).
                    body(JSON_SCHEMA_VALIDATOR);
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
                get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                then().
                assertThat().
                statusCode(400);
    }

    @Test
    public void testAPIVersionQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                header("API-Version", "2").
                get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(JSON_SCHEMA_VALIDATOR);
    }

    private boolean getBool(Map response, String s) {
        Boolean b = (Boolean) response.get(s);
        Assert.assertNotNull(b, "expected boolean to present since, " + s + " is MANDATORY! \n");
        return b;
    }

    private List getListOfAnyAttribute(String attribute, String queryParam, String queryParamValue) {
        String json = given().

                auth().
                oauth2(Configuration.accessToken).
                queryParam(queryParam, queryParamValue).
                get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(JSON_SCHEMA_VALIDATOR).
                        extract().body().asString();

        return JsonPath.from(json).getList(attribute).stream().collect(Collectors.toList());
    }


}
