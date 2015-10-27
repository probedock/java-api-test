package io.probedock.api.test.matchers;

import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Matcher for Json error responses coming from our API.
 * Should be used only when expecting multiple erorrs.
 *
 * @author Laurent Prevost <laurent.prevost@forbes-digital.com>
 */
public class JsonErrorMatcher extends BaseMatcher<String>{

	private List<Entry<String, Integer>> expectedErrors;
	// Needed because we consume errors in the other List while processing
	private List<Entry<String, Integer>> expectedErrorsLog;

	private boolean sizeMatches;
	private boolean errorMatches = true;
	private boolean errorMessageMatches = true;
	private int errorsReceived = 0;
	private String missingErrorMessageLocation;
	private int missingErrorMessageCode;

	/**
	 * Adds the list of errors to verify to the matcher
	 *
	 * @param expectedErrors A list of errors in the form Location : Code
	 * @return The matcher for convenience
	 */
	public JsonErrorMatcher withExpectedErrors(List<Entry<String, Integer>> expectedErrors) {
		this.expectedErrors = new ArrayList<>(expectedErrors);
		this.expectedErrorsLog = new ArrayList<>(expectedErrors);
		return this;
	}

	@Override
	public boolean matches(Object item) {
		String response = (String) item;
		if (response != null) {
			JSONArray errors = JsonPath.read(response, "$.errors");
			if (errors != null) {
				// Check the size of the error list received
				if (expectedErrors != null) {
					sizeMatches = errors.size() == expectedErrors.size();
					errorsReceived = errors.size();
				}
				if (expectedErrors != null) {
					Iterator<Object> error = errors.iterator();
					boolean found;
					// Check every error until found in the expected errors list
					while (error.hasNext()) {
						found = false;
						JSONObject jsonError = (JSONObject) error.next();
						Iterator<Entry<String, Integer>> expectedError = expectedErrors.iterator();
						// Check code and location as well and verify a message is provided
						while(expectedError.hasNext() && !found) {
							Entry<String, Integer> e = expectedError.next();
							// If found and correct, remove from the expected errors list
							if (e.getKey().equals(jsonError.get("location")) && e.getValue().equals(jsonError.get("code"))){
								if (jsonError.get("message") == null) {
									missingErrorMessageLocation = (String) jsonError.get("location");
									missingErrorMessageCode = (int) jsonError.get("code");
									errorMessageMatches = false;
									return false;
								}
								found = true;
								expectedError.remove();
							}
						}
						// If error was not found in the ecpected errors, fail
						if (!found) {
							errorMatches = false;
							return false;
						}
					}
					// If the expected error is not empty, it means an error expected was not present
					if (!expectedErrors.isEmpty()) {
						errorMatches = false;
						return false;
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void describeMismatch(Object item, Description description) {
		String response = (String) item;
		if (response != null && !response.isEmpty()) {
			if (!sizeMatches) {
				description.appendText("The size of the errors received doesn't match. Expected: " + expectedErrorsLog.size() + " Received: " + errorsReceived + "\n");
			}
			if (!errorMessageMatches) {
				description.appendText("Error with location: " + missingErrorMessageLocation + " and code: " + missingErrorMessageCode + "doesn't have an error message.\n");
			}
			if (!errorMatches) {
				description.appendText("At least one error that was expected was not found in the response: \n");
				description.appendText("Location: " + expectedErrorsLog.get(0).getKey() + " Code: " + expectedErrorsLog.get(0).getValue() + "\n");
			}
		}
		else {
			description.appendText("The provided response was null or empty.");
		}
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("Number of errors expected: " + expectedErrorsLog.size() + "\n");
		Iterator<Entry<String, Integer>> i = expectedErrorsLog.iterator();
		while (i.hasNext()) {
			Entry<String, Integer> e = i.next();
			description.appendText("Location: " + e.getKey() + " Code: " + e.getValue() + ",\n");
		}
	}

}
