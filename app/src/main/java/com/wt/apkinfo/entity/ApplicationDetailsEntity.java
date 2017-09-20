package com.wt.apkinfo.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.wt.apkinfo.model.ApplicationDetailsModel;
import com.wt.apkinfo.model.ApplicationModel;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class ApplicationDetailsEntity implements ApplicationDetailsModel {

	public String id;
	public String name;
	public Drawable icon;
	public Drawable icon36dp;
	public ComponentInfo[] activities;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Drawable getIcon() {
		return icon;
	}

	@Override
	public Drawable getIcon36dp() {
		return icon36dp;
	}

}
