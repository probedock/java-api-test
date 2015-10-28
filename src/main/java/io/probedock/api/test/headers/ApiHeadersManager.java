package io.probedock.api.test.headers;

import io.probedock.api.test.client.ApiTestRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manager of API request headers that allows setting headers for all requests or only for the next
 * request.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class ApiHeadersManager {
	/**
	 * Operation that represents the modification of an HTTP request header.
	 */
	public static enum Operation implements ApiRequestHeaderModification {
		/**
		 * Add a header. The header is appended to the end of the list. Previous headers with the
		 * same name are kept.
		 */
		ADD {
			@Override
			public void apply(ApiTestRequest request, ApiHeader header) {
				request.addHeader(header.getName(), header.computeValue(request));
			}
		},
		
		/**
		 * Sets a header. Previous headers with the same name are overwritten.
		 */
		SET {
			@Override
			public void apply(ApiTestRequest request, ApiHeader header) {
				request.setHeader(header.getName(), header.computeValue(request));
			}
		},
		
		/**
		 * Removes a header. All headers with the same name are removed.
		 */
		REMOVE {
			@Override
			public void apply(ApiTestRequest request, ApiHeader header) {
				request.removeHeader(header.getName());
			}
		};
	}

	/**
	 * Header operations to apply to all requests.
	 */
	private final List<HeaderOperation> permanentHeaders;
	
	/**
	 * Header operations to apply only to the next request (cleared after calling
	 * <tt>configure</tt>).
	 */
	private final List<HeaderOperation> nextRequestHeaders;

	/**
	 * Constructs an empty headers manager.
	 */
	public ApiHeadersManager() {
		permanentHeaders = new ArrayList<>();
		nextRequestHeaders = new ArrayList<>();
	}

	/**
	 * Modifies a request header. Call
	 * {@link ApiHeadersManager#applyConfiguration(io.probedock.api.test.client.ApiTestRequest)} to apply all configured
	 * modifications to a request.
	 *
	 * @param op what to do with the header (ADD/SET/REMOVE)
	 * @param header the header to modify
	 * @param forAllRequests true to apply the modification to all subsequent requests, false to
	 * apply it only to the next request
	 * @return this manager
	 * @throws IllegalArgumentException if the operation or header are null
	 */
	public ApiHeadersManager configure(final Operation op, final ApiHeader header, final boolean forAllRequests) {
		if (op == null) {
			throw new IllegalArgumentException("Operation cannot be null");
		} else if (header == null) {
			throw new IllegalArgumentException("Header cannot be null");
		}

		// create the header operation instance
		final HeaderOperation operation = new HeaderOperation(header, op);

		// remove operations that would become superfluous after adding this one, e.g. a previously
		// added SET operation becomes useless if we add a REMOVE for the same header
		cleanHeaderOperations(operation, forAllRequests);

		// add the operation to permanent headers or next request headers depending on the supplied boolean
		(forAllRequests ? permanentHeaders : nextRequestHeaders).add(operation);

		//<editor-fold defaultstate="collapsed" desc="Special REMOVE/ADD Case">
		/*
		 * Special case: if a header is removed for the next request, and then added for all
		 * requests, it would not be normally added because next request operations overwrite
		 * operations for all requests (see #configure).
		 *
		 * Example:
		 * - REMOVE X-Custom-Header false   // next request operation, will be run last
		 * - ADD X-Custom-Header true       // all requests operation, will be run first
		 *
		 * To fix this, we automatically also add the header to the next request:
		 * - ADD X-Custom-Header false
		 */
		if (Operation.ADD.equals(op) && forAllRequests) {
			nextRequestHeaders.add(operation);
		}
		//</editor-fold>

		return this;
	}

	/**
	 * Modifies multiple request headers. Call
	 * {@link ApiHeadersManager#applyConfiguration(io.probedock.api.test.client.ApiTestRequest)} to apply all configured
	 * modifications to a request.
	 *
	 * @param op what to do with the headers (ADD/SET/REMOVE)
	 * @param headerConfiguration the headers to modify
	 * @param forAllRequests true to apply the modifications to all subsequent requests, false to
	 * apply it only to the next request
	 * @return this manager
	 * @throws IllegalArgumentException if the operation or header configuration are null or the
	 * header configuration returns a list containing null elements
	 */
	public ApiHeadersManager configure(final Operation op, final IApiHeaderConfiguration headerConfiguration, final boolean forAllRequests) {
		if (headerConfiguration == null) {
			throw new IllegalArgumentException("Header configuration cannot be null");
		}

		// apply the operation for each header in the configuration
		for (final ApiHeader header : headerConfiguration.getHeaders()) {
			configure(op, header, forAllRequests);
		}

		return this;
	}

	/**
	 * Configures the specified request with the headers of this manager. This will apply all
	 * permanent header modifications and modifications specific to the next request. Next request
	 * modifications are then cleared.
	 *
	 * @param request the request to configure
	 * @return this manager
	 */
	public ApiHeadersManager applyConfiguration(ApiTestRequest request) {

		// apply all permanent header operations
		for (final HeaderOperation op : permanentHeaders) {
			op.apply(request);
		}

		// apply all header operations for the next request
		for (final HeaderOperation op : nextRequestHeaders) {
			op.apply(request);
		}

		// clear operations for the next request
		nextRequestHeaders.clear();

		return this;
	}

	/**
	 * Removes header operations that would become superfluous after adding the specified one. For
	 * example, a previously added SET operation becomes useless if a REMOVE is added for the same
	 * header.
	 *
	 * @param op the operation that will be added
	 * @param forAllRequests whether the operation will be applied to all subsequent requests or
	 * only the next one
	 */
	private void cleanHeaderOperations(HeaderOperation op, boolean forAllRequests) {
		
		// only SET and REMOVE operations require cleaning
		if (!Operation.ADD.equals(op.getOperation())) {
			
			// all previously added operations for the next request become superflous
			clearHeaderOperations(nextRequestHeaders, op.getHeaderName());
			
			// previous operations for all requests become superflous only if the operation being
			// added also applies to all requests
			if (forAllRequests) {
				clearHeaderOperations(permanentHeaders, op.getHeaderName());
			}
		}
	}
	
	/**
	 * Removes header operations from a list.
	 *
	 * @param operations the list from which to remove operations
	 * @param headerName all operations with that header name will be removed
	 */
	private static void clearHeaderOperations(List<HeaderOperation> operations, String headerName) {
		
		final Iterator<HeaderOperation> iterator = operations.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getHeaderName().equals(headerName)) {
				iterator.remove();
			}
		}
	}

	/**
	 * Modification of an API request header.
	 */
	private static interface ApiRequestHeaderModification {
		
		/**
		 * Applies the modification.
		 *
		 * @param request the request to modify
		 * @param header the header
		 */
		void apply(ApiTestRequest request, ApiHeader header);
	}

	/**
	 * Wrapper to apply a header operation.
	 */
	private static class HeaderOperation {
		
		/**
		 * The header.
		 */
		private final ApiHeader header;
		/**
		 * The operation to apply to the header.
		 */
		private final Operation operation;
		
		/**
		 * Constructs a new operation.
		 *
		 * @param header the header
		 * @param operation the operation to apply to the header
		 */
		public HeaderOperation(ApiHeader header, Operation operation) {
			this.header = header;
			this.operation = operation;
		}
		
		/**
		 * @return the operation to apply to the header
		 */
		public Operation getOperation() {
			return operation;
		}
		
		/**
		 * @return the header name
		 */
		public String getHeaderName() {
			return header.getName();
		}
		
		/**
		 * Applies this operation to the specified request.
		 *
		 * @param request the request to configure
		 */
		public void apply(ApiTestRequest request) {
			operation.apply(request, header);
		}
	}
}