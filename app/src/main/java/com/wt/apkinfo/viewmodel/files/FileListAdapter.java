package com.wt.apkinfo.viewmodel.files;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wt.apkinfo.R;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListHolder>{

    private List<FileItem> items;
    private OnFileItemClick mOnFileItemClick;
    private Context mContext;

    public FileListAdapter(Context ctx, List<FileItem> i, OnFileItemClick click) {
        items = i;
        mOnFileItemClick = click;
        mContext = ctx;
    }

    @NonNull
    @Override
    public FileListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_apk_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileListHolder holder, int position) {
        if (items.size() == 0) {
            holder.update(new FileItem(mContext.getResources().getString(R.string.app_no_apk_files), -1, null), null);
        } else {
            holder.update(items.get(position), mOnFileItemClick);
        }

    }

    @Override
    public int getItemCount() {
        int size = items.size();
        if (size == 0) {
            size = 1; // no data row
        }
        return size;
    }
}