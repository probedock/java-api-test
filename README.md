# java-api-test

> A Java library to test JSON APIs with JUnit.

We created this library to help writing JSON API testing. We introduced a lot of utility methods to facilitate the management of the URI building and to prepare the headers for the request. The lib takes care through some mechanism to populate authentication headers and so on.

## Usage

1. Put the following dependency in your pom.xml

```xml
<dependency>
  <groupId>io.probedock.test</groupId>
  <artifactId>java-api-test</artifactId>
  <version>1.0.0</version>
</dependency>
```

2. We need to create a configuration class to enable the possibility to configure a proxy when the tests are run. There is an example of the configuration class which implements `IApiTestClientConfiguration` and the `properties` file with the configuration. The configuraiton is a `Singleton`.

  ```java
  public class Configuration implements IApiTestClientConfiguration {
    // Load the config.properties that is present in the classpath
    private static final ResourceBundle CONFIG = ResourceBundle.getBundle("config");

    // Constants for the configuration names
    private static final String BASE_URL = "base.url";
    private static final String PROXY_ENABLED = "proxy.enabled";
    private static final String PROXY_HOST = "proxy.host";
    private static final String PROXY_PORT = "proxy.port";
    private static final String PROXY_EXCEPTIONS = "proxy.exceptions";

    // Singleton
  	private static final Configuration instance = new Configuration();

    // Forbid creation of new instances
    private Configuration() {}

    /**
     * @return The configuration instance
     */
    public static Configuration getInstance() {
    	return instance;
    }

    /**
     * @return The base URL
     * We added this to get the root URL for the tests. It's up to you
     * to do something similar or to find another way to get this information
     * during test execution.
     */
    public String getBaseUrl() {
      return CONFIG.getString(BASE_URL);
    }

    @Override
    public boolean isProxyEnabled() {
    	return Boolean.parseBoolean(CONFIG.getString(PROXY_ENABLED));
    }

    @Override
    public String getProxyHost() {
    	return CONFIG.getString(PROXY_HOST);
    }

    @Override
    public int getProxyPort() {
    	return Integer.parseInt(CONFIG.getString(PROXY_PORT));
    }

    @Override
    public String[] getProxyExceptions() {
    	return CONFIG.getString(PROXY_EXCEPTIONS).split(",");
    }
  }
  ```

  And the configuration file:

  ```java-api-test
  base.url.ext=http://localhost:8080
  proxy.enabled=true
  proxy.host=localhost
  proxy.port=8000
  proxy.exceptions=127.0.0.1,localhost
  ```

3. We need to create an `ApiHeaderConfiguratorLocator`. In fact, the idea behind this is to let the possibility to create header enrichers that are configured through annotations on the test methods. These enrichers can be managed for the dependency injection and then must be looked up in a different way. So this component is responsible to retrieve an instance of API header configurator based on the given class. Or it can also be useful to implement a sort of cache. Let's see that with this example:

  ```java
  public class HeaderConfiguratorLocator implements IApiHeaderConfiguratorLocator {
    // Lookup configurator instance
    private static final ConfiguratorLookup configuratorLookup = new ConfiguratorLookup();

    @Override
    public IApiHeaderConfigurator getHeaderConfigurator(Class<? extends IApiHeaderConfigurator> klass) {
      // Do the lookup
      return configuratorLookup.lookup(klass);
    }

    /**
     * Implement a sort of caching mechanism and suppose the header configurators has an empty constructor
     */
    private static class ConfiguratorLookup {
      // Manage the cache
      private final Map<Class<? extends IApiHeaderConfigurator>, IApiHeaderConfigurator> cache = new HashMap<>();

      /**
       * Do the lookup based on the class
       *
       * @param klass The class to lookup
       * @return The instance of the configurator or null if not found
       */
      protected IApiHeaderConfigurator lookup(Class<? extends IApiHeaderConfigurator> klass) {
        // Return the existing instance
        if (cache.containsKey(klass)) {
          return cache.get(klass);
        }
        else {
          try {
            // Try to create a new instance and add it to the cache
            IApiHeaderConfigurator configurator = klass.newInstance();
            cache.put(klass, configurator);
            return configurator;
          }
          catch (IllegalAccessException | InstantiationException e) {
            // Empty constructor. In practice, we will at least log a warning to
            // notify the test developers about the problem.
            return null;
          }
        }
      }
    }
  }
  ```

4. Create the base API test class that all your tests will inherit. It inherits from `AbstractApiTest`.

  ```java
  public class MyAbstractApiTest extends AbstractApiTest {
    // Reference to the configuration
    private static final Configuration CONFIGURATION = Configuration.getInstance();

    // Create the header configurator locator
    private static final HeaderConfiguratorLocator headerConfiguratorLocator = new HeaderConfiguratorLocator();

    @Override
    protected void preBuild() {
      // Do all the stuff you want. This method is called by the constructor of AbstractApiTest
    }

    @Override
    protected String getEntryPoint() {
      // Shortcut method to get the base URL
      return CONFIGURATION.getBaseUrl();
    }

    @Override
    protected IApiTestClientConfiguration getClientConfiguration() {
      // Retrieve the configuration
      return CONFIGURATION;
    }

    @Override
    protected IApiHeaderConfiguratorLocator getHeaderConfiguratorLocator() {
      // Retrieve the header configurator locator
      return headerConfiguratorLocator;
    }
  }
  ```

5. It's time to write a test. No wait, we need to prepare an authentication header to be used during the tests. Ok, here we go with an example of the `ApiHeaderConfigurator`.

  ```java
  public class UserAuthenticationHeaderConfigurator implements IApiHeaderConfigurator {
    @Override
    public List<IApiHeaderConfiguration> getApiHeaderConfigurations() {
      // We need to return an array of configurations. This means that we
      // can provide multiple header configurations at once.
      return Arrays.asList(new IApiHeaderConfiguration[]{
        // Prepare an authentication header configuration. This class
        // comes from the testing lib.
        new AuthenticationBasicApiHeaderConfiguration("username", "password")
      });
    }
  }
  ```

6. Ok, now we are ready to write our first test.

  ```java
  public class MyUserTest extends AbstractApiTest {
    @Test
    // We configure our header configurator to be managed by the lib
    @ApiHeaderConfigurator(UserAuthenticationHeaderConfigurator.class)
    public void itShouldMakeTheLifeEasier() {
      // Try to retrieve the user info on <baseUrl>/users/userId.
      // Oh, wait! Where is the authentication? In fact, the lib will take
      // care to add the headers retrieved from the header configurators. So,
      // the header injection is done for you automatically and for the
      // requests done in the test. It is possible to override this behavior
      // in the test by using some methods provided by the AbstractApiTest class.
      ApiTestResponse response = getResource(uri("users", "userId"));

      // Make all the assertions you want based on the response.
      assertEquals(200, response.getStatus());
      assertNotNull(response.getResponseAsJsonObject().getString("email"));
      ...
    }
  }
  ```

7. What's next? You need to take a deeper look on the APIs offered by the `AbstractApiTest` class. You have several methods to manipulate the headers and to do the `POST`, `PUT`, `PATCH`, `DELETE` and `GET` requests. You also have access to `uri()` method which will provide you an `ApiUriBuilder` to prepare the request (path, query params and headers). You also have several methods to interact with the headers for only the next request or all the next requests.

8. If you are interested by interact with a database in a Java EE application, you should take a look on the [junitee-data-utils](https://github.com/probedock/junitee-data-utils) repository. There is also a [doc](https://github.com/probedock/junitee-data-utils) to see how to integrate these two projects together to take advantages on persistence layer during JSON API testing.

### Requirements

* Java 6+

## Contributing

* [Fork](https://help.github.com/articles/fork-a-repo)
* Create a topic branch - `git checkout -b feature`
* Push to your branch - `git push origin feature`
* Create a [pull request](http://help.github.com/pull-requests/) from your branch

Please add a changelog entry with your name for new features and bug fixes.

## License

**java-api-test** is licensed under the [MIT License](http://opensource.org/licenses/MIT).
See [LICENSE.txt](LICENSE.txt) for the full text.
