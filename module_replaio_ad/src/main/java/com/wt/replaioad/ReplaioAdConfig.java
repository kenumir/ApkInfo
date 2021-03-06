package com.wt.replaioad;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ReplaioAdConfig {

    public final static String REPLAIO_PACKAGE = "com.hv.replaio";
    public final static String REFERRER = "&referrer=utm_source%3Dkenumir%26utm_medium%3Dapkinfo";

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
            if (mReplaioAdView != null) {
                mReplaioAdView.setOnInstallButtonClick(mOnInstallButtonClick);
                mReplaioAdView.setOnInflateError(mOnInflateError);
            }
        }
    };
    private OnInstallButtonClick mOnInstallButtonClick;
    private OnInflateError mOnInflateError;

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

    public void onActivityStart(@Nullable OnInstallButtonClick ocl, @Nullable OnInflateError inflate) {
        mOnInstallButtonClick = ocl;
        mOnInflateError = inflate;
        mReplaioAdView.setOnInstallButtonClick(mOnInstallButtonClick);
        mReplaioAdView.setOnInflateError(mOnInflateError);
    }

    public void onActivityStop() {
        mOnInstallButtonClick = null;
        mOnInflateError = null;
    }

}
