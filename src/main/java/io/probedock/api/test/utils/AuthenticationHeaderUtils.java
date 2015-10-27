package io.probedock.api.test.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utilities API request signatures.
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class AuthenticationHeaderUtils {

	private static final String UTC_TIMEZONE = "UTC";
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * Returns the current UTC timestamp as string (ISO 8601).
	 */
	public static String getUtcTimestampAsString() {
		Calendar cal = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat(DATE_FORMAT);
		dfm.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
		return dfm.format(cal.getTime());
	}

	/**
	 * Returns an ISO 8601 timestamp for the given date.
	 */
	public static String getUtcTimestampAsString(Date date) {
		DateFormat dfm = new SimpleDateFormat(DATE_FORMAT);
		dfm.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
		return dfm.format(date);
	}

	/**
	 * Returns the current UTC timestamp.
	 */
	public static Date getUtcTimestamp() {
		return Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE)).getTime();
	}
}
