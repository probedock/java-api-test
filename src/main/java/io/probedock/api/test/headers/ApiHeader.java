package io.probedock.api.test.headers;

import io.probedock.api.test.client.ApiTestRequest;

/**
 * HTTP request header with meta information to indicate whether it concerns only the next request
 * or should be kept.
 *
 * @see ApiHeadersManager
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ApiHeader {

	/**
	 * Header name.
	 */
	private String name;
	/**
	 * Header value.
	 */
	private String value;

	/**
	 * Constructs a new header.
	 *
	 * @param name header name
	 * @param value header value
	 * @throws IllegalArgumentException if the name is null
	 */
	public ApiHeader(String name, String value) {
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null");
		}

		this.name = name;
		this.value = value;
	}

	/**
	 * Returns the name of the header.
	 *
	 * @return the header name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the header.
	 *
	 * @return the header value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the value of the header. The API request is provided for headers whose value may
	 * depend on request information.
	 *
	 * @param request the API request
	 * @return the header value
	 */
	public String computeValue(ApiTestRequest request) {
		return value != null ? value.toString() : null;
	}
}