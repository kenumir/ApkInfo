package com.wt.apkinfo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;

public class StartActivity extends AppCompatActivity {

	@SuppressLint("SetTextI18n")
	@Override
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
	protected void onResume() {
		super.onResume();
		new Handler(Looper.getMainLooper()).postDelayed(() -> {
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
			overridePendingTransition(0, 0);
			finish();
		}, 1000);
	}
}
