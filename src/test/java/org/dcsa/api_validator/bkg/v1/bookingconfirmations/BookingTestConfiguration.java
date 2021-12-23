package org.dcsa.api_validator.bkg.v1.bookingconfirmations;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;

public class BookingTestConfiguration {

	private static final String OAS_JSON_URL =
		"https://api.swaggerhub.com/apis/dcsaorg/DCSA_BKG/1.0.0?resolved=true";

  public static final OpenApiValidationFilter BKG_OAS_VALIDATOR =
      new OpenApiValidationFilter(
          OpenApiInteractionValidator.createForSpecificationUrl(OAS_JSON_URL)
              .withLevelResolver(
                  LevelResolver.create()
                      .withLevel(
                          "validation.schema.additionalProperties", ValidationReport.Level.IGNORE)
                      .build())
              .build());

	public static final String SHIPMENT_SUMMARIES_PATH = "/shipment-summaries";
	public static final String BOOKING_REQUEST_SUMMARIES_PATH = "/booking-summaries";

}
