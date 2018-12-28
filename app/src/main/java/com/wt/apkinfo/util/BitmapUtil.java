package com.wt.apkinfo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by kenumir on 20.09.2017.
 *
 */

public class BitmapUtil {

	// src: https://stackoverflow.com/a/10600736/959086
	public static Bitmap drawableToBitmap (Drawable drawable) {
		Bitmap bitmap = null;

		// on Xiaomi devices generate exception: IllegalStateException: Can't parcel a recycled bitmap
		// at miui.security.ISecurityManager$Stub$Proxy.saveIcon(ISecurityManager.java:1736)
		// at miui.security.SecurityManager.saveIcon(SecurityManager.java:311)
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if(bitmapDrawable.getBitmap() != null) {
				return bitmapDrawable.getBitmap();
			}
		}

		if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
		} else {
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static Drawable bitmapToDrawable(Context ctx, Bitmap bitmap) {
		return new BitmapDrawable(ctx.getResources(), bitmap);
	}

}
