package io.probedock.api.test.matchers;

import java.util.Collection;
import java.util.Iterator;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * This helper class offers additional, custom Hamcrest matchers for our test
 * assertions.
 *
 * @author Laurent Prevost <laurent.prevost@forbes-digital.com>
 */
public class Matchers {

	public static ApiResponseMatcher isApiResponse(int expectedHttpStatusCode) {
		return new ApiResponseMatcher().withStatusCode(expectedHttpStatusCode);
	}

	public static ApiResponseMatcher isEmptyResponse(int expectedHttpStatusCode) {
		return new ApiResponseMatcher().withStatusCode(expectedHttpStatusCode).withEmptyBody();
	}

	public static ApiResponseMatcher isRootElementResponse(int expectedHttpStatusCode) {
		return new ApiResponseMatcher().withStatusCode(expectedHttpStatusCode).withOnlyRootElement();
	}

	public static ApiErrorResponseMatcher isApiErrorResponse(int expectedHttpStatusCode) {
		return ApiErrorResponseMatcher.isApiErrorResponse(expectedHttpStatusCode);
	}

	public static interface CollectionComparator<T> {

		boolean compare(T expObj, T actObj);

		String describeProblem(T expObj, T actObj);
	}

	/**
	 * Builds a matcher to compare all elements of a list.
	 *
	 * @param <T> generic type of collection elements
	 * @param expectedList expected values, actual values will be provided
	 * dynamically upon usage of this matcher
	 * @param comparator simple comparator for individual items in the list
	 * @return a corresponding Hamcrest matcher
	 */
	public static <T> Matcher<Collection<T>> collectionEquals(final Collection<T> expectedList, final CollectionComparator<? super T> comparator) {
		return new BaseMatcher<Collection<T>>() {
			private String error;

			@Override
			@SuppressWarnings("unchecked")
			public boolean matches(Object item) {
				Collection<T> actualList = (Collection<T>) item;
				Iterator<T> expIterator = expectedList.iterator(), actIterator = actualList.iterator();
				int index = 0;
				while (expIterator.hasNext() && actIterator.hasNext()) {
					T expObj = expIterator.next(), actObj = actIterator.next();
					if (!comparator.compare(expObj, actObj)) {
						error = comparator.describeProblem(expObj, actObj) + " at index " + index;
						return false;
					}
					index++;
				}
				if (expIterator.hasNext() || actIterator.hasNext()) {
					error = "size does not match at index " + index;
					return false;
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				if (error != null) {
					description.appendText(error);
				}
			}
		};
	}

	/**
	 * Matcher for an empty JSON object (or array) (literally {} or []).
	 *
	 * @return a matcher to be used in an assertThat statement
	 */
	public static Matcher<JSONObject> isEmptyJsonObject() {
		return new BaseMatcher<JSONObject>() {
			@Override
			public boolean matches(Object item) {
				if (item instanceof JSONObject) {
					return ((JSONObject) item).isEmpty();
				} else if(item instanceof JSONArray) {
					return ((JSONArray) item).isEmpty();
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("empty JSON object");
			}
		};
	}
}
