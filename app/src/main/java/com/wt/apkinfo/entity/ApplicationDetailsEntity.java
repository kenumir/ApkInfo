package com.wt.apkinfo.entity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.wt.apkinfo.model.ApplicationDetailsModel;
import com.wt.apkinfo.util.BitmapUtil;

/**
 * Created by kenumir on 15.09.2017.
 *
 */

public class ApplicationDetailsEntity implements ApplicationDetailsModel {

	public String id;
	public String name;
	public String dataDir;
	public String nativeLibraryDir;
	public ComponentInfo[] activities;
	public ComponentInfo[] services;
	public ComponentInfo[] permissions;
	public ComponentInfo[] providers;
	public ComponentInfo[] receivers;
	public ComponentInfo[] metadata;
	public String[] signatures;
	public String versionName;
	public String installerPackage;
	public int versionCode;
	public long firstInstallTime;
	public long lastUpdateTime;
	public String apkFile;
	public int targetSdkVersion;
	public int minSdkVersion;

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
		return "app://" + id;
	}


}
