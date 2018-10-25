package com.wt.apkinfo.entity;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.wt.apkinfo.model.ApplicationModel;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class ApplicationEntity implements ApplicationModel, Comparable<ApplicationEntity> {

	public String id;
	public String name;
	public String iconUri;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getIconUri() {
		return iconUri;
	}

	@Override
	public int compareTo(@NonNull ApplicationEntity applicationEntity) {
		return name.compareToIgnoreCase(applicationEntity.name);
	}
}
