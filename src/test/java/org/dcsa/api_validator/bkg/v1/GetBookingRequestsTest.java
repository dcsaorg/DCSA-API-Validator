package org.dcsa.api_validator.bkg.v1;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class GetBookingRequestsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();

    @Test
    public void testBookingRequests() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/booking-summaries").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").
                        using(jsonSchemaFactory));
    }

    // Dependent on carrierBookingRequestReference value being same in test data
    @Test
    public void testForCarrierBookingRequestReference() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                queryParam("carrierBookingRequestReference", "CARRIER_BOOKING_REQUEST_REFERENCE_01").
                get(Configuration.ROOT_URI + "/booking-summaries").
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").using(jsonSchemaFactory));

    }

    // check for exportDeclarationReference if (isExportDeclarationRequired = true)
    @Test
    public void testForExportDeclarationReference() {
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> responses =
                (List<Map<?, ?>>) given().
                        auth().
                        oauth2(Configuration.accessToken).
                        get(Configuration.ROOT_URI + "/booking-summaries").
                        then().
                        assertThat().
                        statusCode(200).
                        body("size()", greaterThanOrEqualTo(0)).
                        body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").using(jsonSchemaFactory)).
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
                        get(Configuration.ROOT_URI + "/booking-summaries").
                        then().
                        assertThat().
                        statusCode(200).
                        body("size()", greaterThanOrEqualTo(0)).
                        body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").using(jsonSchemaFactory)).
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
                        get(Configuration.ROOT_URI + "/booking-summaries").
                        then().
                        assertThat().
                        statusCode(200).
                        body("size()", greaterThanOrEqualTo(0)).
                        body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").using(jsonSchemaFactory)).
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

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(submissionDateTimesSorted.get(i - 1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(submissionDateTimesSorted.get(i), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) <= 0);
        }
    }

    // Test sorting:DESC (descending) and if order is respected. for submissionDateTime
    @Test
    public void testSortDescSubmissionDateTimeQueryParam() {

        List<String> submissionDateTimesSorted = getListOfAnyAttribute("submissionDateTime", "sort", "submissionDateTime:DESC");
        assert (!submissionDateTimesSorted.isEmpty());
        int n = submissionDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(submissionDateTimesSorted.get(i - 1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(submissionDateTimesSorted.get(i), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) >= 0);
        }
    }

    // Test sorting:ASC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortAscBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestDateTime", "sort", "bookingRequestDateTime:ASC");
        assert (!bookingRequestDateTimesSorted.isEmpty());
        int n = bookingRequestDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(bookingRequestDateTimesSorted.get(i - 1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(bookingRequestDateTimesSorted.get(i), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) <= 0);
        }
    }

    // Test sorting:DESC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortDescBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestDateTime", "sort", "bookingRequestDateTime:DESC");
        assert (!bookingRequestDateTimesSorted.isEmpty());
        int n = bookingRequestDateTimesSorted.size();

        for (int i = 1; i < n; i++) {
            OffsetDateTime dateTime1 = OffsetDateTime.parse(bookingRequestDateTimesSorted.get(i - 1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            OffsetDateTime dateTime2 = OffsetDateTime.parse(bookingRequestDateTimesSorted.get(i), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Assert.assertTrue(dateTime1.compareTo(dateTime2) >= 0);
        }
    }

    @Test
    public void testLimitQueryParam() {

        List<Integer> limits = Arrays.asList(1, 2, 5, 6);

        limits.forEach(limit -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("limit", limit).
                    get(Configuration.ROOT_URI + "/booking-summaries").
                    then().
                    assertThat().
                    statusCode(200).
                    body("size()", equalTo(limit)).
                    body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").using(jsonSchemaFactory));
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

    @Test
    public void testAPIVersionQueryParam() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                header("API-Version", "2").
                get(Configuration.ROOT_URI + "/booking-summaries").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").using(jsonSchemaFactory));
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
                get(Configuration.ROOT_URI + "/booking-summaries").
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestsSchema.json").using(jsonSchemaFactory)).
                        extract().body().asString();

        return JsonPath.from(json).getList(attribute).stream().collect(Collectors.toList());
    }


}
