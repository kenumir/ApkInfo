package com.wt.apkinfo;

import android.app.Application;
import android.os.StrictMode;

import com.hivedi.console.Console;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class App extends Application {

	@Override
	public void onCreate() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll()
					.penaltyLog()
					//.penaltyDeath()
					.build());
		}
		super.onCreate();
		if (BuildConfig.DEBUG) {
			Console.setEnabled(true);
			Console.setTag("ApkInfo");
			Console.addLogWriterLogCat();
		}
	}
}
