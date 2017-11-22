package com.wt.apkinfo.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;


public class StartAlarmReceiver extends WakefulBroadcastReceiver {
	

	public StartAlarmReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (BuildConfig.DEBUG) {
			Console.logi("StartAlarmReceiver: onReceive");
		}
		Intent service = new Intent(context, StartAlarmService.class);
		startWakefulService(context, service);

	}
}
