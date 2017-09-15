package com.wt.apkinfo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;

public class StartActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		((TextView) findViewById(R.id.info)).setText(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
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
