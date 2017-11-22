package com.wt.apkinfo;

import android.app.Application;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;
import com.google.firebase.perf.metrics.AddTrace;
import com.hivedi.console.Console;

import io.fabric.sdk.android.Fabric;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class App extends Application {

	@Override
	@AddTrace(name = "App_onCreate")
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
		} else {
			new ANRWatchDog().setReportMainThreadOnly().setANRListener(new ANRWatchDog.ANRListener() {
				@Override
				public void onAppNotResponding(ANRError error) {
					Answers.getInstance().logCustom(new CustomEvent("ANR Error Detected"));
					Crashlytics.logException(error);
				}
			}).start();
		}

		final Fabric fabric = new Fabric.Builder(this)
				.kits(new Crashlytics(), new Answers())
				.debuggable(true)
				.build();
		Fabric.with(fabric);
	}
}
