package io.probedock.api.test.client;

/**
 * Thrown to indicate a problem with the API test client configuration or the execution of an API
 * request.
 *
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiTestException extends RuntimeException {

	/**
	 * Constructs a new exception.
	 *
	 * @param message the detail message
	 */
	public ApiTestException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception.
	 *
	 * @param message the detail message
	 * @param cause the cause
	 */
	public ApiTestException(String message, Throwable cause) {
		super(message, cause);
	}
}
