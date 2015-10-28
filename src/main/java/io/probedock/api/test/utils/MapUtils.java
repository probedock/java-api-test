package io.probedock.api.test.utils;

import java.util.Map;
import java.util.Map.Entry;
import org.junit.Assert;

/**
 * Utility class to manipulate maps in a test environment. 
 * 
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class MapUtils {
	
	/**
	 * Ugly method to get the first entry of a map.
	 * Use only in tests, only if you understand why you need this.
	 * As maps are not ordered, you should really only use this with maps 
	 * containing only one entry. 
	 * 
	 * @param map The map to parse
	 * @return The first entry encountered
	 */
	public static Entry<String, String> getUniqueEntry(Map<String, String> map) throws IndexOutOfBoundsException {
		if (map == null || map.size() != 1) {
			throw new IndexOutOfBoundsException("The size of the Map should be 1");
		}
		return map.entrySet().iterator().next();
	}
	
	/**
	 * Validate map pairs.
	 *
	 * @param message message to output in case of problem; it may contain 3 %s
	 * which will be substituted by the (problematic property name, expected
	 * value, actual value).
	 * @param expectedMap expected Map
	 * @param actualMap actual Map to check for equality
	 */
	public static void checkMap(String message, Map<? extends Object, ? extends Object> expectedMap, Map<? extends Object, ? extends Object> actualMap) {
		// Check that for each key of the expectedMap, the value in the expectedMap matches the value in the actualMap
		for (Object key : expectedMap.keySet()) {
			// Note: for performance reasons, I don't use assertEquals (would have to build the message each time)
			if (!expectedMap.get(key).equals(actualMap.get(key))) {
				Assert.fail(String.format(message, key, expectedMap.get(key), actualMap.get(key)));
			}
		}
		// Check that there is no key only present the actualMap
		for (Object key : actualMap.keySet()) {
			if (!expectedMap.containsKey(key)) {
				Assert.fail(String.format(message, key, "(null)", actualMap.get(key)));
			}
		}
	}
	
	/**
	 * Convert a map to a string representation (e.g: for toString())
	 * 
	 * @param map The map to convert
	 * @return The string representation of the map
	 */
	public static String mapToString(Map<? extends Object, ? extends Object> map) {
		StringBuilder sb = new StringBuilder();
		if (map == null || map.isEmpty()) {
			return sb.toString();
		}
		for (Object key : map.keySet()) {
			sb.append(key.toString());
			sb.append(" : ");
			sb.append(map.get(key).toString());
			sb.append(" ; ");
		}
		return sb.toString();
	}
}
