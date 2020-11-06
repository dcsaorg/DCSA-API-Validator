package org.dcsa.api_validator.ebl.v1.shippinginstructions;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV4;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/*
 * Tests related to the GET /schedules endpoint
 */

public class PostShippingInstructionsTest {

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();

    @Test
    public void testShippingInstructions() {
//        given().
//                auth().
//                oauth2(Configuration.accessToken).
//                contentType("application/json").
//                body("  {\n" +
//                        "  \"callbackUrl\": \"https://myserver.com/send/callback/here\",\n" +
//                        "  \"carrierBookingReference\": \"ABC709951\",\n" +
//                        "  \"transportDocumentTypeCode\": \"BOL\",\n" +
//                        "  \"transportReference\": \"MISSING\",\n" +
//                        "  \"carrierVoyageNumber\": \"MISSING\",\n" +
//                        "  \"descriptionOfGoods\": \"MISSING\",\n" +
//                        "  \"parties\": [\n" +
//                        "    {\n" +
//                        "      \"partyType\": \"ISSUER\",\n" +
//                        "      \"partyName\": \"MISSING\",\n" +
//                        "      \"email\": \"MISSING\",\n" +
//                        "      \"phoneNumber\": \"MISSING\",\n" +
//                        "      \"fax\": \"MISSING\",\n" +
//                        "      \"taxReference\": \"MISSING\"\n" +
//                        "    }\n" +
//                        "  ],\n" +
//                        "  \"locations\": [\n" +
//                        "    {\n" +
//                        "      \"locationID\": \"MISSING\",\n" +
//                        "      \"locationTypeCode\": \"PRE\"\n" +
//                        "    }\n" +
//                        "  ],\n" +
//                        "  \"serviceType\": \"CY\",\n" +
//                        "  \"shipmentTerm\": \"BB\",\n" +
//                        "  \"weight\": \"MISSING\",\n" +
//                        "  \"volume\": \"MISSING\",\n" +
//                        "  \"weightUnit\": \"MISSING\",\n" +
//                        "  \"volumeUnit\": \"MISSING\",\n" +
//                        "  \"freightPayableAt\": \"MISSING\",\n" +
//                        "  \"dateOfIssue\": \"MISSING\",\n" +
//                        "  \"equipmentReference\": \"APZU4812090\",\n" +
//                        "  \"ISOEquipmentCode\": \"MISSING\",\n" +
//                        "  \"paymentTerm\": \"string\",\n" +
//                        "  \"shipmentEquipmentQuantity\": \"MISSING\",\n" +
//                        "  \"documentReferenceNumber\": \"MISSING\",\n" +
//                        "  \"numberOfOriginals\": \"MISSING\",\n" +
//                        "  \"sealNumber\": \"MISSING\",\n" +
//                        "  \"taxReference\": \"MISSING\",\n" +
//                        "  \"sealSource\": \"MISSING\",\n" +
//                        "  \"tareWeight\": \"MISSING\",\n" +
//                        "  \"clauseContent\": \"MISSING\",\n" +
//                        "  \"carrierCode\": \"MISSING\",\n" +
//                        "  \"verifiedGrossMass\": \"MISSING\",\n" +
//                        "  \"declaredValue\": 0,\n" +
//                        "  \"declaredValueCurrencyCode\": \"MISSING\",\n" +
//                        "  \"shippingMarks\": \"MISSING\",\n" +
//                        "  \"exportReferenceNumber\": \"MISSING\",\n" +
//                        "  \"currencyCode\": \"MISSING\",\n" +
//                        "  \"currencyAmount\": \"MISSING\",\n" +
//                        "  \"SVCContract\": \"MISSING\"\n" +
//                        "}").
//                post(Configuration.ROOT_URI + "/shipping-instructions").
//                then().
//                assertThat().
//                body(matchesJsonSchemaInClasspath("ebl/v1/ShippingInstructionsSchema.json").
//                        using(jsonSchemaFactory));
    }


//    //Finds all startDates from schedules, and then uses them each of them as a query parameter, and verifies the response
//    @Test
//    public void testStartDateQueryParam() {
//        String json = given().
//
//                auth().
//                oauth2(Configuration.accessToken).
//                get(Configuration.ROOT_URI + "/schedules").
//                body().asString();
//
//        List<String> startDates = JsonPath.from(json).getList("startDate");
//        for (String startDate : startDates) {
//            given().
//                    auth().
//                    oauth2(Configuration.accessToken).
//                    queryParam("startDate", startDate).
//                    get(Configuration.ROOT_URI + "/schedules").
//                    then().
//                    body("startDate", everyItem(equalTo(startDate)));
//        }
//    }
//
//    //Finds all dateRanges from schedules, and then uses them each of them as a query parameter, and verifies the response
//    @Test
//    public void testDateRangeQueryParam() {
//        String json = given().
//
//                auth().
//                oauth2(Configuration.accessToken).
//                get(Configuration.ROOT_URI + "/schedules").
//                body().asString();
//
//        List<String> dateRanges = JsonPath.from(json).getList("dateRange");
//        for (String dateRange : dateRanges) {
//            given().
//                    auth().
//                    oauth2(Configuration.accessToken).
//                    queryParam("dateRange", dateRange).
//                    get(Configuration.ROOT_URI + "/schedules").
//                    then().
//                    body("dateRange", everyItem(equalTo(dateRange)));
//        }
//    }


}
