package io.probedock.api.test.headers;

import java.util.List;

/**
 * List of API headers to add to an HTTP request. It can also be used to remove headers.
 *
 * @see ApiHeadersManager
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public interface IApiHeaderConfiguration {
	/**
	 * @return Returns the headers defined by this configuration.
	 */
	List<ApiHeader> getHeaders();
}
