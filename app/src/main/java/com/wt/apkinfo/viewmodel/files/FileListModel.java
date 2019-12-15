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
    private String dir;
    private final ExecutorService exec = Executors.newCachedThreadPool(runnable -> {
        Thread result = new Thread(runnable, "FileListModel Task");
        result.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        return result;
    });

    public FileListModel(String dir) {
        this.dir = dir;
    }

    public MutableLiveData<List<FileItem>> getFileList() {
        if (fileList == null) {
            fileList = new MutableLiveData<>();
            exec.execute(() -> {
                List<FileItem> l = new ArrayList<>();
                File[] fl = new File(dir).listFiles((file, s) -> s.endsWith(".apk"));
                if (fl != null) {
                    for (File f : fl) {
                        l.add(new FileItem(f.getName(), f.length()));
                    }
                }
                fileList.postValue(l);
            });
        }
        return fileList;
    }

}
