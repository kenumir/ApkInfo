package com.wt.apkinfo.activity;

import android.app.IntentService;
import android.content.Intent;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;

public class StartAlarmService extends IntentService {

	public static final String KEY_ALARM_ID = "alarm_id";

	public StartAlarmService() {
		super("StartRecordingService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			long alarmId = intent.getLongExtra(KEY_ALARM_ID, 0L);

			if (BuildConfig.DEBUG) {
				Console.logi("onHandleIntent alarmId=" + alarmId);
			}

			Intent it = new Intent(getApplicationContext(), AlarmActivity.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(it);
		}
	}

}
