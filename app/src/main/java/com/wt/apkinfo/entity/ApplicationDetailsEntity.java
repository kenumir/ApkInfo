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
	//public Drawable icon;
	//public Drawable icon36dp;
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
	public Drawable getIcon() {
		return null; // icon;
	}

	@Override
	public Drawable getIcon36dp(Context ctx) {
		//if (icon != null && icon36dp == null) {
		//	int dp36 = (int) (ctx.getResources().getDisplayMetrics().density * 36f);
		//	Bitmap src = BitmapUtil.drawableToBitmap(icon);
		//	icon36dp = BitmapUtil.bitmapToDrawable(ctx, Bitmap.createScaledBitmap(src, dp36, dp36, true));
		//}
		//if (icon36dp == null) {
		//	PackageManager pm = ctx.getPackageManager();
		//	try {
		//		PackageInfo pi = pm.getPackageInfo(id, PackageManager.GET_ACTIVITIES);
		//		icon36dp = pi.applicationInfo.loadIcon(pm);
		//	} catch (Exception e) {
		//		// ignore
		//	}
		//}
		//return icon36dp;
		return null;
	}

}
