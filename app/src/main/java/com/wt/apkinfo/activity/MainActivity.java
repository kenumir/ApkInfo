package com.wt.apkinfo.activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.hivedi.console.Console;
import com.hivedi.era.ERA;
import com.wt.apkinfo.App;
import com.wt.apkinfo.R;
import com.wt.apkinfo.dialog.RateAppDialog;
import com.wt.apkinfo.fragment.ApplicationsFragment;
import com.wt.apkinfo.util.UserEngagement;

public class MainActivity extends AppCompatActivity implements InstallReferrerStateListener {

	private InstallReferrerClient mReferrerClient;
	private ApplicationsFragment mApplicationsFragment;

	@Override
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

		if (savedInstanceState == null) {
			UserEngagement.showRateDialog(this, () -> {
				if (!isFinishing()) {
					new RateAppDialog().show(getSupportFragmentManager(), "rate_app");
					UserEngagement.markRateDialogAsOpened(getApplicationContext());
				}
			});

			String ir = ((App)getApplication()).getUserInfo().getInstallReferrer();
			if (TextUtils.isEmpty(ir)) {
				mReferrerClient = InstallReferrerClient.newBuilder(this).build();
				try {
					mReferrerClient.startConnection(this);
				} catch (Exception e) {
					ERA.logException(e);
				}
			}
		}
	}

	@Override
	public void onInstallReferrerSetupFinished(int responseCode) {
		ERA.log("onInstallReferrerSetupFinished: responseCode=" + responseCode);
		if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
			try {
				ReferrerDetails response = mReferrerClient.getInstallReferrer();
				String ir = response.getInstallReferrer();
				Console.logi("InstallReferrer=" + ir);
				ERA.log("onInstallReferrerSetupFinished: InstallReferrer=" + ir);
				((App)getApplication()).getUserInfo().saveInstallReferrer(ir);
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
