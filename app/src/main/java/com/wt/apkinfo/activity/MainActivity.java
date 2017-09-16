package com.wt.apkinfo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.wt.apkinfo.R;
import com.wt.apkinfo.R2;
import com.wt.apkinfo.fragment.ApplicationsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	@BindView(R2.id.mainFrame) FrameLayout mainFrame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		ApplicationsFragment mApplicationsFragment = (ApplicationsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mainFrame);
		if (mApplicationsFragment == null) {
			mApplicationsFragment = new ApplicationsFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.mainFrame, mApplicationsFragment)
					.commit();
		}
	}
}
