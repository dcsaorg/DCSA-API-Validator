package org.dcsa.api_validator.bkg.v1;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
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

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.dcsa.api_validator.bkg.v1.bookingconfirmations.BookingTestConfiguration.BOOKING_REQUEST_SUMMARIES_PATH;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /booking-summaries endpoint
 */

public class GetBookingRequestsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();

    private List<Map<?, ?>> response;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.response =
                (List<Map<?, ?>>)
                        given()
                                .auth().
                                oauth2(Configuration.accessToken).
                                get(Configuration.ROOT_URI + BOOKING_REQUEST_SUMMARIES_PATH).
                                then().
                                assertThat().
                                statusCode(200).
                                body("size()", greaterThanOrEqualTo(0)).
                                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestSummariesSchema.json").
                                        using(jsonSchemaFactory)).
                                extract().body().as(List.class);
    }


    @Test
    public void testBookingRequests() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + BOOKING_REQUEST_SUMMARIES_PATH).
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestSummariesSchema.json").
                        using(jsonSchemaFactory));
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
                            .get(Configuration.ROOT_URI + BOOKING_REQUEST_SUMMARIES_PATH)
                            .then()
                            .assertThat()
                            .statusCode(200)
                            .body("[0]", hasEntry("documentStatus", documentStatus))
                            .body("documentStatus", everyItem(equalTo(documentStatus)))
                            .body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestSummariesSchema.json").
                                    using(jsonSchemaFactory));
                });
    }

    // Test sorting:DESC (descending) and if order is respected. for submissionDateTime
    @Test
    public void testSortAscSubmissionDateTimeQueryParam() {

        List<String> submissionDateTimesSorted = getListOfAnyAttribute("submissionDateTime", "sort", "submissionDateTime:ASC");
        assert (!submissionDateTimesSorted.isEmpty());
        int n = submissionDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(submissionDateTimesSorted.get(0)).isBefore(OffsetDateTime.parse(submissionDateTimesSorted.get(n - 1))));
    }

    // Test sorting:DESC (descending) and if order is respected. for submissionDateTime
    @Test
    public void testSortDescSubmissionDateTimeQueryParam() {

        List<String> submissionDateTimesSorted = getListOfAnyAttribute("submissionDateTime", "sort", "submissionDateTime:DESC");
        assert (!submissionDateTimesSorted.isEmpty());
        int n = submissionDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(submissionDateTimesSorted.get(0)).isAfter(OffsetDateTime.parse(submissionDateTimesSorted.get(n - 1))));
    }

    // Test sorting:ASC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortAscBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestCreatedDateTime", "sort", "bookingRequestCreatedDateTime:ASC");
        assert (!bookingRequestDateTimesSorted.isEmpty());
        int n = bookingRequestDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(0)).isBefore(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(n - 1))));
    }

    // Test sorting:DESC (descending) and if order is respected. for BookingRequestDateTime
    @Test
    public void testSortDescBookingRequestDateTimeQueryParam() {

        List<String> bookingRequestDateTimesSorted = getListOfAnyAttribute("bookingRequestCreatedDateTime", "sort", "bookingRequestCreatedDateTime:DESC");
        assert (!bookingRequestDateTimesSorted.isEmpty());
        int n = bookingRequestDateTimesSorted.size();

        Assert.assertTrue(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(0)).isAfter(OffsetDateTime.parse(bookingRequestDateTimesSorted.get(n - 1))));
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
                    body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestSummariesSchema.json").using(jsonSchemaFactory));
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
                get(Configuration.ROOT_URI + BOOKING_REQUEST_SUMMARIES_PATH).
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
                get(Configuration.ROOT_URI + BOOKING_REQUEST_SUMMARIES_PATH).
                then().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestSummariesSchema.json").using(jsonSchemaFactory));
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
                get(Configuration.ROOT_URI + BOOKING_REQUEST_SUMMARIES_PATH).
                then().
                assertThat().
                statusCode(200).
                body("size()", greaterThanOrEqualTo(0)).
                body(matchesJsonSchemaInClasspath("bkg.v1/BookingRequestSummariesSchema.json").using(jsonSchemaFactory)).
                extract().body().asString();

        return JsonPath.from(json).getList(attribute).stream().collect(Collectors.toList());
    }


}
