package com.wt.replaioad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class ReplaioAdView extends FrameLayout {

    public ReplaioAdView(Context context) {
        super(context);
        init(context);
    }

    public ReplaioAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReplaioAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReplaioAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {
        View childView = LayoutInflater.from(context).inflate(R.layout.reaplaio_ad_view, this, false);
        childView.findViewById(R.id.replaio_ad_installBtn).setOnClickListener(v -> {
            try {
                context.startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ReplaioAdConfig.REPLAIO_PACKAGE))
                );
            } catch (Exception e) {
                try {
                    context.startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + ReplaioAdConfig.REPLAIO_PACKAGE))
                    );
                } catch (Exception e2) {
                    // ignore
                }
            }
        });
        addView(childView);
    }

}
