package io.probedock.api.test.client;

import java.io.File;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * Container to send form data
 *
 * @author Valentin Delaye <valentin.delaye@novaccess.ch>
 */
public class ApiTestMultipartFormData {
	
	private MultipartEntityBuilder builder;

	public ApiTestMultipartFormData() {
		builder = MultipartEntityBuilder.create();
	}
	
	public ApiTestMultipartFormData addData(String key, byte[] array) {
		builder.addBinaryBody(key, array);
		return this;
	}
	
	public ApiTestMultipartFormData addData(String key, File file) {
		builder.addBinaryBody(key, file);
		return this;
	}
	
	public ApiTestMultipartFormData addData(String key, InputStream stream) {
		builder.addBinaryBody(key, stream);
		return this;
	}
	
	public ApiTestMultipartFormData addData(String key, String value, String contentType) {
		builder.addTextBody(key, value, ContentType.create(contentType));
		return this;
	}
	
	public ApiTestMultipartFormData addData(String key, String value) {
		builder.addTextBody(key, value);
		return this;
	}
	
	public HttpEntity getMultipartEntity() {
		return builder.build();
	}
	
	
}