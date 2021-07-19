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

    @Test//(enabled = false)
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
                body("").
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);
    }

    // These are mandatory parameters and is the main test
    @Test//(enabled = false)
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

    @Test//(enabled = false)
    public void testFacilitySMDGCodeQueryParam() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("eventLocation");
        map.remove("vesselPosition");
        map.remove("modeOfTransport");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testFacilitySMDGCodeQueryParam").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Test facilitySMDGCode, fails as specification -> maxLength:6 & ENUM
    @Test//(enabled = false)
    public void testFacilitySMDGCodeFalseFormat() {

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

        map.put("facilitySMDGCode", "aBC");

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

    @Test//(enabled = false)
    public void testEventLocationQueryParam() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
        map.remove("vesselPosition");
        map.remove("modeOfTransport");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testEventLocationQueryParam").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Test EventLocation, fails as format is JSON.
    @Test//(enabled = false)
    public void testEventLocationFalseFormat() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
        map.remove("vesselPosition");
        map.remove("modeOfTransport");
        map.remove("portCallServiceTypeCode");

        map.put("eventLocation","aBCDaFGHaE"); // Wrong format

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(400);

        map.put("eventLocation", "{}"); // Empty (fails Maybe - does not include mandatory parameters)

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

    @Test//(enabled = false)
    public void testVesselPositionQueryParam() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
        map.remove("modeOfTransport");
        map.remove("eventLocation");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testVesselPositionQueryParam").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Test vesselPosition, fails as as format is JSON & (latitude & longitude required).
    @Test//(enabled = false)
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

        map.put("vesselPosition", "{}"); // Empty (fails - includes mandatory parameters)

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

    @Test//(enabled = false)
    public void testModeOfTransportQueryParam() {

        Map<String, String> map = (Map<String, String>) jsonToMap(VALID_TIMESTAMP);
        map.remove("facilitySMDGCode");
        map.remove("vesselPosition");
        map.remove("eventLocation");
        map.remove("portCallServiceTypeCode");

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                header("testname","testModeOfTransportQueryParam").
                body(map).
                post(Configuration.ROOT_URI + "/timestamps").
                then().
                assertThat().
                statusCode(204);
    }

    // Test modeOfTransport, fails as VES is not ENUM.
    @Test//(enabled = false)
    public void testModeOfTransportFalseFormat() {
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

        map.put("vesselPosition", "{}"); // Empty (fails - includes mandatory parameters)

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

    @Test//(enabled = false)
    public void testMandatoryPublisherQueryParamFalseFormat() {

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
        map.put("publisher", "{}"); // Empty (fails Maybe - does not include mandatory parameters)
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
        map.put("publisher", ""); // Empty (fails Maybe - does not include mandatory parameters)
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

    @Test//(enabled = false)
    public void testMandatoryPublisherRoleQueryParamFalseFormat() {
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

    // Missing mandatory VesselIMONumber parameter
    @Test//(enabled = false)
    public void testMandatoryVesselIMONumberQueryParamFalseFormat() {

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

    @Test//(enabled = false)
    public void testMandatoryUNLocationCodeQueryParamFalseFormat() {
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

    @Test//(enabled = false)
    public void testMandatoryFacilityTypeCodeQueryParamFalseFormat() {

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

    // Missing mandatory EventClassifierCode parameter
    @Test//(enabled = false)
    public void testMandatoryEventClassifierCodeQueryParamFalseFormat() {

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

    // Missing mandatory OperationsEventTypeCode parameter
    @Test//(enabled = false)
    public void testMandatoryOperationsEventTypeCodeQueryParamFalseFormat() {

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

    // Missing mandatory EventDateTime parameter
    @Test//(enabled = false)
    public void testMandatoryEventDateTimeQueryParamFalseFormat() {

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
        map.put("eventDateTime", "sdf"); // Empty
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
