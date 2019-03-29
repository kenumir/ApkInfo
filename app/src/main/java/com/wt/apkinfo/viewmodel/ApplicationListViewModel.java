package com.wt.apkinfo.viewmodel;

import android.app.Application;

import com.wt.apkinfo.db.DatabaseCreator;
import com.wt.apkinfo.entity.ApplicationEntity;

import java.util.List;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class ApplicationListViewModel extends AndroidViewModel {

	private final LiveData<List<ApplicationEntity>> mObservableProducts;
	private DatabaseCreator databaseCreator;

	public ApplicationListViewModel(Application application) {
		super(application);
		databaseCreator = DatabaseCreator.getInstance();
		mObservableProducts = Transformations.switchMap(databaseCreator.isDatabaseCreated(),
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
		databaseCreator.filterResult(this.getApplication(), f);
		return this;
	}

	public ApplicationListViewModel setup(String f) {
		databaseCreator.createDb(this.getApplication(), f);
		return this;
	}

	public LiveData<List<ApplicationEntity>> getApplications() {
		return mObservableProducts;
	}

	public String getFilter() {
		return databaseCreator.getFilter();
	}

}
