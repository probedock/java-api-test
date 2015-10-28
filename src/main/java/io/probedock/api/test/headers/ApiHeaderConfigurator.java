package io.probedock.api.test.headers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to automatically enrich API request headers during a test.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiHeaderConfigurator {
	/**
	 * Returns the type of API header configurator that will be used to configure request headers.
	 *
	 * @return an API header configurator class
	 */
	Class<? extends IApiHeaderConfigurator>[] value();
}
