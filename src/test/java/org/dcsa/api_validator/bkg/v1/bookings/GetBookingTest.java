package org.dcsa.api_validator.bkg.v1.bookings;

import org.apache.http.HttpStatus;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.BOOKING_PATH;
import static org.dcsa.api_validator.bkg.v1.BookingTestConfiguration.JSON_SCHEMA_VALIDATOR;

public class GetBookingTest {

	@Test
	public void testGetValidBookingTest() {

		given()
			.auth()
			.oauth2(Configuration.accessToken)
		.when()
			.pathParam("carrierBookingRequestReference", "ef223019-ff16-4870-be69-9dbaaaae9b11")
			.get(Configuration.ROOT_URI + BOOKING_PATH)
		.then()
			.assertThat()
			.header("API-Version", "1.0.0")
			.statusCode(HttpStatus.SC_OK)
			.body(JSON_SCHEMA_VALIDATOR);
	}

	@Test
	public void testGetBookingSomeEmptyDeepObjects() {
		given()
			.auth()
			.oauth2(Configuration.accessToken)
		.when()
			.pathParam("carrierBookingRequestReference", "CARRIER_BOOKING_REQUEST_REFERENCE_01")
			.get(Configuration.ROOT_URI + BOOKING_PATH)
		.then()
			.assertThat()
			.header("API-Version", "1.0.0")
			.statusCode(HttpStatus.SC_OK)
			.body(JSON_SCHEMA_VALIDATOR);
	}

	@Test
	public void testWithInvalidCarrierBookingRequestReference() {
		given()
			.auth()
			.oauth2(Configuration.accessToken)
		.when()
			.pathParam("carrierBookingRequestReference", "12345678912345678901235678945651205451686156465154515564845156754845678465544567845648456548151554234")
			.get(Configuration.ROOT_URI + BOOKING_PATH)
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
			.get(Configuration.ROOT_URI + BOOKING_PATH)
		.then()
			.assertThat()
			.statusCode(HttpStatus.SC_NOT_FOUND);
	}
}
