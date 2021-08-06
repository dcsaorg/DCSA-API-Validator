package org.dcsa.api_validator.ovs.v2;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.dcsa.api_validator.TestUtil.loadFileAsString;
import static org.dcsa.api_validator.TestUtil.jsonToMap;

/*
 * Tests related to the POST /Timestamps endpoint
 */

// TODO: 1. Add reasons and Errors to the 400 bad request tests.
// TODO: 2. ENABLE ALL TESTS

public class PostTimestampsTest {

    public static final String VALID_TIMESTAMP = loadFileAsString("ovs/v2/ovsTimeStamps/TimeStampsSample.json");

    // Testing with all fields provided in VALID_TIMESTAMP variable
    @Test(enabled = false)
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
    @Test(enabled = false)
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

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
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
    @Test(enabled = false)
    public void testFacilitySMDGCodeFieldFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
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

    // Testing with mandatory fields + EventLocation field
    @Test(enabled = false)
    public void testEventLocationField() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
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
    @Test(enabled = false)
    public void testEventLocationFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
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
    @Test(enabled = false)
    public void testVesselPositionField() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
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
    @Test(enabled = false)
    public void testVesselPositionFalseFormat() {
        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
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

        map.put("vesselPosition", "{}"); // Empty (fails - vesselPosition includes mandatory parameters)

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
    @Test(enabled = false)
    public void testModeOfTransportField() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
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
    @Test(enabled = false)
    public void testModeOfTransportFieldFalseFormat() {
        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
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
    @Test(enabled = false)
    public void testPortCallServiceTypeCodeField() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
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
    @Test(enabled = false)
    public void testPortCallServiceTypeCodeFalseFormat() {
        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
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
    @Test(enabled = false)
    public void testMandatoryPublisherFieldFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
    @Test(enabled = false)
    public void testMandatoryPublisherRoleFieldFalseFormat() {
        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
    @Test(enabled = false)
    public void testMandatoryVesselIMONumberFieldFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
    @Test(enabled = false)
    public void testMandatoryUNLocationCodeFieldFalseFormat() {
        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
    @Test(enabled = false)
    public void testMandatoryFacilityTypeCodeFieldFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
    @Test(enabled = false)
    public void testMandatoryEventClassifierCodeFieldFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
    @Test(enabled = false)
    public void testMandatoryOperationsEventTypeCodeFieldFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
    @Test(enabled = false)
    public void testMandatoryEventDateTimeFieldFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);

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
