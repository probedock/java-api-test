package io.probedock.api.test.headers;

import static org.mockito.Mockito.*;

import io.probedock.api.test.client.ApiTestRequest;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @see ApiHeadersManager
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
//@RoxableTestClass(tags = {"apiTestClient", "apiHeadersManager"})
public class ApiHeadersManagerUnitTest {

	@Mock
	private ApiTestRequest request;
	private ApiHeadersManager manager;

	@Before
	public void setUp() {
		manager = new ApiHeadersManager();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void apiHeadersManagerShouldRemoveRequestHeaders() {
		final InOrder inOrder = inOrder(request);

		removeHeader(header("X-A", "foo"), true);
		removeHeader(header("X-A", "bar"), false);
		removeHeader(header("X-B", "foo"), false);
		manager.applyConfiguration(request);

		inOrder.verify(request, times(2)).removeHeader("X-A");
		inOrder.verify(request).removeHeader("X-B");
		inOrder.verifyNoMoreInteractions();

		removeHeader(header("X-B", "bar"), false);
		manager.applyConfiguration(request);

		inOrder.verify(request).removeHeader("X-A");
		inOrder.verify(request).removeHeader("X-B");
		inOrder.verifyNoMoreInteractions();

		removeHeader(header("X-A", "baz"), true);
		manager.applyConfiguration(request);

		inOrder.verify(request).removeHeader("X-A");
		inOrder.verifyNoMoreInteractions();

		manager.applyConfiguration(request);

		inOrder.verify(request).removeHeader("X-A");
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void apiHeadersManagerShouldSetRequestHeaders() {
		final InOrder inOrder = inOrder(request);

		setHeader(header("X-A", "foo"), true);
		setHeader(header("X-A", "bar"), false);
		setHeader(header("X-B", "foo"), false);
		manager.applyConfiguration(request);

		inOrder.verify(request).setHeader("X-A", "foo");
		inOrder.verify(request).setHeader("X-A", "bar");
		inOrder.verify(request).setHeader("X-B", "foo");
		inOrder.verifyNoMoreInteractions();

		setHeader(header("X-B", "bar"), false);
		manager.applyConfiguration(request);

		inOrder.verify(request).setHeader("X-A", "foo");
		inOrder.verify(request).setHeader("X-B", "bar");
		inOrder.verifyNoMoreInteractions();

		setHeader(header("X-A", "baz"), true);
		manager.applyConfiguration(request);

		inOrder.verify(request).setHeader("X-A", "baz");
		inOrder.verifyNoMoreInteractions();

		manager.applyConfiguration(request);

		inOrder.verify(request).setHeader("X-A", "baz");
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void apiHeadersManagerShouldAddRequestHeaders() {
		final InOrder inOrder = inOrder(request);

		addHeader(header("X-A", "foo"), true);
		addHeader(header("X-A", "bar"), true);
		addHeader(header("X-B", "foo"), false);
		addHeader(header("X-B", "bar"), true);
		addHeader(header("X-C", "foo"), false);
		manager.applyConfiguration(request);

		inOrder.verify(request).addHeader("X-A", "foo");
		inOrder.verify(request).addHeader("X-A", "bar");
		inOrder.verify(request).addHeader("X-B", "bar");
		inOrder.verify(request).addHeader("X-B", "foo");
		inOrder.verify(request).addHeader("X-C", "foo");
		inOrder.verifyNoMoreInteractions();

		addHeader(header("X-A", "baz"), false);
		manager.applyConfiguration(request);

		inOrder.verify(request).addHeader("X-A", "foo");
		inOrder.verify(request).addHeader("X-A", "bar");
		inOrder.verify(request).addHeader("X-B", "bar");
		inOrder.verify(request).addHeader("X-A", "baz");
		inOrder.verifyNoMoreInteractions();

		manager.applyConfiguration(request);

		inOrder.verify(request).addHeader("X-A", "foo");
		inOrder.verify(request).addHeader("X-A", "bar");
		inOrder.verify(request).addHeader("X-B", "bar");
		inOrder.verifyNoMoreInteractions();
	}

	private void addHeader(final ApiHeader header, boolean forAllRequests) {
		manager.configure(ApiHeadersManager.Operation.ADD, header, forAllRequests);
	}

	private void setHeader(final ApiHeader header, boolean forAllRequests) {
		manager.configure(ApiHeadersManager.Operation.SET, header, forAllRequests);
	}

	private void removeHeader(final ApiHeader header, boolean forAllRequests) {
		manager.configure(ApiHeadersManager.Operation.REMOVE, header, forAllRequests);
	}

	private static ApiHeader header(final String name, final String value) {
		return new ApiHeader(name, value);
	}

	private static IApiHeaderConfiguration headers(final ApiHeader... headers) {
		return new IApiHeaderConfiguration() {
			@Override
			public List<ApiHeader> getHeaders() {
				return Arrays.asList(headers);
			}
		};
	}
}