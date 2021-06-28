package org.dcsa.api_validator.ovs.v2.transportcalls;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class PostTransportCallsTest {

    @Test
    public void testPostNewTransportCall(){
        String facilityCode = "NLRTMAPM";
        int transportCallSequenceNumber = 2;
        String facilityTypeCode = "POTE";

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(" \n" +
                        "  {\n" +
                        "    \"transportCallSequenceNumber\": " + transportCallSequenceNumber + ",\n" +
                        "    \"facilityTypeCode\": \"" + facilityTypeCode + "\",\n" +
                        "    \"facilityCode\": \"" + facilityCode + "\",\n" +
                        "    \"otherFacility\": \"\"\n" +
                        "  }\n" +
                        "").
                when().
                post(Configuration.ROOT_URI + "/transport-calls").
                then().
                body("transportCallSequenceNumber", equalTo(2)).
                body("facilityTypeCode", equalTo("POTE")).
                body("facilityCode", equalTo(facilityCode)).
                body("$", hasKey("transportCallID"));

        }

}
