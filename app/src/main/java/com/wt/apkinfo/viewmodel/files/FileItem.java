package com.wt.apkinfo.viewmodel.files;

import androidx.annotation.NonNull;

public class FileItem {
    public String name, fullName;
    public long size;
    public FileItem(String n, long s, String fn) {
        name = n;
        size = s;
        fullName = fn;
    }

    @NonNull
    @Override
    public String toString() {
        return "{name=" + name + ", size=" + size + ", fullName=" + fullName + "}";
    }
}
