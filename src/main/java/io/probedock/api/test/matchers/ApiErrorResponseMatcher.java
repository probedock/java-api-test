package io.probedock.api.test.matchers;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import io.probedock.api.test.client.ApiTestResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Ensures that a JSON API error response has the expected HTTP status code and errors.
 *
 * @author Laurent Prevost <laurent.prevost@forbes-digital.com>
 */
public class ApiErrorResponseMatcher extends BaseMatcher<ApiTestResponse> {

	//<editor-fold defaultstate="collapsed" desc="Static Imports">
	public static ApiErrorResponseMatcher isApiErrorResponse(int expectedHttpStatusCode) {
		return new ApiErrorResponseMatcher(expectedHttpStatusCode);
	}
	//</editor-fold>
	private Integer expectedHttpStatusCode;
	private List<ErrorExpectation> expectedErrors;
	private boolean isNonNullResponse;
	private Integer actualHttpStatusCode;
	private boolean httpStatusCodeMatches;
	private String contentType;
	private boolean isJsonResponse;
	private boolean hasErrorsProperty;
	private List<Object> invalidErrors;
	private List<Error> actualErrors;
	private List<ErrorExpectation> unmetErrorExpectations;
	private String requestUri;

	public ApiErrorResponseMatcher(int expectedHttpStatusCode) {
		expectedErrors = new ArrayList<>();
		this.expectedHttpStatusCode = expectedHttpStatusCode;
	}

	public ApiErrorResponseMatcher withError(int code) {
		return withError(code, null, null, (String) null);
	}

	public ApiErrorResponseMatcher withError(int code, String locationType, String location) {
		return withError(code, locationType, location, (String) null);
	}

	public ApiErrorResponseMatcher withError(int code, Pattern messagePattern) {
		return withError(code, null, null, messagePattern);
	}

	public ApiErrorResponseMatcher withError(int code, String locationType, String location, String message) {
		expectedErrors.add(new ErrorExpectation(code, locationType, location, message));
		return this;
	}

	public ApiErrorResponseMatcher withError(int code, String locationType, String location, Pattern messagePattern) {
		expectedErrors.add(new ErrorExpectation(code, locationType, location, messagePattern));
		return this;
	}

	@Override
	public boolean matches(Object item) {

		// reset state
		isNonNullResponse = true;
		actualHttpStatusCode = null;
		httpStatusCodeMatches = true;
		contentType = null;
		isJsonResponse = true;
		hasErrorsProperty = true;
		invalidErrors = new ArrayList<>();
		actualErrors = new ArrayList<>();
		unmetErrorExpectations = new ArrayList<>();

		final ApiTestResponse response = (ApiTestResponse) item;

		// ensure response is not null
		if (response == null) {
			isNonNullResponse = false;
			return false;
		}

		// get uri
		if (response.getRequestUri() != null) {
			requestUri = response.getRequestUri().toString();
		}
		else {
			requestUri = "<request URI not available>";
		}

		// ensure the HTTP status code is the correct one (if set)
		actualHttpStatusCode = response.getStatus();
		httpStatusCodeMatches = expectedHttpStatusCode == null || expectedHttpStatusCode.equals(actualHttpStatusCode);

		// ensure content type is JSON
		contentType = response.getHeaderString("Content-Type");
		if (contentType == null || !contentType.matches("^application\\/json")) {
			isJsonResponse = false;
			return false;
		}

		// ensure response has "errors" property
		JSONArray errors;
		try {
			errors = JsonPath.read(response.getResponseAsString(), "$.errors");
		} catch (InvalidPathException | ClassCastException e) {
			hasErrorsProperty = false;
			return false;
		}

		// parse actual errors
		parseErrors(errors);

		// ensure all expected errors are there
		for (final ErrorExpectation expectedError : expectedErrors) {

			boolean found = false;
			for (final Error actualError : actualErrors) {
				if (expectedError.matches(actualError)) {
					actualErrors.remove(actualError);
					found = true;
					break;
				}
			}

			if (!found) {
				unmetErrorExpectations.add(expectedError);
			}
		}

		return httpStatusCodeMatches && hasErrorsProperty && invalidErrors.isEmpty()
				&& unmetErrorExpectations.isEmpty() && actualErrors.isEmpty();
	}

	@Override
	public void describeTo(Description description) {

		description.appendText("JSON API error response");

		if (expectedHttpStatusCode != null) {
			description.appendText(" with HTTP status code " + expectedHttpStatusCode);
		}

		if (!expectedErrors.isEmpty()) {
			description.appendValueList(" with " + expectedErrors.size() + " errors: ", ", ", "", expectedErrors);
		}
	}

	@Override
	public void describeMismatch(Object item, Description description) {

		if (!isNonNullResponse) {
			description.appendText("response is null");
			return;
		}

		description
			.appendText("response for URI ")
			.appendText(requestUri)
			.appendText(" doesn't match");
		
		if (!httpStatusCodeMatches) {
			description.appendText(", has HTTP status code " + actualHttpStatusCode);
		}
		
		if (!isJsonResponse) {
			description.appendText(", is not JSON (content type is " + contentType + ")");
		}

		if (!hasErrorsProperty) {
			description.appendText(", has no \"errors\" array property");
		}

		if (!invalidErrors.isEmpty()) {
			description.appendValueList(", has " + invalidErrors.size() + " invalid errors (", ", ", ")", invalidErrors);
		}

		if (!unmetErrorExpectations.isEmpty()) {
			description.appendValueList(", is missing " + unmetErrorExpectations.size() + " expected errors (", ", ", ")", unmetErrorExpectations);
		}

		if (!actualErrors.isEmpty()) {
			description.appendValueList(", has " + actualErrors.size() + " additional unexpected errors (", ", ", ")", actualErrors);
		}

		final ApiTestResponse response = (ApiTestResponse) item;
		description.appendText("\n          body: ").appendText(response.getResponseAsString());
	}

	private void parseErrors(JSONArray errors) {

		for (Object object : errors) {

			if (!(object instanceof JSONObject)) {
				invalidErrors.add(object);
				continue;
			}

			final JSONObject error = (JSONObject) object;
			if (!(error.get("code") instanceof Integer)) {
				invalidErrors.add(object);
				continue;
			} else if (error.containsKey("locationType") && !(error.get("locationType") instanceof String)) {
				invalidErrors.add(object);
				continue;
			} else if (error.containsKey("location") && !(error.get("location") instanceof String)) {
				invalidErrors.add(object);
				continue;
			} else if (!(error.get("message") instanceof String)) {
				invalidErrors.add(object);
				continue;
			}

			actualErrors.add(new Error((Integer) error.get("code"), (String) error.get("locationType"), (String) error.get("location"), (String) error.get("message")));
		}
	}

	private static class Error {

		private int code;
		private String locationType;
		private String location;
		private String message;

		public Error(int code, String locationType, String location, String message) {
			this.code = code;
			this.locationType = locationType;
			this.location = location;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public String getLocationType() {
			return locationType;
		}

		public String getLocation() {
			return location;
		}

		public String getMessage() {
			return message;
		}

		@Override
		public String toString() {

			final StringBuilder builder = new StringBuilder();
			builder.append("code=").append(code);

			if (locationType != null) {
				builder.append(", locationType=").append(locationType);
			}

			if (location != null) {
				builder.append(", location=").append(location);
			}

			builder.append(", message=").append(message);

			return builder.toString();
		}
	}

	private static class ErrorExpectation {

		private int code;
		private String locationType;
		private String location;
		private String message;
		private Pattern messagePattern;

		public ErrorExpectation(int code, String locationType, String location, String message) {
			this.code = code;
			this.locationType = locationType;
			this.location = location;
			this.message = message;
		}

		public ErrorExpectation(int code, String locationType, String location, Pattern messagePattern) {
			this.code = code;
			this.locationType = locationType;
			this.location = location;
			this.messagePattern = messagePattern;
		}

		@Override
		public String toString() {

			final StringBuilder builder = new StringBuilder();
			builder.append("code=").append(code);

			if (locationType != null) {
				builder.append(", locationType=").append(locationType);
			} else {
				builder.append(", no locationType");
			}

			if (location != null) {
				builder.append(", location=").append(location);
			} else {
				builder.append(", no location");
			}

			if (message != null) {
				builder.append(", message=").append(message);
			} else if (messagePattern != null) {
				builder.append(", message~=").append(messagePattern.toString());
			} else {
				builder.append(", non-blank message");
			}

			return builder.toString();
		}

		public boolean matches(Error error) {

			if (code != error.getCode()) {
				return false;
			} 
			
			if (locationType != null ? !locationType.equals(error.getLocationType()) : error.getLocationType() != null) {
				return false;
			}

			if (location != null ? !location.equals(error.getLocation()) : error.getLocation() != null) {
				return false;
			}
			
			if (message != null) {
				return message.equals(error.getMessage());
			} else if (messagePattern != null) {
				return error.getMessage() != null && messagePattern.matcher(error.getMessage()).matches();
			} else {
				return error.getMessage() != null && !error.getMessage().isEmpty();
			}
		}
	}
}
