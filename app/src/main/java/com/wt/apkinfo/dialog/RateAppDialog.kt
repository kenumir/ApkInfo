package com.wt.apkinfo.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.wt.apkinfo.R
import com.wt.apkinfo.util.VariousUtil

class RateAppDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return MaterialDialog.Builder(activity!!)
        //        .title(R.string.rate_title)
        //        .customView(R.layout.dialog_rate_app, false)
        //        .positiveText(R.string.label_ok)
        //        .negativeText(R.string.label_close)
        //        .onPositive(MaterialDialog.SingleButtonCallback { dialogx, which ->  kotlin.run {
        //            val r = dialog.getCustomView().findViewById<RatingBar>(R.id.ratingBar).getRating()
        //            if (r == 5f) {
        //                VariousUtil.openInPlayStore(activity)
        //                if (activity != null) {
        //                    Toast.makeText(activity, R.string.rate_toast_play_store, Toast.LENGTH_SHORT).show()
        //                }
        //            } else {
        //                if (activity != null) {
        //                    Toast.makeText(activity, R.string.rate_toast, Toast.LENGTH_SHORT).show()
        //                }
        //            }
        //        }})
        //        .build();
        return MaterialDialog.Builder(activity!!)
                .title(R.string.rate_title)
                .positiveText(R.string.label_ok)
                .onPositive { dialog, _ ->  kotlin.run {
                    val r = dialog.getCustomView()?.findViewById<RatingBar>(R.id.ratingBar)?.getRating()
                    if (r == 5f) {
                        VariousUtil.openInPlayStore(activity)
                        if (activity != null) {
                            Toast.makeText(activity, R.string.rate_toast_play_store, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (activity != null) {
                            Toast.makeText(activity, R.string.rate_toast, Toast.LENGTH_SHORT).show()
                        }
                    }
                }}
                .negativeText(R.string.label_close)
                .customView(
                        R.layout.dialog_rate_app,
                        false
                ).build()
    }
}
