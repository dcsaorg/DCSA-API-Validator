import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.testng.annotations.Test;

/*
 * A simple test illustrating the use of REST Assured
 */

public class RestAssuredSimpleTest {

    final static String ROOT_URI = "http://localhost:9090";

    @Test
    public void simple_get_test() {
        get(ROOT_URI + "/events").then().assertThat().body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void connectionSucceded() {
        get(ROOT_URI + "/events").then().assertThat().statusCode(200);
    }
}
