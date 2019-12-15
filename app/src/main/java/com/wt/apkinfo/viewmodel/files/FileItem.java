package com.wt.apkinfo.viewmodel.files;

import androidx.annotation.NonNull;

public class FileItem {
    public String name;
    public long size;
    public FileItem(String n, long s) {
        name = n;
        size = s;
    }

    @NonNull
    @Override
    public String toString() {
        return "{name=" + name + ", size=" + size + "}";
    }
}
