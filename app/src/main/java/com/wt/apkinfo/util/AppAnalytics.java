package com.wt.apkinfo.util;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AppAnalytics {

    public static void userRate(@NonNull Context ctx, int rate) {
        Bundle b = new Bundle();
        b.putInt("value", rate);
        FirebaseAnalytics.getInstance(ctx).logEvent("user_rate_value", b);
    }

}
