package com.wt.apkinfo.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.Nullable;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;
import com.wt.apkinfo.entity.ApplicationEntity;

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
	private MutableLiveData<ApplicationDetailsEntity> dataApp = new MutableLiveData<>();
	private String filter = null;

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
		mIsDatabaseCreated.setValue(false);// Trigger an update to show a loading screen.
		new AppInfoTask(appIdx, new AppInfoTask.OnFinish() {
			@Override
			public void onFinish(ApplicationDetailsEntity res) {
				// Now on the main thread, notify observers that the db is created and ready.
				mIsDatabaseCreated.setValue(true);
				dataApp.setValue(res);
			}
		}).executeOnExecutor(exec, context.getApplicationContext());
	}

	public void filterResult(Context context, @Nullable String f) {
		filter = f;

		mIsDatabaseCreated.setValue(false);// Trigger an update to show a loading screen.

		new AppListTask(filter, new AppListTask.OnFinish() {
			@Override
			public void onFinish(List<ApplicationEntity> res) {
				// Now on the main thread, notify observers that the db is created and ready.
				mIsDatabaseCreated.setValue(true);
				data.setValue(res);
			}
		}).executeOnExecutor(exec, context.getApplicationContext());
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
