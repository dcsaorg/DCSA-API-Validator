package org.dcsa.api_validator.bkg.v1.bookings;

import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.TestUtil.jsonToMap;
import static org.dcsa.api_validator.TestUtil.loadFileAsString;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BOOKING_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.JSON_SCHEMA_VALIDATOR;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the POST /bookings endpoint
 */

public class PostBookingTest {

  public static final String VALID_BOOKING = loadFileAsString("bkg/v1/ValidBookingPost.json");

  @Test
  public void testValidBookingPost() {
    given()
        .contentType("application/json")
        .body(VALID_BOOKING)
        .post(Configuration.ROOT_URI + BOOKING_PATH)
        .then()
        .assertThat()
        .body("documentStatus", equalTo("RECE"))
        .body("carrierBookingRequestReference", notNullValue())
        .body("bookingRequestCreatedDateTime", notNullValue())
        .body("bookingRequestUpdatedDateTime", notNullValue())
        .statusCode(HttpStatus.SC_OK)
        .body(JSON_SCHEMA_VALIDATOR)
        .extract()
        .body()
        .asString();
  }

  @Test
  public void testBookingPostMissingAllMandatoryFields() {
    Map<String, Object> map = jsonToMap(VALID_BOOKING);
    assert map != null;
    map.remove("receiptTypeAtOrigin");
    map.remove("deliveryTypeAtDestination");
    map.remove("cargoMovementTypeAtOrigin");
    map.remove("cargoMovementTypeAtDestination");
    map.remove("serviceContractReference");
    map.remove("isPartialLoadAllowed");
    map.remove("isExportDeclarationRequired");
    map.remove("isImportLicenseRequired");
    map.remove("submissionDateTime");
    map.remove("communicationChannel");
    map.remove("isEquipmentSubstitutionAllowed");
    map.remove("commodities");

    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .contentType("application/json")
        .body(map)
        .post(Configuration.ROOT_URI + BOOKING_PATH)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body(JSON_SCHEMA_VALIDATOR);
  }

  @Test
  public void testBookingPostMissingAllOptionalFields() {
    Map<String, Object> map = jsonToMap(VALID_BOOKING);
    assert map != null;
    map.remove("paymentTermCode");
    map.remove("exportDeclarationReference");
    map.remove("importLicenseReference");
    map.remove("isAMSACIFilingRequired");
    map.remove("isDestinationFilingRequired");
    map.remove("contractQuotationReference");
    map.remove("expectedDepartureDate");
    map.remove("transportDocumentTypeCode");
    map.remove("transportDocumentReference");
    map.remove("bookingChannelReference");
    map.remove("incoTerms");
    map.remove("vesselName");
    map.remove("vesselIMONumber");
    map.remove("exportVoyageNumber");
    map.remove("preCarriageModeOfTransportCode");
    map.remove("invoicePayableAt");
    map.remove("placeOfIssue");
    map.remove("valueAddedServiceRequests");
    map.remove("references");
    map.remove("requestedEquipments");
    map.remove("documentParties");
    map.remove("shipmentLocations");

    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .contentType("application/json")
        .body(map)
        .post(Configuration.ROOT_URI + BOOKING_PATH)
        .then()
        .assertThat()
        .body("documentStatus", equalTo("RECE"))
        .body("carrierBookingRequestReference", notNullValue())
        .body("bookingRequestCreatedDateTime", notNullValue())
        .body("bookingRequestUpdatedDateTime", notNullValue())
        .statusCode(HttpStatus.SC_ACCEPTED)
        .body(JSON_SCHEMA_VALIDATOR);
  }

  @Test
  public void testAPIVersionQueryParam() {

    Map<String, Object> map = jsonToMap(VALID_BOOKING);

    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .contentType("application/json")
        .body(map)
        .post(Configuration.ROOT_URI + BOOKING_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .statusCode(HttpStatus.SC_ACCEPTED)
        .body("size()", greaterThanOrEqualTo(0))
        .body(JSON_SCHEMA_VALIDATOR);
  }
}
