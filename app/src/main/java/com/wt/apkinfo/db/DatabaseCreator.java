package com.wt.apkinfo.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;
import com.wt.apkinfo.entity.ApplicationEntity;
import com.wt.apkinfo.entity.ComponentInfo;
import com.wt.apkinfo.util.BitmapUtil;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class DatabaseCreator {

	private static DatabaseCreator sInstance;
	private static final Object LOCK = new Object();

	public synchronized static DatabaseCreator getInstance() {
		if (sInstance == null) {
			synchronized (LOCK) {
				if (sInstance == null) {
					sInstance = new DatabaseCreator();
				}
			}
		}
		return sInstance;
	}

	private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();
	private final AtomicBoolean mInitializing = new AtomicBoolean(true);
	private final ExecutorService exec = Executors.newSingleThreadExecutor();

	private MutableLiveData<List<ApplicationEntity>> data = new MutableLiveData<>();
	private MutableLiveData<ApplicationDetailsEntity> dataApp = new MutableLiveData<>();
	private String filter = null;
	private String appId = null;

	public LiveData<Boolean> isDatabaseCreated() {
		return mIsDatabaseCreated;
	}

	public LiveData<List<ApplicationEntity>> getAllApplicationEntity() {
		return data;
	}

	public LiveData<ApplicationDetailsEntity> getApplicationDetailsEntity() {
		return dataApp;
	}

	public String getFilter() {
		return filter;
	}

	public void fetchAppInfo(Context context, @Nullable String appIdx) {
		this.appId = appIdx;

		mIsDatabaseCreated.setValue(false);// Trigger an update to show a loading screen.
		new AsyncTask<Context, Void, ApplicationDetailsEntity>() {

			@Override
			protected ApplicationDetailsEntity doInBackground(Context... params) {
				Context context = params[0].getApplicationContext();
				ApplicationDetailsEntity result = new ApplicationDetailsEntity();
				if (BuildConfig.DEBUG) {
					Console.logi("DatabaseCreator: start load item with appId=" + appId);
				}

				PackageManager pm = context.getPackageManager();
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
					result.id = appId;
					result.name = pi.applicationInfo.loadLabel(pm).toString();
					result.icon = pi.applicationInfo.loadIcon(pm);

					Bitmap src = BitmapUtil.drawableToBitmap(result.icon);
					int dp36 = (int) (context.getResources().getDisplayMetrics().density * 36f);
					result.icon36dp = BitmapUtil.bitmapToDrawable(context, Bitmap.createScaledBitmap(src, dp36, dp36, true));

					int counter;

					result.versionName = pi.versionName;
					result.versionCode = pi.versionCode;
					result.firstInstallTime = pi.firstInstallTime;
					result.lastUpdateTime = pi.lastUpdateTime;
					result.apkFile = pi.applicationInfo.publicSourceDir;

					if (pi.signatures != null) {
						counter = 0;
						MessageDigest md = MessageDigest.getInstance("SHA");
						result.signatures = new String[pi.signatures.length];
						for(Signature ai : pi.signatures) {
							md.update(ai.toByteArray());
							String s = "";
							for(byte b : md.digest()) {
								s += ":" + String.format("%02x", b);
							}
							result.signatures[counter] = s.substring(1).toUpperCase(Locale.US);
							counter++;
						}
					}


					if (pi.activities != null) {
						counter = 0;
						result.activities = new ComponentInfo[pi.activities.length];
						for(ActivityInfo ai : pi.activities) {
							ComponentInfo ci  = new ComponentInfo();
							ci.className = ai.name;
							ci.name = ai.loadLabel(pm).toString();
							result.activities[counter] = ci;
							counter++;
						}
					}

					if (pi.services != null) {
						counter = 0;
						result.services = new ComponentInfo[pi.services.length];
						for(ServiceInfo ai : pi.services) {
							ComponentInfo ci  = new ComponentInfo();
							ci.className = ai.name;
							ci.name = ai.loadLabel(pm).toString();
							result.services[counter] = ci;
							counter++;
						}
					}

					if (pi.permissions != null) {
						counter = 0;
						int all = pi.permissions.length;
						if (pi.requestedPermissions != null) {
							all += pi.requestedPermissions.length;
						}
						result.permissions = new ComponentInfo[all];
						if (pi.requestedPermissions != null) {
							for(String p : pi.requestedPermissions) {
								ComponentInfo ci  = new ComponentInfo();
								ci.className = p;
								ci.name = p;
								result.permissions[counter] = ci;
								counter++;
							}
						}
						for(PermissionInfo ai : pi.permissions) {
							ComponentInfo ci  = new ComponentInfo();
							ci.className = ai.name;
							ci.name = ai.loadLabel(pm).toString();
							result.permissions[counter] = ci;
							counter++;
						}
					}

					if (pi.providers != null) {
						counter = 0;
						result.providers = new ComponentInfo[pi.providers.length];
						for(ProviderInfo ai : pi.providers) {
							ComponentInfo ci  = new ComponentInfo();
							ci.className = ai.name;
							ci.name = ai.loadLabel(pm).toString();
							result.providers[counter] = ci;
							counter++;
						}
					}

					if (pi.receivers != null) {
						counter = 0;
						result.receivers = new ComponentInfo[pi.receivers.length];
						for(ActivityInfo ai : pi.receivers) {
							ComponentInfo ci  = new ComponentInfo();
							ci.className = ai.name;
							ci.name = ai.loadLabel(pm).toString();
							result.receivers[counter] = ci;
							counter++;
						}
					}

					if (pi.applicationInfo.metaData != null) {
						counter = 0;
						Set<String> keys =  pi.applicationInfo.metaData.keySet();
						result.metadata = new ComponentInfo[keys.size()];
						for (String key : keys) {
							Object o = pi.applicationInfo.metaData.get(key);
							if (o != null) {
								ComponentInfo ci = new ComponentInfo();
								ci.className = o.toString();
								ci.name = key;
								result.metadata[counter] = ci;
								counter++;
							}
						}
					}

					//src.recycle();

					//Console.logi("APK: " + pi.applicationInfo.publicSourceDir);
					//Console.logi("metaData: " + pi.applicationInfo.metaData);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return result;
			}

			@Override
			protected void onPostExecute(ApplicationDetailsEntity vv) {
				// Now on the main thread, notify observers that the db is created and ready.
				mIsDatabaseCreated.setValue(true);
				dataApp.setValue(vv);
			}
		}.executeOnExecutor(exec, context.getApplicationContext());
	}

	public void filterResult(Context context, @Nullable String f) {
		filter = f;

		mIsDatabaseCreated.setValue(false);// Trigger an update to show a loading screen.
		new AsyncTask<Context, Void, List<ApplicationEntity>>() {

			@Override
			protected List<ApplicationEntity> doInBackground(Context... params) {
				Context context = params[0].getApplicationContext();

				if (BuildConfig.DEBUG) {
					Console.logi("DatabaseCreator: start load items with filter=" + filter);
				}

				Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
				mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				PackageManager pm = context.getPackageManager();
				List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
				List<ApplicationEntity> list = new ArrayList<>();

				if (pkgAppsList != null && pkgAppsList.size() > 0) {

					for(ResolveInfo ri : pkgAppsList) {
						ApplicationEntity e = new ApplicationEntity();
						e.id = ri.activityInfo.packageName;
						e.name = ri.activityInfo.loadLabel(pm).toString();

						if (filter != null && !e.name.toLowerCase().contains(filter.toLowerCase())) {
							continue;
						}

						//Console.logi("SRC=" + ri.activityInfo.applicationInfo.publicSourceDir);

						if (ri.activityInfo.icon == 0) {
							e.icon = ri.activityInfo.applicationInfo.loadIcon(pm);
						} else {
							e.icon = ri.activityInfo.loadIcon(pm);
						}
						list.add(e);
					}

					Collections.sort(list);

					//try {
					//	Thread.sleep(1_000);
					//} catch (InterruptedException e) {
					//	return null;
					//}
				}

				return list;
			}

			@Override
			protected void onPostExecute(List<ApplicationEntity> vv) {
				// Now on the main thread, notify observers that the db is created and ready.
				mIsDatabaseCreated.setValue(true);
				data.setValue(vv);
			}
		}.executeOnExecutor(exec, context.getApplicationContext());
	}

	public void createDb(Context context, @Nullable String f) {
		if (!mInitializing.compareAndSet(true, false)) {
			if (BuildConfig.DEBUG) {
				Console.logi("Already initializing, filter=" + filter);
			}
			return; // Already initializing
		}

		filterResult(context, f);
	}

}
