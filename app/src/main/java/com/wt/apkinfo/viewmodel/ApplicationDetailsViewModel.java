package com.wt.apkinfo.viewmodel;

import android.app.Application;

import com.wt.apkinfo.db.DatabaseCreator;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class ApplicationDetailsViewModel extends AndroidViewModel {

	private final LiveData<ApplicationDetailsEntity> mObservableApp;
	private DatabaseCreator databaseCreator;

	private final String applicationId;
	public ApplicationDetailsEntity applicationInfo = new ApplicationDetailsEntity();

	public ApplicationDetailsViewModel(Application application, final String appId) {
		super(application);
		applicationId = appId;

		databaseCreator = DatabaseCreator.getInstance();
		mObservableApp = Transformations.switchMap(databaseCreator.isDatabaseCreated(),
				new Function<Boolean, LiveData<ApplicationDetailsEntity>>() {
					@Override
					public LiveData<ApplicationDetailsEntity> apply(Boolean isDbCreated) {
						if (isDbCreated != null && isDbCreated.equals(Boolean.FALSE)) {
							return null;
						} else {
							return databaseCreator.getApplicationDetailsEntity();
						}
					}
				});
		databaseCreator.fetchAppInfo(this.getApplication(), applicationId);
	}

	public void setProduct(ApplicationDetailsEntity p) {
		this.applicationInfo = p;
	}

	public LiveData<ApplicationDetailsEntity> getApplicationDetails() {
		return mObservableApp;
	}

	public static class Factory extends ViewModelProvider.NewInstanceFactory {

		@NonNull
		private final Application mApplication;

		private final String applicationId;

		public Factory(@NonNull Application application, String appId) {
			mApplication = application;
			applicationId = appId;
		}

		@NonNull
		@Override
		public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
			//noinspection unchecked
			return (T) new ApplicationDetailsViewModel(mApplication, applicationId);
		}
	}

}
