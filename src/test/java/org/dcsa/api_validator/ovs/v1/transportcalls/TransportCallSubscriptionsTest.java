package org.dcsa.api_validator.ovs.v1.transportcalls;

import org.dcsa.api_validator.conf.Configuration;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import spark.Request;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class TransportCallSubscriptionsTest {
    //Don't reuse request objects to reduce risk of other unrelated events affecting the tests
    private Request transportCallRequest1;
    private Request transportCallRequest2;
    private CountDownLatch lock = new CountDownLatch(1); //Initialize countdown at 1, when count is 0 lock is released
    private CountDownLatch lock2 = new CountDownLatch(1); //Initialize countdown at 1, when count is 0 lock is released

    @BeforeMethod
    void setup() {
        cleanUp();
        Spark.port(Configuration.getCallbackListenPort());
        Spark.post("v1/webhook/receive-transport-calls-1", (req, res) -> {
            if(req.body()==null) return "Ignoring null callback received"; //Not sure why this sometimes happens. May be a problem in the API, for now we ignore it to avoid tests failing sporadically
            this.transportCallRequest1 = req;
            lock.countDown(); //Release lock
            return "Callback received!";
        });
        Spark.post("v1/webhook/receive-transport-calls-2", (req, res) -> {
            if(req.body()==null) return "Ignoring null callback received"; //Not sure why this sometimes happens. May be a problem in the API, for now we ignore it to avoid tests failing sporadically
            this.transportCallRequest2 = req;
            lock2.countDown(); //Release lock
            return "Callback received!";
        });
        Spark.awaitInitialization();


    }

    private void cleanUp() {
        this.transportCallRequest1 = null;
        this.transportCallRequest2 = null;
        lock = new CountDownLatch(1); //Initialize countdown at 1, when count is 0 lock is released
        lock2 = new CountDownLatch(1); //Initialize countdown at 1, when count is 0 lock is released
    }

    @AfterMethod
    void shutdown() {
        Spark.stop();
        Spark.awaitStop();
    }

    @Test
    public void testCallbacks() throws InterruptedException, IOException, JSONException {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(" \n" +
                        "  {\n" +
                        "    \"scheduleID\": \"" + queryForScheduleID() + "\",\n" +
                        "    \"carrierServiceCode\": \"FE1\",\n" +
                        "    \"vesselIMONumber\": \"1801322\",\n" +
                        "    \"vesselName\": \"Vessel A\",\n" +
                        "    \"carrierVoyageNumber\": \"2015W\",\n" +
                        "    \"UNLocationCode\": \"USNYC\",\n" +
                        "    \"UNLocationName\": \"New York\",\n" +
                        "    \"transportCallNumber\": 2,\n" +
                        "    \"facilityTypeCode\": \"POTE\",\n" +
                        "    \"facilityCode\": \"AEAUHADT\",\n" +
                        "    \"otherFacility\": \"Depot location or address\"\n" +
                        "  }\n" +
                        "").
                post(Configuration.ROOT_URI + "/transport-calls");

        lock.await(20000, TimeUnit.MILLISECONDS); //Released immediately if lock countdown is 0
        Assert.assertNotNull(transportCallRequest1, "The callback request should not be null");
        Assert.assertNotNull(transportCallRequest1.body(), "The callback request body should not be null");
        String jsonBody = transportCallRequest1.body();

        System.out.println("The testCallbacks() test received the body: " + jsonBody);
        //Validate that the callback body is a Shipment Event
        JSONObject jsonSubject = new JSONObject(jsonBody);

        try (InputStream inputStream = getClass().getResourceAsStream("/ovs/v1/TransportCallSchema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(jsonSubject); // throws a ValidationException if this object is invalid
        }

    }

    //Disabled since filters are not implemented yet
    @Test(enabled=false)
    public void testCallbackFilter() throws InterruptedException, JSONException {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(" \n" +
                        "  {\n" +
                        "    \"scheduleID\": \"" + queryForScheduleID() + "\",\n" +
                        "    \"carrierServiceCode\": \"FE1\",\n" +
                        "    \"vesselIMONumber\": \"1801322\",\n" +
                        "    \"vesselName\": \"Vessel A\",\n" +
                        "    \"carrierVoyageNumber\": \"2015W\",\n" +
                        "    \"UNLocationCode\": \"USNYC\",\n" +
                        "    \"UNLocationName\": \"New York\",\n" +
                        "    \"transportCallNumber\": 2,\n" +
                        "    \"facilityTypeCode\": \"POTE\",\n" +
                        "    \"facilityCode\": \"AEAUHADT\",\n" +
                        "    \"otherFacility\": \"Depot location or address\"\n" +
                        "  }\n" +
                        "").
                post(Configuration.ROOT_URI + "/transport-calls");

        lock2.await(3000, TimeUnit.MILLISECONDS); //Released immediately if lock countdown is 0
        Assert.assertNull(transportCallRequest2, "The callback request should be null"); //The body should be null, since only transport events must be sent to this endpoint

    }

    private String queryForScheduleID() {

        String scheduleID = given().auth().oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/schedules").body().jsonPath().getList("scheduleID").get(0).toString();
        Assert.assertNotNull(scheduleID);
        return scheduleID;
    }

}
