package org.dcsa.api_validator.bkg.v1.bookingconfirmations;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.bookingconfirmations.BookingTestConfiguration.BKG_OAS_VALIDATOR;
import static org.dcsa.api_validator.bkg.v1.bookingconfirmations.BookingTestConfiguration.SHIPMENT_SUMMARIES_PATH;
import static org.hamcrest.Matchers.equalTo;

public class GetShipmentSummaryTestConfiguration {

  @Test
  public void testGetShipmentSummaries(ITestContext context) {
    String bookingConfirmationSummaries =
        given()
            .auth()
            .oauth2(Configuration.accessToken)
            .filter(BKG_OAS_VALIDATOR)
          .when()
            .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
          .then()
            .assertThat()
            .header("API-Version", "1.0.0")
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString();

    List<String> documentStatusList = JsonPath.from(bookingConfirmationSummaries).getList("documentStatus");
    context.setAttribute("documentStatusList", documentStatusList);
  }

  @Test(dependsOnMethods = {"testGetShipmentSummaries"}, alwaysRun = true)
  public void testGetShipmentSummariesWithDocumentStatusFilter(ITestContext context) {
    List<String> documentStatusList = (List<String>) context.getAttribute("documentStatusList");
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .filter(BKG_OAS_VALIDATOR)
        .queryParam("documentStatus", documentStatusList.get(0))
        .when()
        .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
        .then()
        .body("size()", equalTo(Collections.frequency(documentStatusList, documentStatusList.get(0))))
        .assertThat()
        .statusCode(HttpStatus.SC_OK);
  }

  @Test
  public void testGetShipmentSummariesWithoutResults() {
    given()
      .auth()
      .oauth2(Configuration.accessToken)
      .filter(BKG_OAS_VALIDATOR)
      .queryParam("documentStatus", "CONF")
    .when()
      .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
    .then()
      .body("size()", equalTo(0))
      .assertThat()
      .statusCode(HttpStatus.SC_OK);
  }

  @Test
  public void testGetShipmentSummariesWithInvalidDocumentStatus() {
    given()
      .auth()
      .oauth2(Configuration.accessToken)
      .queryParam("documentStatus", "IAmNotValid")
    .when()
      .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
    .then()
      .assertThat()
      .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void testGetBookingConfirmationSummariesWithInvalidDocumentStatus() {
    given()
      .auth()
      .oauth2(Configuration.accessToken)
      .queryParam("documentStatus", "VOID")
    .when()
      .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
    .then()
      .assertThat()
      .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void testGetBookingConfirmationSummariesWithSort() {
    String bookingConfirmationSummaries =
      given()
        .auth()
        .oauth2(Configuration.accessToken)
        .filter(BKG_OAS_VALIDATOR)
        .queryParam("sort", "confirmationDateTime:DESC")
      .when()
        .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
      .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .asString();

    List<String> confirmationDateTimeList = JsonPath.from(bookingConfirmationSummaries).getList("confirmationDateTime");
    assert (!confirmationDateTimeList.isEmpty());
    int n = confirmationDateTimeList.size();

    Assert.assertTrue(OffsetDateTime.parse(confirmationDateTimeList.get(0)).isAfter(OffsetDateTime.parse(confirmationDateTimeList.get(n -1))));
  }

  @Test
  public void testGetShipmentSummariesWithLimit() {
    String bookingConfirmationSummaries =
      given()
        .auth()
        .oauth2(Configuration.accessToken)
        .filter(BKG_OAS_VALIDATOR)
        .queryParam("limit", "2")
      .when()
        .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
      .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .asString();

    List<String> documentStatusList = JsonPath.from(bookingConfirmationSummaries).getList("documentStatus");
    Assert.assertEquals(2, documentStatusList.size());
  }


}
