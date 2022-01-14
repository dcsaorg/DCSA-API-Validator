package org.dcsa.api_validator.bkg.v1.shipments;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.*;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.SHIPMENT_CARRIERBOOKINGREFERENCE_PATH;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.JSON_SCHEMA_VALIDATOR;
import static org.hamcrest.Matchers.*;

/*
 * Tests related to the GET /shipments/{carrierBookingReference} endpoint
 */

public class testGetShipmentByCarrierBookingReference {

  private List<String> carrierBookingReferenceListForShipments;

  @BeforeClass
  public void getAllCarrierBookingReferencesForShipments() {
    this.carrierBookingReferenceListForShipments =
        JsonPath.from(
                given()
                    .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .body("size()", greaterThanOrEqualTo(0))
                    .body(JSON_SCHEMA_VALIDATOR)
                    .extract()
                    .body()
                    .asString())
            .getList("carrierBookingReference");
  }

  @Test
  public void testGetValidShipments() {
    carrierBookingReferenceListForShipments.forEach(
        carrierBookingReference ->
            given()
                .when()
                .pathParam("carrierBookingReference", carrierBookingReference)
                .get(Configuration.ROOT_URI + SHIPMENT_CARRIERBOOKINGREFERENCE_PATH)
                .then()
                .assertThat()
                .body("size()", greaterThanOrEqualTo(0))
                .body("carrierBookingReference", equalTo(carrierBookingReference))
                .header("API-Version", "1.0.0")
                .statusCode(HttpStatus.SC_OK)
                .body(JSON_SCHEMA_VALIDATOR));
  }

  @Test
  public void testShipmentWithInvalidCarrierBookingReference() {
    given()
        .when()
        .pathParam(
            "carrierBookingReference",
            "12345678912345678901235678945651205451686156465154515564845156754845678465544567845648456548151554234")
        .get(Configuration.ROOT_URI + SHIPMENT_CARRIERBOOKINGREFERENCE_PATH)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void testShipmentWithUnknownCarrierBookingReference() {
    given()
        .when()
        .pathParam("carrierBookingReference", "IdoNotExist")
        .get(Configuration.ROOT_URI + SHIPMENT_CARRIERBOOKINGREFERENCE_PATH)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }
}
