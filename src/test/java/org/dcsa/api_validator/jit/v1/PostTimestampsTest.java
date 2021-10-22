package org.dcsa.api_validator.jit.v1;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.TestUtil.loadFileAsString;
import static org.dcsa.api_validator.TestUtil.jsonToMap;

/*
 * Tests related to the POST /Timestamps endpoint
 */

public class PostTimestampsTest {

    public static final String VALID_TIMESTAMP = loadFileAsString("jit/v1/jitTimeStamps/TimeStampsSample.json");

    // Testing with all fields provided in VALID_TIMESTAMP variable
    @Test
    public void testTimestampRequiredParameters() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testTimestampRequiredParameters").
                body(VALID_TIMESTAMP).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Testing with no fields / Empty body
    // Should fail as nothing is provided
    @Test
    public void testTransportCallsNoMandatoryParameters() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testTransportCallsNoMandatoryParameters").
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testTransportCallsNoMandatoryParameters2").
                body("").                                                       // Note: Empty body
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }


    // Testing with mandatory fields + FacilitySMDGCode field
    @Test
    public void testFacilitySMDGCodeField() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("eventLocation");
        map.remove("vesselPosition");
        map.remove("modeOfTransport");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testFacilitySMDGCodeField").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Test // Testing with mandatory fields + FacilitySMDGCode field
    // Should fail because specification -> maxLength:6
    @Test
    public void testFacilitySMDGCodeFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("eventLocation");
        map.remove("vesselPosition");
        map.remove("modeOfTransport");
        map.remove("portCallServiceTypeCode");

        map.put("facilitySMDGCode","aBCDaFGHaE");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields + EventLocation field (DDT-340)
    @Test
    public void testEventLocationField() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
        map.remove("vesselPosition");
        map.remove("modeOfTransport");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testEventLocationField").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Testing with mandatory fields + EventLocation field
    // fails as eventLocation is an object.
    @Test
    public void testEventLocationFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("vesselPosition");
        map.remove("modeOfTransport");
        map.remove("portCallServiceTypeCode");

        map.put("eventLocation","aBCDaFGHaE"); // Wrong format - eventLocation is an object

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

    }

    // Testing with mandatory fields + VesselPosition field
    @Test
    public void testVesselPositionField() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("modeOfTransport");
        map.remove("eventLocation");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testVesselPositionField").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Testing with mandatory fields + vesselPosition field
    // fails as vesselPosition is an object & (latitude & longitude are required parameters).
    @Test
    public void testVesselPositionFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("modeOfTransport");
        map.remove("eventLocation");
        map.remove("portCallServiceTypeCode");

        map.put("vesselPosition","aBCDaFGHaE"); // Wrong format

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty (fails - vesselPosition includes mandatory parameters)
        map.put("vesselPosition", new HashMap<>());

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields + ModeOfTransport field
    @Test
    public void testModeOfTransportField() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("vesselPosition");
        map.remove("eventLocation");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testModeOfTransportField").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Testing with mandatory fields + ModeOfTransport field
    // fails as ModeOfTransport is an ENUM.
    @Test
    public void testModeOfTransportFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
        map.remove("modeOfTransport");
        map.remove("eventLocation");
        map.remove("portCallServiceTypeCode");

        map.put("modeOfTransport","VES"); // Wrong format ( VES -- is not ENUM)

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        map.put("modeOfTransport", ""); // Empty (fails - not ENUM)

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields + PortCallServiceTypeCode field
    @Test
    public void testPortCallServiceTypeCodeField() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("vesselPosition");
        map.remove("eventLocation");
        map.remove("modeOfTransport");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testPortCallServiceTypeCodeField").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Testing with mandatory fields + PortCallServiceTypeCode field
    // fails as portCallServiceTypeCode is an ENUM.
    // Test modeOfTransport,
    @Test
    public void testPortCallServiceTypeCodeFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
        map.remove("modeOfTransport");
        map.remove("eventLocation");
        map.remove("modeOfTransport");

        map.put("portCallServiceTypeCode","VES"); // Wrong format ( VES -- is not ENUM)

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        map.put("portCallServiceTypeCode", ""); // Empty (fails - not ENUM)

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except PublisherField field
    // Should fail as PublisherField is mandatory
    @Test
    public void testMandatoryPublisherFieldFalseFormat() {

        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("publisher");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Wrong format Publisher parameter
        map.put("publisher", "{sasds}");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty Publisher parameter
        map.put("publisher", "{}"); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Wrong format
        map.put("publisher", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except publisherRole field
    // Should fail as publisherRole is mandatory
    @Test
    public void testMandatoryPublisherRoleFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("publisherRole");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty
        map.put("publisherRole", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except vesselIMONumber field
    // Should fail as vesselIMONumber is mandatory
    @Test
    public void testMandatoryVesselIMONumberFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("vesselIMONumber");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // wrong format maxLength:7
        map.put("vesselIMONumber", "abcdfght");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty
        map.put("vesselIMONumber", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except UNLocationCode field
    // Should fail as UNLocationCode is mandatory
    @Test
    public void testMandatoryUNLocationCodeFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("UNLocationCode");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // wrong format maxLength:5
        map.put("UNLocationCode", "abcdfght");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty value
        map.put("UNLocationCode", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except FacilityTypeCode field
    // Should fail as FacilityTypeCode is mandatory
    @Test
    public void testMandatoryFacilityTypeCodeFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("facilityTypeCode");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // wrong format -> ENUM
        map.put("facilityTypeCode", "abcdfght");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty value
        map.put("facilityTypeCode", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except EventClassifierCode field
    // Should fail as EventClassifierCode is mandatory
    @Test
    public void testMandatoryEventClassifierCodeFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("eventClassifierCode");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty value
        map.put("eventClassifierCode", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except OperationsEventTypeCode field
    // Should fail as OperationsEventTypeCode is mandatory
    @Test
    public void testMandatoryOperationsEventTypeCodeFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("operationsEventTypeCode");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty value
        map.put("operationsEventTypeCode", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // Testing with mandatory fields - Except EventDateTime field
    // Should fail as EventDateTime is mandatory
    @Test
    public void testMandatoryEventDateTimeFieldFalseFormat() {
        Map<String, Object> map = jsonToMap(VALID_TIMESTAMP);

        map.remove("eventDateTime");
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Wrong value
        map.put("eventDateTime", "sdf"); // wrong format
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        // Empty value
        map.put("eventDateTime", ""); // Empty
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }
}
