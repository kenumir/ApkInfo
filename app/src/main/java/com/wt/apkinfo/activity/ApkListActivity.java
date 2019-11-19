package com.wt.apkinfo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ApkListActivity extends AppCompatActivity {

    private static final String KEY_APP_ID = "application_id";
    private static final String KEY_APP_NAME = "application_name";

    public static void start(@NonNull Context ctx, @NonNull String appId, @NonNull String appName) {
        Intent it = new Intent(ctx, ApkListActivity.class);
        it.putExtra(KEY_APP_ID, appId);
        it.putExtra(KEY_APP_NAME, appName);
        ctx.startActivity(it);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
