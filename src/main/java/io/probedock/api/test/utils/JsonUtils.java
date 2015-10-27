package io.probedock.api.test.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * Utility methods when working with JSON objects
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class JsonUtils {
	public static JsonObjectBuilder mapToJsonObjectBuilder(Map<String, String> map) {
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		if (map == null || map.isEmpty()) {
			return jsonObject;
		}
		Iterator<Entry<String, String>> i = map.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, String> e = i.next();
			jsonObject.add(e.getKey(), e.getValue());
		}
		return jsonObject;
	}

	/**
	 * Method used to build the JSON Array of Strings.
	 *
	 * @param list The list of string
	 * @return A JsonArray object
	 */
	public static JsonArray listToJsonArray(List<String> list) {
		JsonArrayBuilder ab = Json.createArrayBuilder();
		
		for (String element : list) {
			ab.add(element);
		}
		
		return ab.build();
	}
}
