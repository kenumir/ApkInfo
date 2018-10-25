package com.wt.apkinfo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.wt.apkinfo.BuildConfig;

public class VariousUtil {

    public static void openInPlayStore(@Nullable Context ctx) {
        if (ctx != null) {
            try {
                ctx.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID))
                );
            } catch (Exception e) {
                try {
                    ctx.startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))
                    );
                } catch (Exception e2) {
                    // ignore
                }
            }
        }
    }

}
