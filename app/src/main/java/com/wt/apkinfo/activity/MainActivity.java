package com.wt.apkinfo.activity;

import android.os.Bundle;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.firebase.perf.metrics.AddTrace;
import com.hivedi.era.ERA;
import com.wt.apkinfo.R;
import com.wt.apkinfo.dialog.RateAppDialog;
import com.wt.apkinfo.fragment.ApplicationsFragment;
import com.wt.apkinfo.util.UserEngagement;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements InstallReferrerStateListener {

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
	}

	@Override
	public void onInstallReferrerSetupFinished(int responseCode) {
		ERA.log("onInstallReferrerSetupFinished: responseCode=" + responseCode);
		if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
			// TODO send info
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
}
