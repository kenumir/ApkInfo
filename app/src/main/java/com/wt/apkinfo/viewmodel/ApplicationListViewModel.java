package com.wt.apkinfo.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.Nullable;

import com.wt.apkinfo.db.DatabaseCreator;
import com.wt.apkinfo.entity.ApplicationEntity;

import java.util.List;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class ApplicationListViewModel extends AndroidViewModel {

	private final LiveData<List<ApplicationEntity>> mObservableProducts;
	private String filter = null;
	private DatabaseCreator databaseCreator;

	public ApplicationListViewModel(Application application) {
		super(application);
		databaseCreator = DatabaseCreator.getInstance();
		LiveData<Boolean> databaseCreated = databaseCreator.isDatabaseCreated();
		mObservableProducts = Transformations.switchMap(databaseCreated,
				new Function<Boolean, LiveData<List<ApplicationEntity>>>() {
					@Override
					public LiveData<List<ApplicationEntity>> apply(Boolean isDbCreated) {
						if (isDbCreated != null && isDbCreated.equals(Boolean.FALSE)) {
							return null;
						} else {
							return databaseCreator.getAllApplicationEntity();
						}
					}
				});
	}

	public ApplicationListViewModel search(String f) {
		filter = f;
		databaseCreator.filterResult(this.getApplication(), filter);
		return this;
	}

	public ApplicationListViewModel setup(String f) {
		filter = f;
		databaseCreator.createDb(this.getApplication(), filter);
		return this;
	}

	public LiveData<List<ApplicationEntity>> getApplications() {
		return mObservableProducts;
	}

}
