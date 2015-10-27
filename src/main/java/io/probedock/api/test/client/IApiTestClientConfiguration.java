package io.probedock.api.test.client;

/**
 * Define the configuration of an API Test client
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public interface IApiTestClientConfiguration {
	/**
	 * @return True if proxy should be used
	 */
	boolean isProxyEnabled();
	
	/**
	 * @return Proxy host
	 */
	String getProxyHost();
	
	/**
	 * @return Proxy port
	 */
	int getProxyPort();
	
	/**
	 * @return The host for which a proxy should not be used
	 */
	String[] getProxyExceptions();
}
