package io.probedock.api.test.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.utils.URIBuilder;

/**
 * Utility to construct URIs with a builder pattern. Ensures that the final URI will not contain
 * duplicate slash (<tt>/</tt>) characters.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiUriBuilder {

	/**
	 * Base URI.
	 */
	private String baseUri;
	/**
	 * Query parameters.
	 */
	private Map<String, List<Object>> queryParams;
	/**
	 * Path elements.
	 */
	private List<String> pathElements;

	/**
	 * Constructs an URI builder starting from the specified base URI.
	 *
	 * @param baseUri the base URI
	 */
	public ApiUriBuilder(String baseUri) {

		// strip trailing slash
		this.baseUri = baseUri.replaceFirst("\\/$", "");

		this.queryParams = new HashMap<>();
		this.pathElements = new ArrayList<>();
	}

	/**
	 * Appends path elements to the URI.
	 *
	 * @param elements the path elements to add
	 * @return this builder
	 */
	public ApiUriBuilder path(String... elements) {

		for (String element : elements) {
			// strip leading and trailing slash
			pathElements.add(element.replaceFirst("^\\/", "").replaceFirst("\\/$", ""));
		}

		return this;
	}

	/**
	 * Adds a query parameter to the URI.
	 *
	 * @param name parameter name
	 * @param value parameter value
	 * @return this builder
	 */
	public ApiUriBuilder queryParam(String name, Object value) {
		getQueryParamList(name).add(value);
		return this;
	}

	/**
	 * Add a query parameter with multiple values to the URI. For three values, the query string
	 * will contain the parameter three times, once for each value.
	 *
	 * @param name parameter name
	 * @param values parameter values
	 * @return this builder
	 */
	public ApiUriBuilder queryParam(String name, Object... values) {

		for (Object value : values) {
			queryParam(name, value);
		}

		return this;
	}

	/**
	 * Builds and returns the URI.
	 *
	 * @return an URI
	 * @throws ApiTestException if the base URI, path elements or query parameters are invalid
	 */
	public URI build() {
		try {
			return buildUri();
		} catch (URISyntaxException use) {
			throw new ApiTestException("URI entry point, path elements or query parameters are invalid", use);
		}
	}

	/**
	 * Returns the list of values for a query param.
	 *
	 * @param name the name of the query param
	 * @return a list of values
	 */
	private List<Object> getQueryParamList(final String name) {

		// create the list if necessary
		if (!queryParams.containsKey(name)) {
			queryParams.put(name, new ArrayList<>());
		}

		return queryParams.get(name);
	}

	private URI buildUri() throws URISyntaxException {

		// build the URI from the base URI and path elements
		final StringBuilder builder = new StringBuilder(baseUri);
		for (String element : pathElements) {
			builder.append("/").append(element);
		}

		// build the actual URI object
		final URIBuilder uriBuilder = new URIBuilder(builder.toString());

		// add all query parameters
		for (Map.Entry<String, List<Object>> queryParam : queryParams.entrySet()) {
			for (Object value : queryParam.getValue()) {
				uriBuilder.addParameter(queryParam.getKey(), value.toString());
			}
		}

		return uriBuilder.build();
	}
}
