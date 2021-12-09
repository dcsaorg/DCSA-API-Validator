package org.dcsa.api_validator.bkg.v1.bookingconfirmations;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;

public class BookingTestConfiguration {

	private static final String OAS_JSON_URL =
		"https://api.swaggerhub.com/apis/dcsaorg/DCSA_BKG/1.0.0?resolved=true";

	public static final OpenApiValidationFilter BKG_OAS_VALIDATOR =
		new OpenApiValidationFilter(OpenApiInteractionValidator
			.createForSpecificationUrl(OAS_JSON_URL)
			.withLevelResolver(
				LevelResolver.create()
					.withLevel("validation.schema.additionalProperties", ValidationReport.Level.IGNORE)
					.withLevel("validation.response.header.missing", ValidationReport.Level.WARN)
					.build())
			.build());

	public static final String BKG_CONFIRMATION_SUMMARIES_PATH = "/confirmed-booking-summaries";

}
