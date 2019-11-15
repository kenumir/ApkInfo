package com.wt.apkinfo.activity

import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.wt.apkinfo.R
import com.wt.apkinfo.util.AppAnalytics
import com.wt.apkinfo.util.UserEngagement
import com.wt.apkinfo.util.VariousUtil


class RateAppActivity : AppCompatActivity() {

    lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_app)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        ratingBar = findViewById<RatingBar>(R.id.ratingBar)

        val stars = ratingBar.progressDrawable as LayerDrawable
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.rate_selected), PorterDuff.Mode.SRC_ATOP)
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.rate_selected), PorterDuff.Mode.SRC_ATOP)
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.rate_normal), PorterDuff.Mode.SRC_ATOP)
    }

    fun handleRate(v: View) {
        if (ratingBar.rating == 5f) {
            VariousUtil.openInPlayStore(this)
            Toast.makeText(this, R.string.rate_toast_play_store, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.rate_toast, Toast.LENGTH_SHORT).show()
        }
        AppAnalytics.userRate(this, ratingBar.rating.toInt())
        UserEngagement.markRateDialogAsOpened(this)
        finish()
    }

    fun handleClose(v: View) {
        UserEngagement.markRateDialogAsOpened(this)
        finish()
    }
}
