package com.wt.apkinfo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.perf.metrics.AddTrace;
import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kenumir on 17.09.2017.
 *
 */

public class ApplicationDetailsActivity extends AppCompatActivity {

	public static final String KEY_APP_ID = "application_id";

	@BindView(R2.id.toolbar) Toolbar toolbar;

	@Override
	@AddTrace(name = "ApplicationDetailsActivity_onCreate")
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_application_details);
		ButterKnife.bind(this);

		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
}
