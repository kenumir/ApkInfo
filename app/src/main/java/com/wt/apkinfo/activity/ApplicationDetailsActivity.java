package com.wt.apkinfo.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.perf.metrics.AddTrace;
import com.hivedi.console.Console;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;
import com.wt.apkinfo.viewmodel.ApplicationDetailsViewModel;

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
	@BindView(R2.id.text1) TextView text1;
	@BindView(R2.id.text2) TextView text2;
	@BindView(R2.id.icon1) ImageView icon1;

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

		ApplicationDetailsViewModel.Factory factory = new ApplicationDetailsViewModel.Factory(getApplication(), appId);
		final ApplicationDetailsViewModel model = ViewModelProviders.of(this, factory).get(ApplicationDetailsViewModel.class);
		model.getApplicationDetails().observe(this, new Observer<ApplicationDetailsEntity>() {
			@Override
			public void onChanged(@Nullable ApplicationDetailsEntity productEntity) {
				model.setProduct(productEntity);
				Console.loge("productEntity=" + productEntity);
			}
		});

		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

	}
}
