package com.wt.apkinfo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserEngagement {

    private static final ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override public Thread newThread(@NonNull Runnable runnable) {
            Thread result = new Thread(runnable, "UserEngagementTask");
            result.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            return result;
        }
    });

    private static final String KEY_DETAILS_OPEN_COUNT = "details_open_count";
    private static final String KEY_RATE_APP_AUTO_OPEN = "rate_app_auto_open";

    public static void incUserRateConditionValue(@Nullable final Context ctx) {
        if (ctx != null) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences pref = getPrefs(ctx);
                    if (!pref.getBoolean(KEY_RATE_APP_AUTO_OPEN, false)) {
                        int count = pref.getInt(KEY_DETAILS_OPEN_COUNT, 0);
                        count++;
                        pref.edit().putInt(KEY_DETAILS_OPEN_COUNT, count).apply();
                        if (BuildConfig.DEBUG) {
                            Console.logi("UserEngagement.incUserRateConditionValue value=" + count);
                        }
                    }
                }
            });
        }
    }

    public static void markRateDialogAsOpened(@Nullable final Context ctx) {
        if (ctx != null) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    getPrefs(ctx).edit().putBoolean(KEY_RATE_APP_AUTO_OPEN, true).apply();
                }
            });
        }
    }

    public static void showRateDialog(@Nullable final Context ctx, @Nullable final Runnable onPositiveCondition) {
        if (ctx != null) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences pref = getPrefs(ctx);
                    if (!pref.getBoolean(KEY_RATE_APP_AUTO_OPEN, false)) {
                        int count = pref.getInt(KEY_DETAILS_OPEN_COUNT, 0);
                        if (count > 0 && count % 10 == 0 && onPositiveCondition != null) {
                            new Handler(Looper.getMainLooper()).post(onPositiveCondition);
                        }
                        if (BuildConfig.DEBUG) {
                            Console.logi("UserEngagement.showRateDialog: count=" + count);
                        }
                    } else {
                        if (BuildConfig.DEBUG) {
                            Console.logi("UserEngagement.showRateDialog: aouto_open=FLASE");
                        }
                    }
                }
            });
        }
    }

    private static SharedPreferences getPrefs(@NonNull Context ctx) {
        return ctx.getApplicationContext()
                .getSharedPreferences(BuildConfig.APPLICATION_ID + ".UserEngagement", Context.MODE_PRIVATE);
    }

}
