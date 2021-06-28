package org.dcsa.api_validator.ovs.v2.portcallevents;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class PostPortCallEventsTest {

    @Test
    public void testPostNewETABerth(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T22:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"eventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"BERTH\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewRTABerth(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T22:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"REQ\",\n" +
                        "    \"eventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"BERTH\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": \"Delay due to bad Weather!\",\n" +
                        "    \"delayReasonCode\": \"WEA\"," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewPTABerth(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T22:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"PLN\",\n" +
                        "    \"eventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"BERTH\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }


    @Test
    public void testPostNewETAPBP(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"eventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"PBP\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewRTAPBP(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"REQ\",\n" +
                        "    \"eventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"PBP\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewPTAPBP(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"PLN\",\n" +
                        "    \"eventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"PBP\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewATAPBP(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"ACT\",\n" +
                        "    \"eventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"PBP\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewETCCOPS(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"eventTypeCode\": \"COPS\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"CARGO_OPS\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewATCCOPS(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"ACT\",\n" +
                        "    \"eventTypeCode\": \"COPS\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"CARGO_OPS\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewETDBerth(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"eventTypeCode\": \"DEPT\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"BERTH\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewRBerth(){
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(   "  {\n" +
                        "  \"eventType\": \"TRANSPORT\",\n" +
                        "    \"eventDateTime\": \"2021-01-30T20:20:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"REQ\",\n" +
                        "    \"eventTypeCode\": \"DEPT\",\n" +
                        "    \"transportCallID\": \""+this.queryForTransportCallID()+"\",\n" +
                        "    \"locationType\": \"BERTH\",\n" +
                        "    \"locationID\": \"Meter 100-300\",\n" +
                        "    \"comment\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"creationDateTime\": \"2021-01-01T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/transport-events").
                then().body("$", hasKey("eventID"));
    }

    private String queryForTransportCallID() {

        String scheduleID = given().auth().oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-calls").body().jsonPath().getList("transportCallID").get(0).toString();
        Assert.assertNotNull(scheduleID);
        return scheduleID;
    }

}
