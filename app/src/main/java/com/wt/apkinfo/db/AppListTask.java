package com.wt.apkinfo.db;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.entity.ApplicationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by kenumir on 22.11.2017.
 *
 */

public class AppListTask extends AsyncTask<Context, Void, List<ApplicationEntity>> {

    public interface OnFinish {
        void onFinish(List<ApplicationEntity> res);
    }

    private String filter;
    private OnFinish mOnFinish;

    public AppListTask(String f, @Nullable OnFinish callback) {
        filter = f;
        mOnFinish = callback;
    }

    @Override
    protected List<ApplicationEntity> doInBackground(Context... params) {
        Context context = params[0].getApplicationContext();

        if (BuildConfig.DEBUG) {
            Console.logi("DatabaseCreator: start load items with filter=" + filter);
        }

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        //List<ApplicationInfo> pkgAppsList = pm.getInstalledApplications(0);//pm.queryIntentActivities(mainIntent, 0);
        List<ApplicationEntity> list = new ArrayList<>();

        if (pkgAppsList != null && pkgAppsList.size() > 0) {

            for(ResolveInfo ri : pkgAppsList) {
                ApplicationEntity e = new ApplicationEntity();
                e.id = ri.activityInfo.packageName;
                e.name = ri.activityInfo.loadLabel(pm).toString();

                if (filter != null && !e.name.toLowerCase().contains(filter.toLowerCase())) {
                    continue;
                }

                e.iconUri = "app://" + e.id;
                //e.installerPackage = pm.getInstallerPackageName(e.id);

                list.add(e);
            }

            Collections.sort(list);
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<ApplicationEntity> applicationEntities) {
        if (mOnFinish != null) {
            mOnFinish.onFinish(applicationEntities);
        }
    }
}
