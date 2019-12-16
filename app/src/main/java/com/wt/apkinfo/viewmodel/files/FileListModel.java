package com.wt.apkinfo.viewmodel.files;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileListModel extends ViewModel {

    private MutableLiveData<List<FileItem>> fileList;
    private String baseApk;
    private final ExecutorService exec = Executors.newCachedThreadPool(runnable -> {
        Thread result = new Thread(runnable, "FileListModel Task");
        result.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        return result;
    });

    public FileListModel(String dir) {
        this.baseApk = dir;
    }

    public MutableLiveData<List<FileItem>> getFileList() {
        if (fileList == null) {
            fileList = new MutableLiveData<>();
            exec.execute(() -> {
                File baseApkFile = new File(baseApk);
                List<FileItem> l = new ArrayList<>();
                if (baseApkFile.getParent() != null) {
                    File[] fl = new File(baseApkFile.getParent()).listFiles((file, s) -> s.endsWith(".apk"));
                    if (fl != null) {
                        for (File f : fl) {
                            l.add(new FileItem(f.getName(), f.length(), f.getAbsolutePath()));
                        }
                    } else {
                        // older android versions has all apk files in /data/app directory
                        l.add(new FileItem(baseApkFile.getName(), baseApkFile.length(), baseApkFile.getAbsolutePath()));
                    }
                } else {
                    l.add(new FileItem(baseApkFile.getName(), baseApkFile.length(), baseApkFile.getAbsolutePath()));
                }
                fileList.postValue(l);
            });
        }
        return fileList;
    }

}
