package io.probedock.api.test.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.json.Json;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;

/**
 * HTTP request body wrapper.
 *
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiTestRequestBody {

	//<editor-fold defaultstate="collapsed" desc="Media Type Constants">
	/**
	 * The <tt>application/json</tt> media type.
	 */
	public static final String APPLICATION_JSON = ContentType.APPLICATION_JSON.getMimeType();
	//</editor-fold>

	//<editor-fold defaultstate="collapsed" desc="Factory Methods">
	/**
	 * Constructs a request body with content type <tt>application/json</tt> from a JSON string.
	 *
	 * @param json the raw JSON string to use as request body
	 * @return an API request body
	 */
	public static ApiTestRequestBody fromJson(String json) {
		return new ApiTestRequestBody(json.getBytes(), APPLICATION_JSON);
	}

	/**
	 * Constructs a request body with content type <tt>application/json</tt> from a JSON structure.
	 *
	 * @param json the JSON structure to use as request body
	 * @return an API request body
	 */
	public static ApiTestRequestBody from(JsonStructure json) {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (JsonWriter writer = Json.createWriter(baos)) {
			writer.write(json);
		}

		return new ApiTestRequestBody(baos.toByteArray(), APPLICATION_JSON);
	}

	/**
	 * Constructs a request body with content type <tt>application/x-www-form-urlencoded</tt> from
	 * URL-encoded name/value pairs.
	 *
	 * @param data the name/value pairs to encode in form-urlencoded format and use as request body
	 * @return an API request body
	 */
	public static ApiTestRequestBody from(ApiTestFormUrlEncoded data) {

		final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data.getPairs(), StandardCharsets.UTF_8);
		return new ApiTestRequestBody(entity);
	}
	
	/**
	 * Constructs a request body with content type <tt>multipart/form-data</tt> from
	 * a multi part entity.
	 *
	 * @param data The multi part form data
	 * @return an API request body
	 */
	public static ApiTestRequestBody from(ApiTestMultipartFormData data) {
		return new ApiTestRequestBody(data.getMultipartEntity());
	}
	
	//</editor-fold>
	/**
	 * The request body.
	 */
	private final HttpEntity body;

	/**
	 * Constructs a new request body. The body is assumed to be encoded with the UTF-8 charset.
	 *
	 * @param data the request body
	 * @param mediaType the media type of the body
	 */
	public ApiTestRequestBody(byte[] data, String mediaType) {
		this(data, mediaType, StandardCharsets.UTF_8);
	}

	/**
	 * Constructs a new request body.
	 *
	 * @param data the request body
	 * @param mediaType the media type of the body
	 * @param charset the charset of the body
	 */
	public ApiTestRequestBody(byte[] data, String mediaType, Charset charset) {
		this(new InputStreamEntity(new ByteArrayInputStream(data),
				ContentType.create(mediaType, charset)));
	}

	/**
	 * Constructs a new request body.
	 *
	 * @param entity the request body entity
	 */
	protected ApiTestRequestBody(HttpEntity entity) {
		this.body = entity;
	}

	/**
	 * Converts the body to an Apache HTTP entity.
	 *
	 * @return an HTTP entity
	 * @see ApiTestRequest
	 */
	protected HttpEntity toEntity() {
		return body;
	}
}
