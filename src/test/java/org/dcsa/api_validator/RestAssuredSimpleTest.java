import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

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
