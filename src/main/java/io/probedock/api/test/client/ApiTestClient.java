package io.probedock.api.test.client;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * HTTP client wrapper.
 *
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class ApiTestClient {

	/**
	 * The internal Apache HTTP client.
	 */
	private CloseableHttpClient client;

	/**
	 * Constructs a new client. The client should be released with {@link #close()} when no longer
	 * useful.
	 * 
	 * @param clientConfiguration  The client configuration
	 */
	public ApiTestClient(final IApiTestClientConfiguration clientConfiguration) {
		if (clientConfiguration.isProxyEnabled()) {
			HttpHost proxy = new HttpHost(clientConfiguration.getProxyHost(), clientConfiguration.getProxyPort());

			// Create a proxy route planner to check if host should force to avoid using proxy
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy) {
				@Override
				public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
					// Retrieve the host name
					String hostname = host.getHostName();
					
					// Check each exceptions
					for (String hostToCheck : clientConfiguration.getProxyExceptions()) {
						if (hostname.equals(hostToCheck)) {
							return new HttpRoute(host);
						}
					}
					
					return super.determineRoute(host, request, context);
				}
			};
			
			client = HttpClients.custom().setRoutePlanner(routePlanner).build();
		}
		else {
			client = HttpClients.createDefault();
		}
	}
	
	/**
	 * Closes this client and all associated resources.
	 *
	 * @throws ApiTestException if the client could not be closed
	 */
	public void close() {
		try {
			client.close();
		} catch (IOException ex) {
			throw new ApiTestException("Could not close the HTTP client", ex);
		}
	}

	/**
	 * Performs an API request and returns the response.
	 *
	 * @param request the request to execute
	 * @return the API response
	 * @throws ApiTestException if an error occurred executing the request or consuming the response
	 */
	public ApiTestResponse execute(ApiTestRequest request) {

		final CloseableHttpResponse response;
		try {

			// execute the Apache request object
			response = client.execute(request.getRequestObject());

			// build and return the API response
			return buildResponse(response).enrichFromRequest(request);

		} catch (IOException ioe) {
			throw new ApiTestException("Could not complete request " + request, ioe);
		}
	}

	/**
	 * Builds an API response wrapper from an Apache HTTP response. Ensures the HTTP response is
	 * fully consumed and closed.
	 *
	 * @param response the HTTP response to consume
	 * @return an API response
	 * @throws IOException
	 */
	private ApiTestResponse buildResponse(CloseableHttpResponse response) throws IOException {

		final ApiTestResponse responseWrapper;
		try {
			responseWrapper = new ApiTestResponse(response);
			EntityUtils.consume(response.getEntity());
		} finally {
			response.close();
		}

		return responseWrapper;
	}
}
