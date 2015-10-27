package io.probedock.api.test.client;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * HTTP response wrapper.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiTestResponse {

	/**
	 * The internal Apache HTTP response.
	 */
	private HttpResponse response;
	/**
	 * The cached response body.
	 */
	private String responseBody;

	/**
	 * URI from the request
	 */
	private URI requestUri;
	
	/**
	 * Constructs a new API response from an Apache HTTP response.
	 *
	 * @param response the HTTP response
	 * @throws IOException if the response could not be consumed or closed
	 */
	protected ApiTestResponse(HttpResponse response) throws IOException {
		this.response = response;
		this.responseBody = readResponseBody(response);
	}

	/**
	 * Returns the HTTP status code of this response.
	 *
	 * @return an integer status code
	 * @link http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
	 */
	public int getStatus() {
		return response.getStatusLine().getStatusCode();
	}

	/**
	 * Returns the value of the specified response header. If the header is present multiple times
	 * in the response, the value of the first header is returned.
	 *
	 * @param headerName the header name
	 * @return the value of the first response header with the given name, or null if there is none
	 */
	public String getHeaderString(String headerName) {
		return response.getFirstHeader(headerName).getValue();
	}

	/**
	 * Returns the response body as a string. If the response has no body, an empty string is
	 * returned.
	 *
	 * @return the response body string (which may be empty but not null)
	 */
	public String getResponseAsString() {
		return responseBody != null ? responseBody : "";
	}

	/**
	 * Returns the response body as a JSON object.
	 *
	 * @return a JSON object
	 * @throws JsonException if the response body could not be read or is not valid JSON
	 */
	public JsonObject getResponseAsJsonObject() {
		return Json.createReader(new StringReader(getResponseAsString())).readObject();
	}
	
	/**
	 * @return The request URI
	 */
	public URI getRequestUri() {
		return requestUri;
	}
	
	/**
	 * Gather information from the request to enrich the info of the response
	 * 
	 * @param request The request to get the info
	 * @return this
	 */
	public ApiTestResponse enrichFromRequest(ApiTestRequest request) {
		requestUri = request.getUri();
		return this;
	}

	/**
	 * Returns the response body as a JSON array.
	 *
	 * @return a JSON array
	 * @throws JsonException if the response body could not be read or is not valid JSON
	 */
	public JsonArray getResponseAsJsonArray() {
		return Json.createReader(new StringReader(getResponseAsString())).readArray();
	}
	
	/**
	 * Returns the HTTP response body as an UTF-8 string.
	 *
	 * @param response the HTTP response whose body to read
	 * @return the response body as a string
	 * @throws IOException if the response entity could not be read
	 */
	private static String readResponseBody(HttpResponse response) throws IOException {
		return response.getEntity() != null ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8) : null;
	}
}
