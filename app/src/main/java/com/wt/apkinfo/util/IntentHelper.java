package com.wt.apkinfo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;

import androidx.annotation.NonNull;

/**
 * Created by kenumir on 24.09.2017.
 *
 */

public class IntentHelper {

	public static boolean openInBrowser(@NonNull Context ctx, @NonNull String url) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			ctx.startActivity(browserIntent);
			return true;
		} catch (Exception ignore) {
			if (BuildConfig.DEBUG) {
				Console.loge("IntentHelper.openInBrowser: " + ignore.toString(), ignore);
			}
		}
		return false;
	}

}
