package com.wt.apkinfo.viewmodel.files;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.wt.apkinfo.R;

import java.text.DecimalFormat;

public class FileListHolder extends RecyclerView.ViewHolder {
    private TextView txt1, txt2;
    private FileItem mFileItem;
    private OnFileItemClick mOnFileItemClick;
    public FileListHolder(@NonNull View itemView) {
        super(itemView);
        txt1 = itemView.findViewById(R.id.name);
        txt2 = itemView.findViewById(R.id.size);
        itemView.setOnClickListener(view -> {
            if (mOnFileItemClick != null) {
                mOnFileItemClick.onFileItemClick(mFileItem);
            }
        });
    }
    public void update(@NonNull FileItem f, @Nullable OnFileItemClick click) {
        mFileItem = f;
        mOnFileItemClick = click;
        txt1.setText(f.name);
        txt2.setText(formatFileSize(f.size));
    }
    public String formatFileSize(long size) {
        if(size <= 0) return "";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}