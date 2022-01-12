package org.dcsa.api_validator.bkg.v1.events;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.*;
import static org.hamcrest.Matchers.*;

public class GetEventsTest {

  List<String> carrierBookingRequestReferences;
  List<String> carrierBookingReferences;

  @BeforeClass
  private void init() {
    List<Map> shipments =
        given()
            .auth()
            .oauth2(Configuration.accessToken)
            .get(Configuration.ROOT_URI + SHIPMENT_SUMMARIES_PATH)
            .then()
            .assertThat()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body(JSON_SCHEMA_VALIDATOR)
            .extract()
            .body()
            .as(List.class);
    carrierBookingRequestReferences =
        shipments.stream()
            .filter(map -> String.valueOf(map.get("documentTypeCode")).equals("CBR"))
            .map(map -> String.valueOf(map.get("carrierBookingRequestReference")))
            .collect(Collectors.toList());
    carrierBookingReferences =
        shipments.stream()
            .filter(map -> String.valueOf(map.get("documentTypeCode")).equals("BKG"))
            .map(map -> String.valueOf(map.get("carrierBookingReference")))
            .collect(Collectors.toList());
  }

  @Test
  public void testGetAllEvents() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .get(Configuration.ROOT_URI + EVENTS_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .header("Current-Page", notNullValue())
        .statusCode(HttpStatus.SC_OK)
        .body(JSON_SCHEMA_VALIDATOR)
        .body("size()", greaterThanOrEqualTo(1))
        .body("collect { it.eventType }", everyItem(equalTo("SHIPMENT")))
        .body("collect {it.documentTypeCode }", everyItem(anyOf(equalTo("CBR"), equalTo("BKG"))))
        .extract()
        .asString();
  }

  @Test
  public void testGetAllCBREvents() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .queryParam("documentTypeCode", "CBR")
        .get(Configuration.ROOT_URI + EVENTS_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .header("Current-Page", notNullValue())
        .statusCode(HttpStatus.SC_OK)
        .body(JSON_SCHEMA_VALIDATOR)
        .body("size()", greaterThanOrEqualTo(1))
        .body("collect { it.eventType }", everyItem(equalTo("SHIPMENT")))
        .body("collect {it.documentTypeCode }", everyItem(equalTo("CBR")))
        .extract()
        .asString();
  }

  @Test
  public void testWithInvalidDocumentTypeCode() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .queryParam("documentTypeCode", "INVALID")
        .get(Configuration.ROOT_URI + EVENTS_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void testGetAllRECEEvents() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .queryParam("shipmentEventTypeCode", "RECE")
        .get(Configuration.ROOT_URI + EVENTS_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .header("Current-Page", notNullValue())
        .statusCode(HttpStatus.SC_OK)
        .body(JSON_SCHEMA_VALIDATOR)
        .body("size()", greaterThanOrEqualTo(1))
        .body("collect { it.eventType }", everyItem(equalTo("SHIPMENT")))
        .body("collect {it.shipmentEventTypeCode }", everyItem(equalTo("RECE")))
        .extract()
        .asString();
  }

  @Test
  public void testInvalidShipmentEventTypeCode() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .queryParam("shipmentEventTypeCode", "INVALID")
        .get(Configuration.ROOT_URI + EVENTS_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void testGetEventsForCarrierBookingRequestReference() {
    carrierBookingRequestReferences.forEach(
        carrierBookingRequestReference ->
            given()
                .auth()
                .oauth2(Configuration.accessToken)
                .when()
                .queryParam("carrierBookingRequestReference", carrierBookingRequestReference)
                .get(Configuration.ROOT_URI + EVENTS_PATH)
                .then()
                .assertThat()
                .header("API-Version", "1.0.0")
                .header("Current-Page", notNullValue())
                .statusCode(HttpStatus.SC_OK)
                .body(JSON_SCHEMA_VALIDATOR)
                .body("size()", greaterThanOrEqualTo(1))
                .body("collect { it.eventType }", everyItem(equalTo("SHIPMENT"))));
  }

  @Test
  public void testGetEventsForCarrierBookingReference() {
    carrierBookingReferences.forEach(
        carrierBookingReference ->
            given()
                .auth()
                .oauth2(Configuration.accessToken)
                .when()
                .queryParam("carrierBookingReference", carrierBookingReference)
                .get(Configuration.ROOT_URI + EVENTS_PATH)
                .then()
                .assertThat()
                .header("API-Version", "1.0.0")
                .header("Current-Page", notNullValue())
                .statusCode(HttpStatus.SC_OK)
                .body(JSON_SCHEMA_VALIDATOR)
                .body("size()", greaterThanOrEqualTo(1))
                .body("collect { it.eventType }", everyItem(equalTo("SHIPMENT"))));
  }

  @Test
  public void testInvalidCarrierBookingReference() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .queryParam("carrierBookingReference", "123456789012345678901234567890123456")
        .get(Configuration.ROOT_URI + EVENTS_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .statusCode(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  public void testEventCreatedDateTimeFormats() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .queryParam("eventCreatedDateTime:gte", "2019-04-01T14:12:56+01:00")
        .get(Configuration.ROOT_URI + "/events")
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(0))
        .body(JSON_SCHEMA_VALIDATOR);
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .queryParam("eventCreatedDateTime:gt", "2019-04-01T14:12:56+01:00")
        .get(Configuration.ROOT_URI + "/events")
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(0))
        .body(JSON_SCHEMA_VALIDATOR);
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .queryParam("eventCreatedDateTime:lte", "2021-08-01T14:12:56+01:00")
        .get(Configuration.ROOT_URI + "/events")
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(0))
        .body(JSON_SCHEMA_VALIDATOR);
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .queryParam("eventCreatedDateTime:lt", "2021-04-01T14:12:56+01:00")
        .get(Configuration.ROOT_URI + "/events")
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(0))
        .body(JSON_SCHEMA_VALIDATOR);
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .queryParam("eventCreatedDateTime:eq", "2021-07-08T10:44:42.08724+02:00")
        .get(Configuration.ROOT_URI + "/events")
        .then()
        .assertThat()
        .statusCode(200)
        .body("size()", greaterThanOrEqualTo(0))
        .body(JSON_SCHEMA_VALIDATOR);
  }

  @Test
  public void testWithLimit() {
    given()
        .auth()
        .oauth2(Configuration.accessToken)
        .when()
        .queryParam("limit", 2)
        .get(Configuration.ROOT_URI + EVENTS_PATH)
        .then()
        .assertThat()
        .header("API-Version", "1.0.0")
        .header("Current-Page", notNullValue())
        .header("Next-Page", notNullValue())
        .statusCode(HttpStatus.SC_OK)
        .body(JSON_SCHEMA_VALIDATOR)
        .body("size()", equalTo(2));
  }

  @Test
  public void testWithDocumentTypeCodeSort() {
    String events = given()
      .auth()
      .oauth2(Configuration.accessToken)
      .when()
      .queryParam("sort", "documentTypeCode:DESC")
      .get(Configuration.ROOT_URI + EVENTS_PATH)
      .then()
      .assertThat()
      .header("API-Version", "1.0.0")
      .header("Current-Page", notNullValue())
      .statusCode(HttpStatus.SC_OK)
      .body(JSON_SCHEMA_VALIDATOR)
      .body("size()", greaterThanOrEqualTo(2))
      .extract().body().asString();

    List<String> documentStatusList = JsonPath.from(events).getList("documentTypeCode");
    Assert.assertEquals("CBR", documentStatusList.get(0));
    Assert.assertEquals("BKG", documentStatusList.get(documentStatusList.size()-1));
  }

  @Test
  public void testWithEventDateTimeSort() {
    String events = given()
      .auth()
      .oauth2(Configuration.accessToken)
      .when()
      .queryParam("sort", "eventDateTime:DESC")
      .get(Configuration.ROOT_URI + EVENTS_PATH)
      .then()
      .assertThat()
      .header("API-Version", "1.0.0")
      .header("Current-Page", notNullValue())
      .statusCode(HttpStatus.SC_OK)
      .body(JSON_SCHEMA_VALIDATOR)
      .body("size()", greaterThanOrEqualTo(2))
      .extract().body().asString();

    List<String> eventDateTimeList = JsonPath.from(events).getList("eventDateTime");
    Assert.assertTrue(OffsetDateTime.parse(eventDateTimeList.get(0)).isAfter(OffsetDateTime.parse(eventDateTimeList.get(eventDateTimeList.size() -1))));
  }
}
