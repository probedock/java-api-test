package io.probedock.api.test.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 *
 * @author Simon Oulevay <simon.oulevay@probedock.io>
 */
public class SmartJsonObjectBuilder {

	private final JsonObjectBuilder builder;

	public SmartJsonObjectBuilder() {
		builder = Json.createObjectBuilder();
	}

	/**
	 * Adds a name/<tt>JsonValue</tt> pair to the JSON object associated with this object builder.
	 * If the object contains a mapping for the specified name, this method replaces the old value
	 * with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, JsonValue value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonString</tt> pair to the JSON object associated with this object builder.
	 * If the object contains a mapping for the specified name, this method replaces the old value
	 * with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, String value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonNumber</tt> pair to the JSON object associated with this object builder.
	 * If the object contains a mapping for the specified name, this method replaces the old value
	 * with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, BigInteger value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonNumber</tt> pair to the JSON object associated with this object builder.
	 * If the object contains a mapping for the specified name, this method replaces the old value
	 * with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, BigDecimal value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonNumber</tt> pair to the JSON object associated with this object builder.
	 * If the object contains a mapping for the specified name, this method replaces the old value
	 * with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, int value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonNumber</tt> pair to the JSON object associated with this object builder.
	 * If the object contains a mapping for the specified name, this method replaces the old value
	 * with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, long value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonNumber</tt> pair to the JSON object associated with this object builder.
	 * If the object contains a mapping for the specified name, this method replaces the old value
	 * with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, double value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonValue#TRUE</tt> or name/<tt>JsonValue#FALSE</tt> pair to the JSON object
	 * associated with this object builder. If the object contains a mapping for the specified name,
	 * this method replaces the old value with the specified value.
	 *
	 * @param name name in the name/value pair
	 * @param value value in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, boolean value) {
		builder.add(name, value);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonValue#NULL</tt> pair to the JSON object associated with this object
	 * builder where the value is null. If the object contains a mapping for the specified name,
	 * this method replaces the old value with null.
	 *
	 * @param name name in the name/value pair
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder addNull(String name) {
		builder.addNull(name);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonObject</tt> pair to the JSON object associated with this object builder.
	 * The value <tt>JsonObject</tt> is built from the specified object builder. If the object
	 * contains a mapping for the specified name, this method replaces the old value with the
	 * <tt>JsonObject</tt> from the specified object builder.
	 *
	 * @param name name in the name/value pair
	 * @param builder the value is the object associated with this builder
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, JsonObjectBuilder builder) {
		this.builder.add(name, builder);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonArray</tt> pair to the JSON object associated with this object builder.
	 * The value <tt>JsonArray</tt> is built from the specified array builder. If the object
	 * contains a mapping for the specified name, this method replaces the old value with the
	 * <tt>JsonArray</tt> from the specified array builder.
	 *
	 * @param name name in the name/value pair
	 * @param builder the value is the object array with this builder
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, JsonArrayBuilder builder) {
		this.builder.add(name, builder);
		return this;
	}

	/**
	 * Adds a name/<tt>JsonObject</tt> pair to the JSON object associated with this object builder.
	 * The value <tt>JsonObject</tt> is built from the specified object builder. If the object
	 * contains a mapping for the specified name, this method replaces the old value with the
	 * <tt>JsonObject</tt> from the specified object builder.
	 *
	 * @param name name in the name/value pair
	 * @param builder the value is the object associated with this builder
	 * @return the object builder
	 */
	public SmartJsonObjectBuilder add(String name, SmartJsonObjectBuilder builder) {
		this.builder.add(name, builder.builder);
		return this;
	}

	/**
	 * Add the JSON value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, JsonValue value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the string value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, String value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the big integer value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, BigInteger value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the big decimal value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, BigDecimal value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the integer value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, Integer value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the long value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, Long value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the double value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, Double value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the boolean value only if not null
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addIfNotNull(String name, Boolean value) {
		if (value != null) {
			builder.add(name, value);
		}
		return this;
	}

	/**
	 * Add the JSON value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, JsonValue value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Add the string value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, String value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Add the big integer value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, BigInteger value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Add the big decimal value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, BigDecimal value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Add the integer value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, Integer value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Add the long value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, Long value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Add the double value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, Double value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Add the boolean value but if null, add the null node
	 * 
	 * @param name The name of the node
	 * @param value The value
	 * @return this
	 */
	public SmartJsonObjectBuilder addValueOrNull(String name, Boolean value) {
		if (value != null) {
			builder.add(name, value);
		} else {
			builder.addNull(name);
		}
		return this;
	}

	/**
	 * Produce the JSON structure
	 * 
	 * @return The JSON object built
	 */
	public JsonObject build() {
		return builder.build();
	}
}
