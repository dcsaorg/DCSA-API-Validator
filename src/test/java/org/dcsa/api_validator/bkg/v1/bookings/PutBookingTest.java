package org.dcsa.api_validator.bkg.v1.bookings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BOOKING_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.JSON_SCHEMA_VALIDATOR;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

class PutBookingTest {

  private String createdBookingCarrierBookingRequestReference;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private Map<String, Object> bookingRequest = new LinkedHashMap<>();

  @BeforeClass
  public void createAValidBooking() throws JsonProcessingException {

    bookingRequest.put("carrierBookingRequestReference", "");
    bookingRequest.put("receiptTypeAtOrigin", "CY");
    bookingRequest.put("deliveryTypeAtDestination", "CY");
    bookingRequest.put("cargoMovementTypeAtOrigin", "FCL");
    bookingRequest.put("cargoMovementTypeAtDestination", "LCL");
    bookingRequest.put("serviceContractReference", "Test");
    bookingRequest.put("isPartialLoadAllowed", true);
    bookingRequest.put("isExportDeclarationRequired", false);
    bookingRequest.put("isImportLicenseRequired", false);
    bookingRequest.put("submissionDateTime", "2021-11-03T11:41:00+01:00");
    bookingRequest.put("communicationChannel", "AO");
    bookingRequest.put("expectedDepartureDate", "2021-05-17");
    bookingRequest.put("isEquipmentSubstitutionAllowed", true);

    Map<String, Object> commodity = new LinkedHashMap<>();
    commodity.put("commodityType", "Bloom");
    commodity.put("cargoGrossWeight", 2000.0);
    commodity.put("cargoGrossWeightUnit", "LBR");
    bookingRequest.put("commodities", Arrays.asList(commodity));

    createdBookingCarrierBookingRequestReference =
        given()
            .contentType("application/json")
            .body(objectMapper.writeValueAsString(bookingRequest))
            .post(Configuration.ROOT_URI + BOOKING_PATH)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_ACCEPTED)
            .body("size()", greaterThanOrEqualTo(0))
            .body(JSON_SCHEMA_VALIDATOR)
            .extract()
            .body()
            .path("carrierBookingRequestReference")
            .toString();
  }

  @Test
  public void testPUTBookingUpdateShallowValue() throws JsonProcessingException {
    Map<Object, Object> createdBooking =
        given()
            .contentType("application/json")
            .get(
                Configuration.ROOT_URI
                    + BOOKING_PATH
                    + "/"
                    + createdBookingCarrierBookingRequestReference)
            .body()
            .as(Map.class);

    createdBooking.computeIfPresent("serviceContractReference", (k, v) -> "test-update");

    String body = objectMapper.writeValueAsString(createdBooking);

    given()
        .log()
        .body()
        .contentType("application/json")
        .body(body)
        .put(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .asString();

    given()
        .contentType("application/json")
        .get(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .body("serviceContractReference", equalTo("test-update"))
        .extract()
        .asString();
  }

  @Test
  public void testPUTBookingUpdateCommodity() throws JsonProcessingException {
    Map<Object, Object> createdBooking =
        given()
            .contentType("application/json")
            .get(
                Configuration.ROOT_URI
                    + BOOKING_PATH
                    + "/"
                    + createdBookingCarrierBookingRequestReference)
            .body()
            .as(Map.class);

    Map<String, Object> commodity = new LinkedHashMap<>();
    commodity.put("commodityType", "Corn");
    commodity.put("cargoGrossWeight", 2000.0);
    commodity.put("cargoGrossWeightUnit", "KGM");

    createdBooking.computeIfPresent("commodities", (k, v) -> Arrays.asList(commodity));

    String body = objectMapper.writeValueAsString(createdBooking);

    given()
        .log()
        .body()
        .contentType("application/json")
        .body(body)
        .put(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .asString();

    given()
        .contentType("application/json")
        .get(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .body("commodities[0].commodityType", equalTo("Corn"))
        .body("commodities[0].cargoGrossWeightUnit", equalTo("KGM"))
        .extract()
        .asString();
  }

  @Test
  public void testPUTBookingUpdateValueAddedService() throws JsonProcessingException {
    Map<Object, Object> createdBooking =
        given()
            .contentType("application/json")
            .get(
                Configuration.ROOT_URI
                    + BOOKING_PATH
                    + "/"
                    + createdBookingCarrierBookingRequestReference)
            .body()
            .as(Map.class);

    Map<String, Object> valueAddedService = new LinkedHashMap<>();
    valueAddedService.put("valueAddedServiceCode", "SCON");

    createdBooking.computeIfPresent(
        "valueAddedServiceRequests", (k, v) -> Arrays.asList(valueAddedService));

    String body = objectMapper.writeValueAsString(createdBooking);

    given()
        .log()
        .body()
        .contentType("application/json")
        .body(body)
        .put(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .asString();

    given()
        .contentType("application/json")
        .get(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .body("valueAddedServiceRequests[0].valueAddedServiceCode", equalTo("SCON"))
        .extract()
        .asString();
  }

  @Test
  public void testPUTBookingUpdateReference() throws JsonProcessingException {
    Map<Object, Object> createdBooking =
        given()
            .contentType("application/json")
            .get(
                Configuration.ROOT_URI
                    + BOOKING_PATH
                    + "/"
                    + createdBookingCarrierBookingRequestReference)
            .body()
            .as(Map.class);

    Map<String, Object> reference = new LinkedHashMap<>();
    reference.put("referenceType", "SI");
    reference.put("referenceValue", "TEST");

    createdBooking.computeIfPresent("references", (k, v) -> Arrays.asList(reference));

    String body = objectMapper.writeValueAsString(createdBooking);

    given()
        .log()
        .body()
        .contentType("application/json")
        .body(body)
        .put(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .asString();

    given()
        .contentType("application/json")
        .get(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .body("references[0].referenceType", equalTo("SI"))
        .body("references[0].referenceValue", equalTo("TEST"))
        .extract()
        .asString();
  }

  @Test
  public void testPUTBookingUpdateRequestedEquipment() throws JsonProcessingException {
    Map<Object, Object> createdBooking =
        given()
            .contentType("application/json")
            .get(
                Configuration.ROOT_URI
                    + BOOKING_PATH
                    + "/"
                    + createdBookingCarrierBookingRequestReference)
            .body()
            .as(Map.class);

    Map<String, Object> requestedEquipment = new LinkedHashMap<>();
    requestedEquipment.put("requestedEquipmentSizetype", "22GP");
    requestedEquipment.put("requestedEquipmentUnits", 6);
    requestedEquipment.put("isShipperOwned", "true");

    createdBooking.computeIfPresent(
        "requestedEquipments", (k, v) -> Arrays.asList(requestedEquipment));

    String body = objectMapper.writeValueAsString(createdBooking);

    given()
        .log()
        .body()
        .contentType("application/json")
        .body(body)
        .put(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .asString();

    given()
        .contentType("application/json")
        .get(
            Configuration.ROOT_URI
                + BOOKING_PATH
                + "/"
                + createdBookingCarrierBookingRequestReference)
        .then()
        .assertThat()
        .statusCode(HttpStatus.SC_OK)
        .body("requestedEquipments.size()", greaterThanOrEqualTo(1))
        .body("requestedEquipments[0].requestedEquipmentSizetype", equalTo("22GP"))
        .body("requestedEquipments[0].isShipperOwned", equalTo(true))
        .extract()
        .asString();
  }
}
