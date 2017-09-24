package com.wt.apkinfo.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by kenumir on 24.09.2017.
 *
 */

public class DateTime {

	public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss";

	public static String formatFull(Long timestamp) {
		return format(timestamp, FORMAT_FULL);
	}

	public static String format(Long timestamp, String format) {
		return new SimpleDateFormat(format, Locale.getDefault()).format(timestamp);
	}
}
