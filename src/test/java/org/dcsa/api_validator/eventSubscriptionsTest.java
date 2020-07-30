package org.dcsa.api_validator;

import org.dcsa.api_validator.conf.Configuration;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.testng.annotations.Test;
import spark.Request;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class eventSubscriptionsTest {
    private static final CountDownLatch lock = new CountDownLatch(1); //Initialize countdown at 1, when count is 0 lock is released
private static Request req;

    @BeforeAll
    public static void initServer(){
        Spark.port(4567);
        Spark.post("/webhook/receive",(req,res)->{
            eventSubscriptionsTest.req =req;
            lock.countDown(); //Release lock
            return "Callback received!";
        });
        Spark.awaitInitialization();

    }
    @Test
    public void testCallbacks() throws InterruptedException, IOException,  JSONException {

        initServer();
        given().
                contentType("application/json").
                body("{\n" +
                        "            \"eventDateTime\": \"2020-07-14T22:00:00.000+00:00\"," +
                        "            \"eventClassifierCode\": \"PLN\",\n" +
                        "            \"eventType\": \"SHIPMENT\"," +
                        "            \"eventTypeCode\": \"DEPA\"," +
                        "            \"shipmentInformationTypeCode\": \"Asger text\"" +
                        "        }").
                post(Configuration.ROOT_URI + "/events");
        LocalDateTime then = LocalDateTime.now();

       lock.await(20000, TimeUnit.MILLISECONDS); //Released immediately if lock countdown is 0
        Assert.assertNotNull(req.body());

        //Validate that the callback body is a Shipment Event
        JSONObject jsonSubject = new JSONObject(req.body());

        try (InputStream inputStream = getClass().getResourceAsStream("/ShipmentEventSchema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(jsonSubject); // throws a ValidationException if this object is invalid
        }


    }

}
