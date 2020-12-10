package org.dcsa.api_validator.ebl.v1.shippinginstructions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.dcsa.api_validator.conf.Configuration;
import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Consumer;

import static io.restassured.RestAssured.given;

/*
 * Tests related to the GET /schedules endpoint
 */

public class PostShippingInstructionsTest {
    //JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder().setValidationConfiguration(ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV4).freeze()).freeze();

    @Test
    public void testShippingInstructions() {
        String shippingInstructionsRequestBody = JSONObjectBuilder.jsonObject()
                .kv("callbackUrl", "https://myserver.com/send/callback/here")
                .kv("carrierBookingReference", "BR1239719871")
                .kv("numberOfOriginals", 4)
                .kv("numberOfCopies", 2)
                .kv("isPartLoad", Boolean.TRUE)
                .kv("isElectronic", Boolean.TRUE)
                .array("cargoItems")
                    .object()
                        .array("cargoLineItems")
                            .object()
                                .kv("cargoLineItemID", "my-id").kv("shippingMarks", "Mark1").endObject()
                            .endArray()
                        .kv("descriptionOfGoods", "scrap metal")
                        .kv("HSCode", "HS")
                        .kv("numberOfPackages", 18)
                        .kv("weight", 13000.3)
                        .kv("volume", 12)
                        .kv("weightUnit", "KGM")
                        .kv("volumeUnit", "MTQ")
                        .kv("packageCode", "5H")
                        .kv("equipmentReference", "BMOU2149612")
                        .endObject()
                    .endArray()
                .array("shipmentEquipments")
                    .object()
                        .kv("equipmentReference", "BMOU2149612")
                        .kv("verifiedGrossMass", "My verification certification")
                        .kv("weightUnit", "KGM")
                        .kv("cargoGrossWeight", 12000)
                        .kv("cargoGrossWeightUnit", "KGM")
                        // There is no active Reefer settings for this test object
                        /*.object("activeReeferSettings")
                            .kv("temperatureMin", -18.1)
                            .kv("temperatureMax", 64.3)
                            .kv("temperatureUnit", "CEL")
                            .kv("humidityMin", 80)
                            .kv("humidityMax", 100)
                            .kv("ventilationMin", 266)
                            .kv("ventilationMax", 296)
                            .endObject()*/
                        .array("seals")
                            .object()
                                .kv("sealNumber", "Asseco's Seal")
                                .kv("sealSource", "AO123")
                                .kv("sealType", "KLP")
                                .endObject()
                            .endArray()
                        .endObject()
                    .endArray()
                .array("documentParties")
                    .object()
                            .kv("partyID", "8dd9a4c4-4039-11eb-8770-0b2b19847fab")
                            .kv("partyFunction", "COW")
                            .kv("displayedAddress", "Sølvgade 12")
                            .kv("partyContactDetails", "+45 12345678")
                            .kv("shouldBeNotified", Boolean.FALSE)
                            .endObject()
                    .endArray()
                .array("shipmentLocations")
                    .object()
                        .kv("locationType", "PDE")
                        .kv("locationID", "7f29ce3c-403d-11eb-9579-6bd2f4cf4ed6")
                        .kv("displayedName", "Asseco's PDE location")
                        .endObject()
                    .endArray()
                .array("references")
                    .object()
                        .kv("referenceType", "FF")
                        .kv("referenceValue", "My Reference")
                        .endObject()
                    .endArray()
                .buildJson();

        // System.err.println(shippingInstructionsRequestBody);

        given().
                auth().
                oauth2(Configuration.accessToken).
                contentType("application/json").
                body(shippingInstructionsRequestBody).
                post(Configuration.ROOT_URI + "/shipping-instructions").
                then().
                assertThat().
                statusCode(201);
                //body(matchesJsonSchemaInClasspath("ebl/v1/ShippingInstructionsSchema.json").using(jsonSchemaFactory));

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

    private static class JSONArrayBuilder<T extends AbstractJSONObjectBuilder<T>> {
        private List<Object> list = new ArrayList<>();
        private final AbstractJSONObjectBuilder<T> jsonObjectBuilder;
        private final String key;

        private JSONArrayBuilder(AbstractJSONObjectBuilder<T> jsonObjectBuilder, String key) {
            this.jsonObjectBuilder = jsonObjectBuilder;
            this.key = key;
        }

        private void ensureActive() {
            if (list == null) {
                throw new IllegalStateException("MapBuilder cannot be reused");
            }
        }

        public JSONArrayBuilder<T> item(Object obj) {
            ensureActive();
            this.list.add(obj);
            return this;
        }

        public InnerJSONObjectBuilder<JSONArrayBuilder<T>> object() {
            return new InnerJSONObjectBuilder<>(this, this::item);
        }

        public T endArray() {
            List<Object> m = list;
            ensureActive();
            list = null;
            jsonObjectBuilder.map.put(key, m);
            return jsonObjectBuilder.This();
        }
    }

    private static abstract class AbstractJSONObjectBuilder<T extends AbstractJSONObjectBuilder<T>> {
        protected Map<String, Object> map = new LinkedHashMap<>();

        protected void ensureActive() {
            if (map == null) {
                throw new IllegalStateException("MapBuilder cannot be reused");
            }
        }

        T This() {
            @SuppressWarnings("unchecked")
            T t = (T) this;
            return t;
        }

        public T kv(String k, Object v) {
            ensureActive();
            if (map.containsKey(k)) {
                throw new IllegalArgumentException("The key " + k + " was already inserted");
            }
            map.put(k, v);
            return This();
        }

        public InnerJSONObjectBuilder<AbstractJSONObjectBuilder<T>> object(String key) {
            ensureActive();
            return new InnerJSONObjectBuilder<>(this, res -> map.put(key, res));
        }

        public JSONArrayBuilder<T> array(String key) {
            return new JSONArrayBuilder<>(this, key);
        }

    }

    private static class InnerJSONObjectBuilder<R> extends AbstractJSONObjectBuilder<InnerJSONObjectBuilder<R>> {

        private final Consumer<Map<String, Object>> consumer;
        private final R ret;

        private InnerJSONObjectBuilder(R ret, Consumer<Map<String, Object>> consumer) {
            this.consumer = consumer;
            this.ret = ret;
        }

        public R endObject() {
            Map<String, Object> m = map;
            ensureActive();
            map = null;
            consumer.accept(m);
            return ret;
        }
    }

    private static class JSONObjectBuilder extends AbstractJSONObjectBuilder<JSONObjectBuilder> {

        private static JSONObjectBuilder jsonObject() {
            return new JSONObjectBuilder();
        }

        public Map<String, Object> buildObject() {
            Map<String, Object> m = map;
            ensureActive();
            map = null;
            return m;
        }

        public String buildJson() {
            try {
                return new JsonMapper().writer().writeValueAsString(buildObject());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
