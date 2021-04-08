package org.dcsa.api_validator.ovs.v1.portcallevents;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class PostPortCallEventsTest {

    @Test
    public void testPostNewETABerth() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T22:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"operationsEventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"BRTH\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewRTABerth() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T22:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"REQ\",\n" +
                        "    \"operationsEventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"BRTH\",\n" +
                        "    \"publisher\": \"DEHAMCTA\",\n" +
                        "    \"publisherRole\": \"TR\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewPTABerth() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T22:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"PLN\",\n" +
                        "    \"operationsEventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"BRTH\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }


    @Test
    public void testPostNewETAPBP() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T19:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"operationsEventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"PBPL\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewRTAPBP() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T19:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"REQ\",\n" +
                        "    \"operationsEventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"PBPL\",\n" +
                        "    \"publisher\": \"DEHAM\",\n" +
                        "    \"publisherRole\": \"POR\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewPTAPBP() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T19:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"PLN\",\n" +
                        "    \"operationsEventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"PBPL\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewATAPBP() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T19:30:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"ACT\",\n" +
                        "    \"operationsEventTypeCode\": \"ARRI\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"PBPL\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewATSPilot() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-07T19:30:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"ACT\",\n" +
                        "    \"operationsEventTypeCode\": \"STRT\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"portCallServiceTypeCode \": \"PILO\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewATSCOPS() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-08T05:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"ACT\",\n" +
                        "    \"operationsEventTypeCode\": \"STRT\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"portCallServiceTypeCode \": \"CRGO\",\n" +
                        "    \"publisher\": \"DEHAMCTA\",\n" +
                        "    \"publisherRole\": \"TR\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewETCCOPS() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-09T01:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"operationsEventTypeCode\": \"CMPL\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"portCallServiceTypeCode \": \"CRGO\",\n" +
                        "    \"publisher\": \"DEHAMCTA\",\n" +
                        "    \"publisherRole\": \"TR\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewATCCOPS() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-09T02:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"ACT\",\n" +
                        "    \"operationsEventTypeCode\": \"CMPL\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"portCallServiceTypeCode \": \"CRGO\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewETDBerth() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-09T16:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"EST\",\n" +
                        "    \"operationsEventTypeCode\": \"DEPA\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"BRTH\",\n" +
                        "    \"publisher\": \"EXP\",\n" +
                        "    \"publisherRole\": \"CA\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    @Test
    public void testPostNewRTDBerth() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "  \"eventType\": \"OPERATIONS\",\n" +
                        "    \"eventDateTime\": \"2021-04-09T16:00:00+02:00\",\n" +
                        "    \"eventClassifierCode\": \"REQ\",\n" +
                        "    \"operationsEventTypeCode\": \"DEPA\",\n" +
                        "    \"transportCallID\": \"" + this.queryForTransportCallID() + "\",\n" +
                        "    \"facilityTypeCode\": \"BRTH\",\n" +
                        "    \"publisher\": \"DEHAM\",\n" +
                        "    \"publisherRole\": \"POR\",\n" +
                        "    \"eventLocation\": \"Meter 100-300\",\n" +
                        "    \"changeRemark\": null,\n" +
                        "    \"delayReasonCode\": null," +
                        "    \"eventCreatedDateTime\": \"2021-01-30T15:00:00+02:00\"" +
                        "}\n").
                when().post(Configuration.ROOT_URI + "/transport-calls/operations-events").
                then().body("$", hasKey("eventID"));
    }

    private String queryForTransportCallID() {

        String scheduleID = given().auth().oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-calls").body().jsonPath().getList("transportCallID").get(0).toString();
        Assert.assertNotNull(scheduleID);
        return scheduleID;
    }

}
