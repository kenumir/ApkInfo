package com.wt.apkinfo.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.RatingBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wt.apkinfo.R;
import com.wt.apkinfo.util.VariousUtil;

public class RateAppDialog extends DialogFragment {

    public RateAppDialog() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(R.string.rate_title)
                .positiveText(R.string.label_ok)
                .negativeText(R.string.label_close)
                .customView(R.layout.dialog_rate_app, false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (dialog.getCustomView() != null) {
                            RatingBar rb = dialog.getCustomView().findViewById(R.id.ratingBar);
                            float r = rb.getRating();
                            if (r == 5) {
                                VariousUtil.openInPlayStore(getActivity());
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), R.string.rate_toast_play_store, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), R.string.rate_toast, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                })
                .build();
    }
}