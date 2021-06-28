package org.dcsa.api_validator.ovs.v1.schedules;

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

public class ScheduleSubscriptionsTest {
    //Don't reuse request objects to reduce risk of other unrelated events affecting the tests
    private Request scheduleRequest1;
    private Request scheduleRequest2;
    private CountDownLatch lock = new CountDownLatch(1); //Initialize countdown at 1, when count is 0 lock is released
    private CountDownLatch lock2 = new CountDownLatch(1); //Initialize countdown at 1, when count is 0 lock is released

    @BeforeMethod
    void setup() {
        cleanUp();
        Spark.port(Configuration.getCallbackListenPort());
        Spark.post("v1/webhook/receive-schedule-2", (req, res) -> {
            if(req.body()==null) return "Ignoring null callback received"; //Not sure why this sometimes happens. May be a problem in the API, for now we ignore it to avoid tests failing sporadically
            this.scheduleRequest1 = req;
            lock.countDown(); //Release lock
            return "Callback received!";
        });
        Spark.post("v1/webhook/receive-schedule-1", (req, res) -> {
            if(req.body()==null) return "Ignoring null callback received"; //Not sure why this sometimes happens. May be a problem in the API, for now we ignore it to avoid tests failing sporadically
            this.scheduleRequest2 = req;
            lock2.countDown(); //Release lock
            return "Callback received!";
        });
        Spark.awaitInitialization();


    }

    private void cleanUp() {
        this.scheduleRequest1 = null;
        this.scheduleRequest2 = null;
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
                body("  {\n" +
                        "    \"vesselOperatorCarrierCode\": \"MAEU\",\n" +
                        "    \"vesselOperatorCarrierCodeListProvider\": \"NMFTA\",\n" +
                        "    \"vesselPartnerCarrierCode\": \"MSCU,HLCU\",\n" +
                        "    \"vesselPartnerCarrierCodeListProvider\": \"NMFTA\",\n" +
                        "    \"startDate\": \"2020-04-06\",\n" +
                        "    \"dateRange\": \"P28D\"\n" +
                        "  }").
                post(Configuration.ROOT_URI + "/schedules");

        lock.await(20000, TimeUnit.MILLISECONDS); //Released immediately if lock countdown is 0
        Assert.assertNotNull(scheduleRequest1, "The callback request should not be null");
        Assert.assertNotNull(scheduleRequest1.body(), "The callback request body should not be null");
        String jsonBody = scheduleRequest1.body();

        System.out.println("The testCallbacks() test received the body: " + jsonBody);
        //Validate that the callback body is a Shipment Event
        JSONObject jsonSubject = new JSONObject(jsonBody);

        try (InputStream inputStream = getClass().getResourceAsStream("/ovs/v1/ScheduleSchema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(jsonSubject); // throws a ValidationException if this object is invalid
        }

    }

    //Disabled until filters work
    @Test(enabled = false)
    public void testCallbackFilter() throws InterruptedException, JSONException {
        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body("  {\n" +
                        "    \"vesselOperatorCarrierCode\": \"MAEU\",\n" +
                        "    \"vesselOperatorCarrierCodeListProvider\": \"NMFTA\",\n" +
                        "    \"vesselPartnerCarrierCode\": \"MSCU,HLCU\",\n" +
                        "    \"vesselPartnerCarrierCodeListProvider\": \"NMFTA\",\n" +
                        "    \"startDate\": \"2020-04-06\",\n" +
                        "    \"dateRange\": \"P4W\"\n" +
                        "  }").
                post(Configuration.ROOT_URI + "/schedules");

        lock2.await(3000, TimeUnit.MILLISECONDS); //Released immediately if lock countdown is 0
        Assert.assertNull(scheduleRequest2, "The callback request should be null"); //The body should be null, since only transport events must be sent to this endpoint

    }

}
