package org.dcsa.api_validator.ovs.v1.transportcalls;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class PostTransportCallsTest {





    @Test
    public void testPostNewTransportCall(){
        //@ToDo Change IMO Number to 6409715 after bugfix on core is done
        String vesselIMONumber = "6409715";
        String vesselName = "MV Kooringa";
        String UNLocationCode =  "NLRTM";
        String UNLocationName = "Rotterdamm";
        String facilityCode = "NLRTMAPM";


        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(" \n" +
                        "  {\n" +
                        "    \"vesselIMONumber\": \""+vesselIMONumber+ "\",\n" +
                        "    \"vesselName\": \""+vesselName+ "\",\n" +
                        "    \"transportCallSequenceNumber\": 2,\n" +
                        "    \"facilityTypeCode\": \"POTE\",\n" +
                        "    \"facilityCode\": \""+facilityCode+"\",\n" +
                        "    \"otherFacility\": \"\"\n" +
                        "  }\n" +
                        "").
                when().
                post(Configuration.ROOT_URI + "/transport-calls").
                then().body("vesselIMONumber", equalTo(vesselIMONumber)).
                body("vesselName", equalTo(vesselName)).
                body("facilityCode", equalTo(facilityCode)).
                body("$", hasKey("transportCallID"));

        }



}
