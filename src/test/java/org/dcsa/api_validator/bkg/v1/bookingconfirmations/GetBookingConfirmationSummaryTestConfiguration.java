package org.dcsa.api_validator.bkg.v1.bookingconfirmations;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BKG_CONFIRMATION_SUMMARIES_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BKG_OAS_VALIDATOR;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

public class GetBookingConfirmationSummaryTestConfiguration {

  @Test
  public void testGetBookingConfirmationSummaries(ITestContext context) {
    String bookingConfirmationSummaries =
        given()
            .auth()
            .oauth2(Configuration.accessToken)
            .filter(BKG_OAS_VALIDATOR)
          .when()
            .get(Configuration.ROOT_URI + BKG_CONFIRMATION_SUMMARIES_PATH)
          .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .asString();

    List<String> carrierBookingReferenceList = JsonPath.from(bookingConfirmationSummaries).getList("carrierBookingReference");
    context.setAttribute("carrierBookingReferenceList", carrierBookingReferenceList);
  }

  @Test(dependsOnMethods = {"testGetBookingConfirmationSummaries"}, alwaysRun = true)
  public void testGetSingleBookingConfirmationSummary(ITestContext context) {
    List<String> carrierBookingReferenceList = (List<String>) context.getAttribute("carrierBookingReferenceList");
    given()
      .auth()
      .oauth2(Configuration.accessToken)
      .filter(BKG_OAS_VALIDATOR)
      .queryParam("carrierBookingReference", carrierBookingReferenceList.get(0))
    .when()
      .get(Configuration.ROOT_URI + BKG_CONFIRMATION_SUMMARIES_PATH)
    .then()
      .body("[0]", hasEntry("carrierBookingReference", carrierBookingReferenceList.get(0)))
      .assertThat()
      .statusCode(HttpStatus.SC_OK);
  }

  @Test()
  public void testGetBookingConfirmationSummariesWithDocumentStatusFilter() {
    given()
      .auth()
      .oauth2(Configuration.accessToken)
      .filter(BKG_OAS_VALIDATOR)
      .queryParam("documentStatus", "PENU") //ToDo once documentStatus is added to the response of bookingConfirmationSummaries we can make this test more dynamic
    .when()
      .get(Configuration.ROOT_URI + BKG_CONFIRMATION_SUMMARIES_PATH)
    .then()
      .body("size()", equalTo(1))
      .assertThat()
      .statusCode(HttpStatus.SC_OK);
  }

  @Test
  public void testGetBookingConfirmationSummariesWithoutResults() {
    given()
      .auth()
      .oauth2(Configuration.accessToken)
      .filter(BKG_OAS_VALIDATOR)
      .queryParam("carrierBookingReference", "IDoNotExist")
    .when()
      .get(Configuration.ROOT_URI + BKG_CONFIRMATION_SUMMARIES_PATH)
    .then()
      .body("size()", equalTo(0))
      .assertThat()
      .statusCode(HttpStatus.SC_OK);
  }

  @Test
  public void testGetBookingConfirmationSummariesWithInvalidReference() {
    given()
      .auth()
      .oauth2(Configuration.accessToken)
      .queryParam("carrierBookingReference", "80d63706-7b93-4936-84fe-3ef9ef1946f0")
    .when()
      .get(Configuration.ROOT_URI + BKG_CONFIRMATION_SUMMARIES_PATH)
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
      .get(Configuration.ROOT_URI + BKG_CONFIRMATION_SUMMARIES_PATH)
    .then()
      .assertThat()
      .statusCode(HttpStatus.SC_BAD_REQUEST);
  }


}
