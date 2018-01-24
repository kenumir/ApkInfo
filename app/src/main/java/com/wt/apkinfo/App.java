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
import com.hivedi.era.ERA;
import com.hivedi.era.ReportInterface;
import com.wt.apkinfo.util.ImageLoader;

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
					ERA.logException(error);
				}
			}).start();
		}

		final Fabric fabric = new Fabric.Builder(this)
				.kits(new Crashlytics(), new Answers())
				.debuggable(true)
				.build();
		Fabric.with(fabric);

		ERA.registerAdapter(new ReportInterface() {
			@Override
			public void logException(Throwable throwable, Object... objects) {
				Crashlytics.logException(throwable);
			}
			@Override
			public void log(String s, Object... objects) {
				Crashlytics.log(s);
			}
			@Override
			public void breadcrumb(String s, Object... objects) {
			}
		});

		ImageLoader.init(getApplicationContext());
	}
}
