package org.dcsa.api_validator.ovs.v1.transportcalls;

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

public class GetTransportCallsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();


    @Test
    public void testTransportCalls() {
        given().
                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-calls").
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("ovs/v1/TransportCallsSchema.json").
                        using(jsonSchemaFactory));
    }



    @Test
    public void testVesselIMONumberQueryParam() {


        List<String> vesselIMONumbers = getListOfAnyAttribute("vesselIMONumber");

        for (String vesselIMONumber : vesselIMONumbers) {
            given().
                    auth().
                    oauth2(Configuration.accessToken).
                    queryParam("vesselIMONumber", vesselIMONumber).
                    get(Configuration.ROOT_URI + "/transport-calls").
                    then().
                    body("vesselIMONumber", everyItem(equalTo(vesselIMONumber)));
        }
    }

    private List getListOfAnyAttribute(String attribute) {
        String json = given().

                auth().
                oauth2(Configuration.accessToken).
                get(Configuration.ROOT_URI + "/transport-calls").
                body().asString();

        return JsonPath.from(json).getList(attribute);
    }
}
