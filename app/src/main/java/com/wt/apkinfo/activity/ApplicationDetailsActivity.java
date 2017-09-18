package com.wt.apkinfo.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.perf.metrics.AddTrace;
import com.hivedi.console.Console;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kenumir on 17.09.2017.
 *
 */

public class ApplicationDetailsActivity extends AppCompatActivity {

	public static final String KEY_APP_ID = "application_id";

	@BindView(R2.id.toolbar) Toolbar toolbar;

	private String appId = null;

	@Override
	@AddTrace(name = "ApplicationDetailsActivity_onCreate")
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent() == null) {
			finish();
			return;
		}
		setContentView(R.layout.activity_application_details);
		ButterKnife.bind(this);

		appId = getIntent().getStringExtra(KEY_APP_ID);

		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(appId,
					PackageManager.GET_ACTIVITIES |
					PackageManager.GET_CONFIGURATIONS |
					PackageManager.GET_INTENT_FILTERS |
					PackageManager.GET_PERMISSIONS |
					PackageManager.GET_META_DATA |
					PackageManager.GET_PROVIDERS |
					PackageManager.GET_RECEIVERS |
					PackageManager.GET_SERVICES |
					PackageManager.GET_SIGNATURES |
					PackageManager.GET_URI_PERMISSION_PATTERNS |
					PackageManager.GET_INSTRUMENTATION |
					PackageManager.GET_SHARED_LIBRARY_FILES
			);
			Console.logi("APK: " + pi.applicationInfo.publicSourceDir);
			Console.logi("metaData: " + pi.applicationInfo.metaData);
			Console.logi("firstInstallTime: " + pi.firstInstallTime);
			Console.logi("lastUpdateTime: " + pi.lastUpdateTime);
			Console.logi("versionName: " + pi.versionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
