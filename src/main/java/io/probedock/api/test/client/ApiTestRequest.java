package io.probedock.api.test.client;

import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * HTTP request wrapper.
 *
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiTestRequest {

	//<editor-fold defaultstate="collapsed" desc="HTTP Methods Constants">
	/**
	 * The HTTP GET method.
	 */
	public static final String GET = HttpGet.METHOD_NAME;
	/**
	 * The HTTP HEAD method.
	 */
	public static final String HEAD = HttpHead.METHOD_NAME;
	/**
	 * The HTTP POST method.
	 */
	public static final String POST = HttpPost.METHOD_NAME;
	/**
	 * The HTTP PUT method.
	 */
	public static final String PUT = HttpPut.METHOD_NAME;
	/**
	 * The HTTP PATCH method.
	 */
	public static final String PATCH = HttpPatch.METHOD_NAME;
	/**
	 * The HTTP DELETE method.
	 */
	public static final String DELETE = HttpDelete.METHOD_NAME;
	//</editor-fold>
	/**
	 * The internal Apache HTTP request.
	 */
	private HttpUriRequest request;

	/**
	 * Constructs a new request without a body.
	 *
	 * @param method the HTTP method (see class constants for supported methods)
	 * @param uriBuilder an URI builder
	 * @throws ApiTestException if the HTTP method is not supported
	 */
	public ApiTestRequest(String method, ApiUriBuilder uriBuilder) {
		this(method, uriBuilder, null);
	}

	/**
	 * Constructs a new request with an optional body.
	 *
	 * @param method the HTTP method (see class constants for supported methods)
	 * @param uriBuilder an URI builder
	 * @param body an optional request body
	 * @throws ApiTestException if the HTTP method is not supported or if a body is specified but
	 * not supported for the given HTTP method
	 */
	public ApiTestRequest(String method, ApiUriBuilder uriBuilder, ApiTestRequestBody body) {

		// build the internal HTTP request object
		request = buildRequestObject(method, uriBuilder.build());

		// set the request body if present
		if (body != null) {
			setBody(body.toEntity());
		}
	}

	/**
	 * Returns the URI of this request.
	 *
	 * @return an URI
	 */
	public URI getUri() {
		return request.getURI();
	}

	/**
	 * Returns the HTTP method of this request.
	 *
	 * @return the HTTP method string
	 */
	public String getMethod() {
		return request.getMethod();
	}

	/**
	 * Adds a request header. The header will be appended to the end of the list.
	 *
	 * @param name header name
	 * @param value header value
	 * @return this request
	 * @throws NullPointerException if the value is null
	 */
	public ApiTestRequest addHeader(String name, Object value) {
		request.addHeader(name, value.toString());
		return this;
	}

	/**
	 * Sets a request header. Previously added headers with the same name are overwritten.
	 *
	 * @param name header name
	 * @param value header value
	 * @return this request
	 * @throws NullPointerException if the value is null
	 */
	public ApiTestRequest setHeader(String name, Object value) {
		request.removeHeaders(name);
		request.setHeader(name, value.toString());
		return this;
	}

	/**
	 * Removes all request headers with the specified name.
	 *
	 * @param name header name
	 * @return this request
	 */
	public ApiTestRequest removeHeader(String name) {
		request.removeHeaders(name);
		return this;
	}

	@Override
	public String toString() {

		// add method and URI
		final StringBuilder builder = new StringBuilder(request.getMethod() + " " + request.getURI().toString());

		// add all header names and values
		final Header[] headers = request.getAllHeaders();
		if (headers.length >= 1) {
			builder.append(", headers:");
			for (Header header : headers) {
				builder.append(" ");
				builder.append(header.getName()).append("=\"").append(header.getValue()).append("\"");
			}
		}

		return builder.toString();
	}

	/**
	 * Returns the internal request object.
	 *
	 * @return an Apache HTTP request
	 */
	protected HttpUriRequest getRequestObject() {
		return request;
	}

	/**
	 * Sets the request body. Also ensures that this request supports a body (POST, PUT or PATCH).
	 *
	 * @param entity the request body
	 * @throws ApiTestException if a body is not supported for the HTTP method of this request
	 */
	private void setBody(HttpEntity entity) {
		if (!(request instanceof HttpEntityEnclosingRequest)) {
			throw new ApiTestException(request.getMethod() + " requests cannot have a body");
		}

		((HttpEntityEnclosingRequest) request).setEntity(entity);
	}

	/**
	 * Builds an Apache HTTP request object.
	 *
	 * @param method the HTTP method
	 * @param uri an URI builder
	 * @return an Apache HTTP request
	 * @throws ApiTestException if the HTTP method is not supported (see class constants for
	 * supported methods)
	 */
	private static HttpUriRequest buildRequestObject(String method, URI uri) {
		switch (method) {
			case GET:
				return new HttpGet(uri);
			case HEAD:
				return new HttpHead(uri);
			case POST:
				return new HttpPost(uri);
			case PUT:
				return new HttpPut(uri);
			case PATCH:
				return new HttpPatch(uri);
			case DELETE:
				return new HttpDelete(uri);
			default:
				throw new ApiTestException("Unsupported HTTP method " + method);
		}
	}
}
