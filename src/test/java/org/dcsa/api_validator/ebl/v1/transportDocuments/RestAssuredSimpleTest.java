package org.dcsa.api_validator.ebl.v1.transportDocuments;

import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/*
 * A simple test illustrating the use of REST Assured
 */

public class RestAssuredSimpleTest {


    @Test
    public void simple_get_transport_documents_test() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-documents").then().assertThat().body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void connection_transport_documents_Succeeded() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-documents").then().assertThat().statusCode(200);
    }
}
