package io.probedock.api.test.rules;

import io.probedock.api.test.headers.ApiHeadersManager;
import org.junit.rules.ExternalResource;

/**
 * JUnit rule to create a headers manager to manage request headers for each test.
 *
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiTestHeadersManagerRule extends ExternalResource {

	/**
	 * The headers manager.
	 */
	private ApiHeadersManager headersManager;

	@Override
	protected void before() throws Throwable {
		headersManager = new ApiHeadersManager();
	}

	@Override
	protected void after() {
	}

	/**
	 * Returns the headers manager.
	 *
	 * @return a headers manager
	 */
	public ApiHeadersManager getHeadersManager() {
		return headersManager;
	}
}
