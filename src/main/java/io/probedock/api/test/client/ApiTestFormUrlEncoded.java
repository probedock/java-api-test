package io.probedock.api.test.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Container of name/value pairs to create a form URL-encoded HTTP request body.
 *
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiTestFormUrlEncoded {

	/**
	 * List of Apache name/value pair objects.
	 */
	private List<NameValuePair> pairs;

	/**
	 * Constructs an empty container.
	 */
	public ApiTestFormUrlEncoded() {
		pairs = new ArrayList<>();
	}

	/**
	 * Constructs a copy of another container.
	 *
	 * @param source the container whose name/value pairs to copy
	 */
	public ApiTestFormUrlEncoded(ApiTestFormUrlEncoded source) {
		pairs = new ArrayList<>(source.pairs);
	}

	/**
	 * Appends a name/value pair to this container. Previously added name/value pairs with the same
	 * name are kept.
	 *
	 * @param name the name
	 * @param value the value
	 * @return this container
	 */
	public ApiTestFormUrlEncoded add(String name, String value) {
		pairs.add(new BasicNameValuePair(name, value));
		return this;
	}

	/**
	 * Appends a name/value pair to this container. This will remove previously added name/value
	 * pairs with the same name.
	 *
	 * @param name the name
	 * @param value the value
	 * @return this container
	 */
	public ApiTestFormUrlEncoded set(String name, String value) {
		return remove(name).add(name, value);
	}

	/**
	 * Removes all name/value pairs with the specified name from this container.
	 *
	 * @param name the name
	 * @return this container
	 */
	public ApiTestFormUrlEncoded remove(String name) {

		final Iterator<NameValuePair> iter = pairs.iterator();
		while (iter.hasNext()) {
			if (name.equals(iter.next().getName())) {
				iter.remove();
			}
		}

		return this;
	}

	/**
	 * Returns the internal name/value pairs for use with {@link ApiTestRequestBody}.
	 *
	 * @return a list of Apache name/value pair objects
	 */
	protected List<NameValuePair> getPairs() {
		return pairs;
	}
}
