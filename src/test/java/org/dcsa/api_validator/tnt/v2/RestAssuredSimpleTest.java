package org.dcsa.api_validator.tnt.v2;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/*
 * A simple test illustrating the use of REST Assured
 */

public class RestAssuredSimpleTest {


    @Test
    public void simple_get_test() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").then().assertThat().body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void connectionSucceded() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/events").then().assertThat().statusCode(200);
    }
}
