package com.wt.apkinfo.entity;

import android.graphics.drawable.Drawable;

import com.wt.apkinfo.model.ApplicationDetailsModel;

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
	public ComponentInfo[] services;
	public ComponentInfo[] permissions;
	public ComponentInfo[] providers;
	public ComponentInfo[] receivers;
	public ComponentInfo[] metadata;
	public String[] signatures;
	public String versionName;
	public int versionCode;
	public long firstInstallTime;
	public long lastUpdateTime;
	public String apkFile;

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
