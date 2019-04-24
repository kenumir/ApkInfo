package com.wt.apkinfo.activity;

import android.os.Bundle;
import android.os.RemoteException;

import androidx.appcompat.app.AppCompatActivity;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.metrics.AddTrace;
import com.hivedi.era.ERA;
import com.wt.apkinfo.App;
import com.wt.apkinfo.R;
import com.wt.apkinfo.dialog.RateAppDialog;
import com.wt.apkinfo.fragment.ApplicationsFragment;
import com.wt.apkinfo.util.UserEngagement;
import com.wt.replaioad.OnInstallButtonClick;

public class MainActivity extends AppCompatActivity implements InstallReferrerStateListener, OnInstallButtonClick {

	private InstallReferrerClient mReferrerClient;
	private ApplicationsFragment mApplicationsFragment;

	@Override
	@AddTrace(name = "MainActivity_onCreate")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mApplicationsFragment = (ApplicationsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mainFrame);
		if (mApplicationsFragment == null) {
			mApplicationsFragment = new ApplicationsFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.mainFrame, mApplicationsFragment)
					.commit();
		}

		mReferrerClient = InstallReferrerClient.newBuilder(this).build();
		try {
			mReferrerClient.startConnection(this);
		} catch (Exception e) {
			ERA.logException(e);
		}

		if (savedInstanceState == null) {
			UserEngagement.showRateDialog(this, () -> {
				if (!isFinishing()) {
					new RateAppDialog().show(getSupportFragmentManager(), "rate_app");
					UserEngagement.markRateDialogAsOpened(getApplicationContext());
				}
			});
		}

		((App)getApplication()).getReplaioAdConfig().configure(findViewById(R.id.replaioAdView), this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		((App)getApplication()).getReplaioAdConfig().refreshSettings();
	}

	@Override
	public void onInstallReferrerSetupFinished(int responseCode) {
		ERA.log("onInstallReferrerSetupFinished: responseCode=" + responseCode);
		if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
			try {
				ReferrerDetails response = mReferrerClient.getInstallReferrer();
				ERA.log("onInstallReferrerSetupFinished: InstallReferrer=" + response.getInstallReferrer());
				mReferrerClient.endConnection();
			} catch (RemoteException e) {
				ERA.logException(e);
			}
		}
	}

	@Override
	public void onInstallReferrerServiceDisconnected() {

	}

	@Override
	public void onBackPressed() {
		if (mApplicationsFragment.onBackAction()) {
			// skip back action - handle by fragment
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onInstallButtonClick() {
		FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("replaio_ad_click", null);
	}
}
