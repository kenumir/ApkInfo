package com.wt.apkinfo.db;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.hivedi.console.Console;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.entity.ApplicationEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        List<ApplicationEntity> list = new ArrayList<>();

        if (pkgAppsList != null && pkgAppsList.size() > 0) {

            for(ResolveInfo ri : pkgAppsList) {
                ApplicationEntity e = new ApplicationEntity();
                e.id = ri.activityInfo.packageName;
                e.name = ri.activityInfo.loadLabel(pm).toString();

                if (filter != null && !e.name.toLowerCase().contains(filter.toLowerCase())) {
                    continue;
                }

                e.iconUri = "app://" + ri.activityInfo.packageName;
                //Console.logi("SRC=" + ri.activityInfo.applicationInfo.publicSourceDir);
                //if (ri.activityInfo.icon == 0) {
                //    e.iconUri = ri.activityInfo.packageName + "/" + ri.activityInfo.applicationInfo.;
                //} else {
                //    e.iconUri = ri.activityInfo.packageName + "/" + ri.activityInfo.targetActivity;
                //}

                //if (ri.activityInfo.icon == 0) {
                    //e.icon = ri.activityInfo.applicationInfo.loadIcon(pm);
                //} else {
                //    e.icon = ri.activityInfo.loadIcon(pm);
                //}
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
