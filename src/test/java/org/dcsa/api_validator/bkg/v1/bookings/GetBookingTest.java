package org.dcsa.api_validator.bkg.v1.bookings;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.*;
import static org.hamcrest.Matchers.*;

public class GetBookingTest {

  List<String> carrierBookingRequestReferenceList;

  @BeforeClass
  public void getAllCarrierBookingRequestReferences() {
    this.carrierBookingRequestReferenceList =
        JsonPath.from(
                given()
                    .auth()
                    .oauth2(Configuration.accessToken)
                    .get(Configuration.ROOT_URI + BOOKING_SUMMARIES_PATH)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .body("size()", greaterThanOrEqualTo(0))
                    .body(JSON_SCHEMA_VALIDATOR)
                    .extract()
                    .body()
                    .asString())
            .getList("carrierBookingRequestReference");
  }

  @Test
  public void testGetValidBookingsTest() {

    carrierBookingRequestReferenceList.forEach(
        carrierBookingRequestReference ->
            given()
                .auth()
                .oauth2(Configuration.accessToken)
                .when()
                .pathParam("carrierBookingRequestReference", carrierBookingRequestReference)
                .get(Configuration.ROOT_URI + BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH)
                .then()
                .assertThat()
                .header("API-Version", "1.0.0")
                .body("carrierBookingRequestReference", equalTo(carrierBookingRequestReference))
                .statusCode(HttpStatus.SC_OK)
                .body(JSON_SCHEMA_VALIDATOR));
  }

  @Test
  public void testWithInvalidCarrierBookingRequestReference() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .pathParam(
            "carrierBookingRequestReference",
            "12345678912345678901235678945651205451686156465154515564845156754845678465544567845648456548151554234")
        .get(Configuration.ROOT_URI + BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void testWithUnknownCarrierBookingRequestReference() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .pathParam("carrierBookingRequestReference", "IdoNotExist")
        .get(Configuration.ROOT_URI + BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_NOT_FOUND);
  }
}
