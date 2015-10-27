package io.probedock.api.test.headers;

import java.util.Arrays;
import java.util.List;

/**
 * API header configuration that will add {@link AuthenticationBasicApiHeader} to the request.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class AuthenticationBasicApiHeaderConfiguration implements IApiHeaderConfiguration {

	/**
	 * The username to use for basic authentication.
	 */
	private String user;
	/**
	 * The password to use for basic authentication.
	 */
	private String password;

	/**
	 * Constructs a new configuration.
	 *
	 * @param user the username
	 * @param password the password
	 */
	public AuthenticationBasicApiHeaderConfiguration(String user, String password) {
		this.user = user;
		this.password = password;
	}

	@Override
	public List<ApiHeader> getHeaders() {
		return Arrays.asList(new ApiHeader[]{buildHeader()});
	}

	/**
	 * @return the API header for basic authentication
	 */
	private ApiHeader buildHeader() {
		return new AuthenticationBasicApiHeader(user, password);
	}
}
