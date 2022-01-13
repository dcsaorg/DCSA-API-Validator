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
import static org.dcsa.api_validator.TestUtil.jsonToMap;
import static org.dcsa.api_validator.TestUtil.loadFileAsString;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.*;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the POST /bookings endpoint
 */

public class PostBookingTest {

  public static final String VALID_BOOKING = loadFileAsString("bkg/v1/ValidBookingPost.json");

  private static final String[] MANDATORY_FIELDS = {
    "receiptTypeAtOrigin",
    "deliveryTypeAtDestination",
    "cargoMovementTypeAtOrigin",
    "cargoMovementTypeAtDestination",
    "serviceContractReference",
    "isPartialLoadAllowed",
    "isExportDeclarationRequired",
    "isImportLicenseRequired",
    "submissionDateTime",
    "communicationChannel",
    "isEquipmentSubstitutionAllowed"
  };

  @Test
  public void testValidBookingPost() {
    String splat =
        given()
            .auth()
            .oauth2(Configuration.accessToken)
            .contentType("application/json")
            .body(VALID_BOOKING)
            .post(Configuration.ROOT_URI + BOOKING_PATH)
            .then()
            .assertThat()
            .body("documentStatus", equalTo("RECE"))
            .body("carrierBookingRequestReference", notNullValue())
            .body("bookingRequestCreatedDateTime", notNullValue())
            .body("bookingRequestUpdatedDateTime", notNullValue())
            .statusCode(202)
            .body(JSON_SCHEMA_VALIDATOR)
            .extract()
            .body()
            .asString();

    System.out.println("testValidBookingPost");
  }

  @Test
  public void testBookingPostMissingAllMandatoryFieldsIndividually() {
    final String[] mandatoryFields = {
      "receiptTypeAtOrigin",
      "deliveryTypeAtDestination",
      "cargoMovementTypeAtOrigin",
      "cargoMovementTypeAtDestination",
      "serviceContractReference",
      "isPartialLoadAllowed",
      "isExportDeclarationRequired",
      "isImportLicenseRequired",
      "submissionDateTime",
      "communicationChannel",
      "isEquipmentSubstitutionAllowed"
    };

    for (String mandatoryField : mandatoryFields) {
      Map<String, Object> map = jsonToMap(VALID_BOOKING);
      map.remove(mandatoryField);

      given()
          .auth()
          .oauth2(Configuration.accessToken)
          .contentType("application/json")
          .body(map)
          .post(Configuration.ROOT_URI + BOOKING_PATH)
          .then()
          .assertThat()
          .statusCode(400)
          .body(JSON_SCHEMA_VALIDATOR);
    }
    System.out.println("testBookingPostMissingAllMandatoryFieldsIndividually");
  }
}
