package com.wt.replaioad;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ReplaioAdConfig {

    public final static String REPLAIO_PACKAGE = "com.hv.replaio";

    private final ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override public Thread newThread(@NonNull Runnable runnable) {
            Thread result = new Thread(runnable, "Replaio Ad Config");
            result.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            return result;
        }
    });
    private Context mContext;
    private Boolean isReplaioInstalled = null;
    private ReplaioAdView mReplaioAdView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            configure(mReplaioAdView);
        }
    };

    public ReplaioAdConfig(@NonNull Context ctx) {
        mContext = ctx.getApplicationContext();
        setup();
    }

    private void setup() {
        exec.execute(() -> {
            try {
                PackageInfo info = mContext.getPackageManager().getPackageInfo(REPLAIO_PACKAGE, 0);
                isReplaioInstalled = info != null;
            } catch (Exception e) {
                e.printStackTrace();
                isReplaioInstalled = false;
            }
            mHandler.post(mRunnable);
        });
    }

    public void configure(@Nullable ReplaioAdView view) {
        mReplaioAdView = view;
        if (isReplaioInstalled != null && mReplaioAdView != null) {
            mReplaioAdView.setVisibility(!isReplaioInstalled ? View.VISIBLE : View.GONE);
        }
    }

    public void refreshSettings() {
        setup();
    }

}
