package io.probedock.api.test.matchers;

import io.probedock.api.test.client.ApiTestResponse;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author Laurent Prevost <laurent.prevost@forbes-digital.com>
 */
public class ApiResponseMatcher extends BaseMatcher<ApiTestResponse> {

	private int expectedHttpStatusCode;
	private String expectedBody;
	private String actualBody;
	private boolean statusCodeMatches;
	private boolean bodyMatches;

	public ApiResponseMatcher withStatusCode(int expectedHttpStatusCode) {
		this.expectedHttpStatusCode = expectedHttpStatusCode;
		return this;
	}

	public ApiResponseMatcher withBody(String body) {
		this.expectedBody = body;
		return this;
	}

	public ApiResponseMatcher withEmptyBody() {
		this.expectedBody = "";
		return this;
	}

	public ApiResponseMatcher withOnlyRootElement() {
		this.expectedBody = "{}";
		return this;
	}

	public ApiResponseMatcher ignoreBody() {
		this.expectedBody = null;
		return this;
	}

	@Override
	public boolean matches(Object res) {
		ApiTestResponse response = (ApiTestResponse) res;

		if (response == null) {
			return false;
		}

		bodyMatches = true;
		if (expectedBody != null) {
			actualBody = response.getResponseAsString();
			bodyMatches = actualBody.equals(expectedBody);
		}

		statusCodeMatches = response.getStatus() == expectedHttpStatusCode;
		return bodyMatches && statusCodeMatches;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("response with HTTP status code " + expectedHttpStatusCode);
		if (expectedBody != null) {
			description.appendText("".equals(expectedBody) ? " and no body" : " and body " + expectedBody);
		}
	}

	@Override
	public void describeMismatch(Object item, Description description) {

		if (item == null) {
			description.appendText("response is null");
			return;
		}

		ApiTestResponse response = (ApiTestResponse) item;
		description.appendText("response doesn't match");

		if (!statusCodeMatches) {
			description.appendText(", status code is " + response.getStatus());
		}

		if (!bodyMatches) {
			description.appendText("\n          body: " + actualBody);
		}
	}
}
