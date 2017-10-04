package com.wt.apkinfo.util;

import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by kenumir on 04.10.2017.
 *
 */

public class ViewUtil {

	@Nullable
	public static View findViewWithContentDescription(View src, String contentDescription) {
		ArrayList<View> potentialViews = new ArrayList<View>();
		src.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		View res = null;
		if (potentialViews.size() > 0){
			res = potentialViews.get(0);
		}
		return res;
	}

}
