package com.wt.apkinfo.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.hivedi.console.Console;
import com.wt.apkinfo.R;
import com.wt.apkinfo.viewmodel.files.FileItem;
import com.wt.apkinfo.viewmodel.files.FileListAdapter;
import com.wt.apkinfo.viewmodel.files.FileListModel;
import com.wt.apkinfo.viewmodel.files.FileListViewModelFactory;
import com.wt.apkinfo.viewmodel.files.OnFileItemClick;

import java.io.File;

public class ApkListActivity extends AppCompatActivity implements OnFileItemClick {

    private static final String KEY_APP_ID = "application_id";
    private static final String KEY_APP_NAME = "application_name";
    private static final String KEY_APP_BASE_APK = "application_base_apk";

    public static void start(@NonNull Context ctx, @NonNull String appId, @NonNull String appName, String baseApk) {
        Intent it = new Intent(ctx, ApkListActivity.class);
        it.putExtra(KEY_APP_ID, appId);
        it.putExtra(KEY_APP_NAME, appName);
        it.putExtra(KEY_APP_BASE_APK, baseApk);
        ctx.startActivity(it);
    }

    private String baseApk;
    private View overlayFrame;
    private RecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_list);

        String appId = getIntent().getStringExtra(KEY_APP_ID);
        String appName = getIntent().getStringExtra(KEY_APP_NAME);
        baseApk = getIntent().getStringExtra(KEY_APP_BASE_APK);

        FileListViewModelFactory factory = new FileListViewModelFactory(baseApk);
        FileListModel mFileListModel = ViewModelProviders.of(this, factory).get(FileListModel.class);

        overlayFrame = findViewById(R.id.overlayFrame);
        recycler = findViewById(R.id.recycler);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(appName);
        toolbar.setSubtitle(appId);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.setNavigationContentDescription(appId);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mFileListModel.getFileList().observe(this, files -> {
            overlayFrame.animate()
                    .alpha(0)
                    .setDuration(250)
                    .setInterpolator(new LinearInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) { }
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            overlayFrame.setAlpha(1);
                            overlayFrame.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationCancel(Animator animator) { }
                        @Override
                        public void onAnimationRepeat(Animator animator) { }
                    })
                    .start();
            recycler.setAdapter(new FileListAdapter(ApkListActivity.this, files, ApkListActivity.this));
        });
    }

    @Override
    public void onFileItemClick(FileItem item) {
        try {
            Console.logi("item: " + item);
            File apkFile = new File(item.fullName);
            Uri apkURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", apkFile);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/vnd.android.package-archive");
            share.putExtra(Intent.EXTRA_STREAM, apkURI);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, getResources().getString(R.string.app_details_share)));
            Console.logi("apkFile=" + apkFile);
        } catch (Exception e) {
            Console.loge("onFileItemClick: e=" + e, e);
            Crashlytics.logException(e);
        }
    }

}
