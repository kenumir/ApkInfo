package com.wt.apkinfo.activity

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Build
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


@Suppress("UNUSED_PARAMETER", "DEPRECATION")
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

        ratingBar = findViewById(R.id.ratingBar)

        val stars = ratingBar.progressDrawable as LayerDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            stars.getDrawable(2).colorFilter = BlendModeColorFilter(ContextCompat.getColor(this, R.color.rate_selected), BlendMode.SRC_ATOP)
            stars.getDrawable(1).colorFilter = BlendModeColorFilter(ContextCompat.getColor(this, R.color.rate_selected), BlendMode.SRC_ATOP)
            stars.getDrawable(0).colorFilter = BlendModeColorFilter(ContextCompat.getColor(this, R.color.rate_normal), BlendMode.SRC_ATOP)
        } else {
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.rate_selected), PorterDuff.Mode.SRC_ATOP)
            stars.getDrawable(1).setColorFilter(ContextCompat.getColor(this, R.color.rate_selected), PorterDuff.Mode.SRC_ATOP)
            stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.rate_normal), PorterDuff.Mode.SRC_ATOP)
        }
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
