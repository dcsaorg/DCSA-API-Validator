package org.dcsa.api_validator.bkg.v1.bookings;

import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BOOKING_SUMMARIES_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.JSON_SCHEMA_VALIDATOR;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /booking-summaries endpoint
 */

public class GetBookingRequestSummariesTest {

    private List<Map<?, ?>> response;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.response =
                (List<Map<?, ?>>)
                        given().
                                auth().
                                oauth2(Configuration.accessToken).
                                get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                                then().
                                assertThat().
                                statusCode(200).
                                body("size()", greaterThanOrEqualTo(0)).
                                body(JSON_SCHEMA_VALIDATOR).
                                extract().body().as(List.class);
    }


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
    @Test
    public void testDocumentStatusQueryParam() {
        response.stream()
                .map(res -> res.get("documentStatus")).forEach(documentStatus -> {
                    given()
                            .auth()
                            .oauth2(Configuration.accessToken)
                            .queryParam("documentStatus", documentStatus)
                            .get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH)
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .body("[0]", hasEntry("documentStatus", documentStatus))
                            .body("documentStatus", everyItem(equalTo(documentStatus)))
                            .body(JSON_SCHEMA_VALIDATOR);
                });
    }

    // Test sorting:DESC (descending) and if order is respected. for submissionDateTime
    @Test
    public void testSortAscSubmissionDateTimeQueryParam() {

        List<String> submissionDateTimesSorted = getListOfAnyAttribute("submissionDateTime", "sort", "submissionDateTime:ASC");
        assert (!submissionDateTimesSorted.isEmpty());
        int n = submissionDateTimesSorted.size();

        for (int i = 0; i < submissionDateTimesSorted.size() - 2; i++) {
          Assert.assertTrue(isAfterOrEqualDateTime(submissionDateTimesSorted.get(i), submissionDateTimesSorted.get(i + 1)));
        }
    }

    // Test sorting:DESC (descending) and if order is respected. for submissionDateTime
    @Test
    public void testSortDescSubmissionDateTimeQueryParam() {

        List<String> submissionDateTimesSorted = getListOfAnyAttribute("submissionDateTime", "sort", "submissionDateTime:DESC");
        assert (!submissionDateTimesSorted.isEmpty());

        for (int i = 0; i < submissionDateTimesSorted.size() - 2; i++) {
            Assert.assertTrue(isAfterOrEqualDateTime(submissionDateTimesSorted.get(i + 1), submissionDateTimesSorted.get(i)));
        }
    }

    // Test sorting:ASC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortAscBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestCreatedDateTime", "sort", "bookingRequestCreatedDateTime:ASC");
        assert (!bookingRequestDateTimesSorted.isEmpty());

        for (int i = 0; i < bookingRequestDateTimesSorted.size() - 2; i++) {
          Assert.assertTrue(isAfterOrEqualDateTime(bookingRequestDateTimesSorted.get(i), bookingRequestDateTimesSorted.get(i + 1)));
        }
    }

    // Test sorting:DESC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortDescBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestCreatedDateTime", "sort", "bookingRequestCreatedDateTime:DESC");
        assert (!bookingRequestDateTimesSorted.isEmpty());

      for (int i = 0; i < bookingRequestDateTimesSorted.size() - 2; i++) {
        Assert.assertTrue(isAfterOrEqualDateTime(bookingRequestDateTimesSorted.get(i + 1), bookingRequestDateTimesSorted.get(i)));
      }
    }

    private Boolean isAfterOrEqualDateTime(String beforeStr, String afterStr) {
        OffsetDateTime before = OffsetDateTime.parse(beforeStr);
        OffsetDateTime after = OffsetDateTime.parse(afterStr);
        return after.isAfter(before) || after.isEqual(before);
    }

    @Test
    public void testLimitQueryParam() {

        List<Integer> limits = Arrays.asList(1, 2, 5, 6);

        limits.forEach(limit -> {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("limit", limit).
                    get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
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
                get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH).
                then().
                assertThat()
                .header("API-Version", "1.0.0").
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
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(JSON_SCHEMA_VALIDATOR).
                extract().body().asString();

        return JsonPath.from(json).getList(attribute).stream().collect(Collectors.toList());
    }


}
