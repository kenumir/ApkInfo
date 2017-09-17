package com.wt.apkinfo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.perf.metrics.AddTrace;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;

public class StartActivity extends AppCompatActivity {

	@Override
	@AddTrace(name = "StartActivity_onCreate")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		((TextView) findViewById(R.id.info)).setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
	}

	@Override
	public void finish() {
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		super.finish();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				overridePendingTransition(0, 0);
				finish();
			}
		}, 1000);
	}
}
