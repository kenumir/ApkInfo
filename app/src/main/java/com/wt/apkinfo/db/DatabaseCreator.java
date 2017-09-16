package com.wt.apkinfo.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.entity.ApplicationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	private String filter = null;

	public LiveData<Boolean> isDatabaseCreated() {
		return mIsDatabaseCreated;
	}

	public LiveData<List<ApplicationEntity>> getAllApplicationEntity() {
		return data;
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

				if (pkgAppsList != null && pkgAppsList.size() > 0) {
					List<ApplicationEntity> list = new ArrayList<>();

					for(ResolveInfo ri : pkgAppsList) {
						ApplicationEntity e = new ApplicationEntity();
						e.id = ri.activityInfo.packageName;
						e.name = ri.activityInfo.loadLabel(pm).toString();

						if (filter != null && !e.name.toLowerCase().contains(filter.toLowerCase())) {
							continue;
						}

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

					return list;
				}

				return null;
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
