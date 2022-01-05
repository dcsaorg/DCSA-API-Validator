package org.dcsa.api_validator.bkg.v1;

import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.hamcrest.Matcher;

import static com.github.fge.jsonschema.SchemaVersion.DRAFTV3;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class BookingTestConfiguration {

  public static final String SHIPMENT_SUMMARIES_PATH = "/shipment-summaries";
  public static final String BOOKING_SUMMARIES_PATH = "/booking-summaries";
  public static final String BOOKING_CARRIERBOOKINGREQUESTREFERENCE_PATH = "/bookings/{carrierBookingRequestReference}";

  public static final Matcher<?> JSON_SCHEMA_VALIDATOR =
      matchesJsonSchemaInClasspath("bkg/v1/schemas/all.json")
          .using(
              JsonSchemaFactory.newBuilder()
                  .setValidationConfiguration(
                      ValidationConfiguration.newBuilder().setDefaultVersion(DRAFTV3).freeze())
                  .freeze());
}
