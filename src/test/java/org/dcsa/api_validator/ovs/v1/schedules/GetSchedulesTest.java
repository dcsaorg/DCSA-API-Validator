package org.dcsa.api_validator.ovs.v1.schedules;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.path.json.JsonPath;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

/*
 * Tests related to the GET /schedules endpoint
 */

public class GetSchedulesTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();


    @Test
    public void testSchedules() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/schedules").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("ovs/v1/SchedulesSchema.json").
                        using(jsonSchemaFactory));
    }


    //Finds all startDates from schedules, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testStartDateQueryParam() {
        String json = given().

                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/schedules").
                body().asString();

        List<String> startDates = JsonPath.from(json).getList("startDate");
        for (String startDate : startDates) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("startDate", startDate).
                    get(Configuration.ROOT_URI + "/schedules").
                    then().
                    body("startDate", everyItem(equalTo(startDate)));
        }
    }   //Finds all dateRanges from schedules, and then uses them each of them as a query parameter, and verifies the response
    @Test
    public void testDateRangeQueryParam() {
        String json = given().

                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/schedules").
                body().asString();

        List<String> dateRanges = JsonPath.from(json).getList("dateRange");
        for (String dateRange : dateRanges) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("dateRange", dateRange).
                    get(Configuration.ROOT_URI + "/schedules").
                    then().
                    body("dateRange", everyItem(equalTo(dateRange)));
        }
    }


}
