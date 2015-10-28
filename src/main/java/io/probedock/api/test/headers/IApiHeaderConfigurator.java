package io.probedock.api.test.headers;

import java.util.List;

/**
 * Object capable of producing an {@link IApiHeaderConfiguration} to configure API request headers.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public interface IApiHeaderConfigurator {
	/**
	 * @return an API header configuration
	 */
	List<IApiHeaderConfiguration> getApiHeaderConfigurations();
}
