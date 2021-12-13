package org.dcsa.api_validator.bkg.v1;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BKG_SUMMARIES_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BKG_OAS_VALIDATOR;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /booking-summaries endpoint
 */

public class GetBookingRequestsTest {


    private List<Map<?, ?>> response;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.response =
                (List<Map<?, ?>>)
                        given()
                                .auth()
                                .oauth2(Configuration.accessToken)
                                .when()
                                .filter(BKG_OAS_VALIDATOR)
                                .get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH)
                                .then()
                                .assertThat()
                                .body("size()", greaterThanOrEqualTo(0))
                                .statusCode(HttpStatus.SC_OK).
                                extract().body().as(List.class);
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testBookingSummariesRequests(ITestContext context) {
        String BookingSummaries =
                given()
                        .auth()
                        .oauth2(Configuration.accessToken)
                        .filter(BKG_OAS_VALIDATOR)
                        .when()
                        .get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .asString();

        List<String> documentStatuses = JsonPath.from(BookingSummaries).getList("documentStatus");
        List<String> carrierBookingReferenceList = JsonPath.from(BookingSummaries).getList("carrierBookingRequestReference");
        context.setAttribute("carrierBookingRequestReferenceList", carrierBookingReferenceList);
        context.setAttribute("documentStatusList", documentStatuses);

    }

    @Test(dependsOnMethods = {"testBookingSummariesRequests"}, alwaysRun = true)
    public void testForCarrierBookingRequestReference(ITestContext context) {
        @SuppressWarnings("unchecked")
        List<String> carrierBookingRequestReferenceList = (List<String>) context.getAttribute("carrierBookingRequestReferenceList");
        carrierBookingRequestReferenceList.forEach(
                carrierBookingRequestReference -> {
                    given()
                            .auth()
                            .oauth2(Configuration.accessToken)
                            .filter(BKG_OAS_VALIDATOR)
                            .queryParam("carrierBookingRequestReference", carrierBookingRequestReference)
                            .when()
                            .get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH)
                            .then()
                            .body("[0]", hasEntry("carrierBookingRequestReference", carrierBookingRequestReference))
                            .body("carrierBookingRequestReference", everyItem(equalTo(carrierBookingRequestReference)))
                            .assertThat()
                            .statusCode(HttpStatus.SC_OK);
                });
    }

    // check for exportDeclarationReference if (isExportDeclarationRequired = true)
    @Test
    public void testForExportDeclarationReference() {
        response.stream().filter(res -> getBool(res, "isExportDeclarationRequired"))
                .map(res -> res.get("exportDeclarationReference")).forEach(Assert::assertNotNull);
    }

    // check for importLicenseReference if (isImportLicenseRequired = true)
    @Test
    public void testForImportLicenseReference() {
        response.stream().filter(res -> getBool(res, "isImportLicenseRequired"))
                .map(res -> res.get("importLicenseReference")).forEach(Assert::assertNotNull);
    }

    // Mandatory if (vessel/voyage is not provided)
    @Test
    public void testForExpectedDepartureTime() {
        response.stream().filter(res -> res.get("vesselIMONumber") == null)
                .map(res -> res.get("expectedDepartureDate")).forEach(Assert::assertNotNull);
    }

    // Test for documentStatus filtering
    @Test(dependsOnMethods = {"testBookingSummariesRequests"}, alwaysRun = true)
    public void testDocumentStatusQueryParam(ITestContext context) {
        @SuppressWarnings("unchecked")
        List<String> documentStatuses = (List<String>) context.getAttribute("documentStatusList");

        documentStatuses.forEach(documentStatus -> {
            given()
                    .auth()
                    .oauth2(Configuration.accessToken)
                    .filter(BKG_OAS_VALIDATOR)
                    .queryParam("documentStatus", documentStatus)
                    .when()
                    .get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH)
                    .then()
                    .body("[0]", hasEntry("documentStatus", documentStatus))
                    .body("documentStatus", everyItem(equalTo(documentStatus)))
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);
        });
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
            given()
                    .auth()
                    .oauth2(Configuration.accessToken)
                    .filter(BKG_OAS_VALIDATOR)
                    .queryParam("limit", limit).
                    get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH).
                    then().
                    assertThat().
                    statusCode(200).
                    body("size()", equalTo(limit));
        });
    }

    // Test limit, fails as formatting is wrong.
    @Test
    public void testLimitFalseFormat() {
        given()
                .auth()
                .oauth2(Configuration.accessToken)
                .when()
                .filter(BKG_OAS_VALIDATOR)
                .queryParam("limit", "0").
                // Specification -> minimum: 1
       get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH).
                then().
                assertThat().
                statusCode(400);
    }

    @Test
    public void testAPIVersionQueryParam() {
        given()
                .auth()
                .oauth2(Configuration.accessToken)
                .filter(BKG_OAS_VALIDATOR)
                .header("API-Version", "2").
                get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH).
                then().
                statusCode(400);
    }

    // Helper function: to avoid nulling for booleans when stream filtering
    private boolean getBool(Map response, String s) {
        Boolean b = (Boolean) response.get(s);
        Assert.assertNotNull(b, "expected boolean to present since, " + s + " is MANDATORY! \n");
        return b;
    }

    // Helper: Used with sort options
    private List getListOfAnyAttribute(String attribute, String queryParam, String queryParamValue) {

        String json =
                given()
                        .auth()
                        .oauth2(Configuration.accessToken)
                        .filter(BKG_OAS_VALIDATOR)
                        .queryParam(queryParam, queryParamValue)
                        .get(Configuration.ROOT_URI + BKG_SUMMARIES_PATH)
                        .then()
                        .assertThat()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body()
                        .asString();

        return JsonPath.from(json).getList(attribute).stream().collect(Collectors.toList());
    }


}
