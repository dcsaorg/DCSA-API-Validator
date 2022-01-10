package org.dcsa.api_validator.bkg.v1.cancellations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class CancelBookingTest {

	JSONObject cancellationRequest;
	String bookingAllowedToCancel;
	List<String> bookingsNotAllowedToCancel;

	@BeforeClass
	private void createCancellationRequest() throws JsonProcessingException {
		List<String> allowedStatusses = Arrays.asList("RECE", "PENU", "CONF");
		JSONObject cancellation = new JSONObject();
		cancellation.put("documentStatus", "CANC");
		cancellation.put("reason", "booking cancelled during test");
		cancellationRequest = cancellation;

		bookingAllowedToCancel = createBooking().get("carrierBookingRequestReference").toString();
		bookingsNotAllowedToCancel = findAllBookings().stream().filter(map -> !allowedStatusses.contains(map.get("documentStatus"))).map(map -> String.valueOf(map.get("carrierBookingRequestReference"))).collect(Collectors.toList());
	}

	//Create a minimal booking to cancel, so it does not effect the base test data
	private Map createBooking() throws JsonProcessingException {

		Map<String, Object> bookingRequest = new LinkedHashMap<>();
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

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonBookingRequest = objectMapper.writeValueAsString(bookingRequest);

		return given()
			.auth()
			.oauth2(Configuration.accessToken)
			.contentType(ContentType.JSON)
			.body(jsonBookingRequest)
			.post(Configuration.ROOT_URI + BOOKING_PATH)
			.then()
			.assertThat()
			.statusCode(202)
			.body(JSON_SCHEMA_VALIDATOR)
			.extract().body().as(Map.class);
	}

	private List<Map> findAllBookings() {
		return given()
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
			.as(List.class);
	}

	@Test
	public void testCancelBookingInvalidCarrierBookingRequestReference() {
		given()
			.auth()
			.oauth2(Configuration.accessToken)
			.when()
			.pathParam(
				"carrierBookingRequestReference",
				"12345678912345678901235678945651205451686156465154515564845156754845678465544567845648456548151554234")
			.body(cancellationRequest.toString())
			.contentType(ContentType.JSON)
			.patch(Configuration.ROOT_URI + BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH)
			.then()
			.assertThat()
			.statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	public void testCancelBookingInvalidRequest() {

		JSONObject cancellation = new JSONObject();
		cancellation.put("documentStatus", "PENU");
		cancellation.put("reason", "booking cancelled during test");

		given()
			.auth()
			.oauth2(Configuration.accessToken)
			.when()
			.pathParam(
				"carrierBookingRequestReference",
				"123456789")
			.body(cancellation.toString())
			.contentType(ContentType.JSON)
			.patch(Configuration.ROOT_URI + BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH)
			.then()
			.assertThat()
			.statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	public void testCancelBookingInInvalidDocumentStatus() {

		given()
			.auth()
			.oauth2(Configuration.accessToken)
			.when()
			.pathParam(
				"carrierBookingRequestReference",
				bookingsNotAllowedToCancel.get(0))
			.body(cancellationRequest.toString())
			.contentType(ContentType.JSON)
			.patch(Configuration.ROOT_URI + BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH)
			.then()
			.assertThat()
			.statusCode(HttpStatus.SC_BAD_REQUEST);
	}

	@Test
	public void testCancelBookingSuccessful() {
		given()
			.auth()
			.oauth2(Configuration.accessToken)
			.when()
			.pathParam("carrierBookingRequestReference", bookingAllowedToCancel)
			.body(cancellationRequest.toString())
			.contentType(ContentType.JSON)
			.patch(Configuration.ROOT_URI + BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH)
			.then()
			.assertThat()
			.statusCode(HttpStatus.SC_OK)
			.body(JSON_SCHEMA_VALIDATOR);
	}

}
