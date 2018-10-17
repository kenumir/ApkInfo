package com.wt.apkinfo.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;

import com.hivedi.console.Console;
import com.hivedi.era.ERA;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.entity.ApplicationDetailsEntity;
import com.wt.apkinfo.entity.ComponentInfo;
import com.wt.apkinfo.util.BitmapUtil;

import java.security.MessageDigest;
import java.util.Locale;
import java.util.Set;

/**
 * Created by kenumir on 22.11.2017.
 *
 */

public class AppInfoTask extends AsyncTask<Context, Void, ApplicationDetailsEntity> {

    public interface OnFinish {
        void onFinish(ApplicationDetailsEntity res);
    }

    private String appId;
    private OnFinish mOnFinish;

    AppInfoTask(String aid, @Nullable OnFinish callback) {
        appId = aid;
        mOnFinish = callback;
    }

    @SuppressLint("PackageManagerGetSignatures")
    @Override
    protected ApplicationDetailsEntity doInBackground(Context... contexts) {
        Context context = contexts[0].getApplicationContext();
        ApplicationDetailsEntity result = new ApplicationDetailsEntity();
        if (BuildConfig.DEBUG) {
            Console.logi("DatabaseCreator: start load item with appId=" + appId);
        }

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(appId, PackageManager.GET_ACTIVITIES);
					/*
					|
						PackageManager.GET_CONFIGURATIONS |
						PackageManager.GET_INTENT_FILTERS |
						PackageManager.GET_PERMISSIONS |
						PackageManager.GET_META_DATA |
						PackageManager.GET_PROVIDERS |
						PackageManager.GET_RECEIVERS |
						PackageManager.GET_SERVICES |
						PackageManager.GET_SIGNATURES |
						PackageManager.GET_URI_PERMISSION_PATTERNS |
						PackageManager.GET_INSTRUMENTATION |
						PackageManager.GET_SHARED_LIBRARY_FILES
					 */
            result.id = appId;
            result.name = pi.applicationInfo.loadLabel(pm).toString();
            //result.icon = pi.applicationInfo.loadIcon(pm);

            //Bitmap src = BitmapUtil.drawableToBitmap(result.icon);
            //int dp36 = (int) (context.getResources().getDisplayMetrics().density * 36f);
            //result.icon36dp = result.icon;
            //result.icon36dp = BitmapUtil.bitmapToDrawable(context, Bitmap.createScaledBitmap(src, dp36, dp36, true));

            int counter;

            result.versionName = pi.versionName;
            result.versionCode = pi.versionCode;
            result.firstInstallTime = pi.firstInstallTime;
            result.lastUpdateTime = pi.lastUpdateTime;
            result.apkFile = pi.applicationInfo.publicSourceDir;
            result.targetSdkVersion = pi.applicationInfo.targetSdkVersion;
            result.minSdkVersion = Build.VERSION.SDK_INT >= 24 ? pi.applicationInfo.minSdkVersion : 0;

            if (pi.activities != null) {
                counter = 0;
                result.activities = new ComponentInfo[pi.activities.length];
                for(ActivityInfo ai : pi.activities) {
                    ComponentInfo ci  = new ComponentInfo();
                    ci.className = ai.name;
                    ci.name = ai.loadLabel(pm).toString();
                    result.activities[counter] = ci;
                    counter++;
                }
            }

            pi = pm.getPackageInfo(appId, PackageManager.GET_SIGNATURES);
            if (pi.signatures != null) {
                counter = 0;
                MessageDigest md = MessageDigest.getInstance("SHA");
                result.signatures = new String[pi.signatures.length];
                for(Signature ai : pi.signatures) {
                    md.update(ai.toByteArray());
                    StringBuilder s = new StringBuilder();
                    for(byte b : md.digest()) {
                        s.append(":").append(String.format("%02x", b));
                    }
                    result.signatures[counter] = s.substring(1).toUpperCase(Locale.US);
                    counter++;
                }
            }

            pi = pm.getPackageInfo(appId, PackageManager.GET_SERVICES);
            if (pi.services != null) {
                counter = 0;
                result.services = new ComponentInfo[pi.services.length];
                for(ServiceInfo ai : pi.services) {
                    ComponentInfo ci  = new ComponentInfo();
                    ci.className = ai.name;
                    ci.name = ai.loadLabel(pm).toString();
                    result.services[counter] = ci;
                    counter++;
                }
            }

            pi = pm.getPackageInfo(appId, PackageManager.GET_PERMISSIONS | PackageManager.GET_URI_PERMISSION_PATTERNS);
            if (pi.permissions != null) {
                counter = 0;
                int all = pi.permissions.length;
                if (pi.requestedPermissions != null) {
                    all += pi.requestedPermissions.length;
                }
                result.permissions = new ComponentInfo[all];
                if (pi.requestedPermissions != null) {
                    for(String p : pi.requestedPermissions) {
                        ComponentInfo ci  = new ComponentInfo();
                        ci.className = p;
                        ci.name = p;
                        result.permissions[counter] = ci;
                        counter++;
                    }
                }
                for(PermissionInfo ai : pi.permissions) {
                    ComponentInfo ci  = new ComponentInfo();
                    ci.className = ai.name;
                    ci.name = ai.loadLabel(pm).toString();
                    result.permissions[counter] = ci;
                    counter++;
                }
            }

            pi = pm.getPackageInfo(appId, PackageManager.GET_PROVIDERS);
            if (pi.providers != null) {
                counter = 0;
                result.providers = new ComponentInfo[pi.providers.length];
                for(ProviderInfo ai : pi.providers) {
                    ComponentInfo ci  = new ComponentInfo();
                    ci.className = ai.name;
                    ci.name = ai.loadLabel(pm).toString();
                    result.providers[counter] = ci;
                    counter++;
                }
            }

            pi = pm.getPackageInfo(appId, PackageManager.GET_RECEIVERS);
            if (pi.receivers != null) {
                counter = 0;
                result.receivers = new ComponentInfo[pi.receivers.length];
                for(ActivityInfo ai : pi.receivers) {
                    ComponentInfo ci  = new ComponentInfo();
                    ci.className = ai.name;
                    ci.name = ai.loadLabel(pm).toString();
                    result.receivers[counter] = ci;
                    counter++;
                }
            }

            pi = pm.getPackageInfo(appId, PackageManager.GET_META_DATA);
            if (pi.applicationInfo.metaData != null) {
                counter = 0;
                Set<String> keys =  pi.applicationInfo.metaData.keySet();
                result.metadata = new ComponentInfo[keys.size()];
                for (String key : keys) {
                    Object o = pi.applicationInfo.metaData.get(key);
                    if (o != null) {
                        ComponentInfo ci = new ComponentInfo();
                        ci.className = o.toString();
                        ci.name = key;
                        result.metadata[counter] = ci;
                        counter++;
                    }
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Console.loge("fetchAppInfo: " + e, e);
            }
            ERA.logException(e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(ApplicationDetailsEntity applicationDetailsEntity) {
        if (mOnFinish != null) {
            mOnFinish.onFinish(applicationDetailsEntity);
        }
    }
}
