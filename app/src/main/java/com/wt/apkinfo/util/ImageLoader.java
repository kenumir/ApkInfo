package com.wt.apkinfo.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.hivedi.console.Console;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;
import com.squareup.picasso.Target;
import com.wt.apkinfo.BuildConfig;
import com.wt.apkinfo.R;

import java.io.IOException;

/**
 * Created by kenumir on 24.01.2018.
 *
 */

public class ImageLoader {

    private static volatile ImageLoader singleton;

    public static void init(Context ctx) {
        if (singleton == null) {
            synchronized (ImageLoader.class) {
                if (singleton == null) {
                    singleton = new ImageLoader(ctx);
                }
            }
        }
    }

    public static ImageLoader get() {
        return singleton;
    }

    private Picasso mPicasso;

    private ImageLoader(Context ctx) {
        final PackageManager pm = ctx.getPackageManager();
        mPicasso = new Picasso.Builder(ctx)
                .memoryCache(new LruCache(ctx))
                .addRequestHandler(new RequestHandler() {
                    @Override
                    public boolean canHandleRequest(Request data) {
                        String scheme = data.uri.getScheme();
                        return "app".equals(scheme);
                    }
                    @Override
                    public Result load(Request request, int networkPolicy) throws IOException {
                        try {
                            return new Result(
                                    BitmapUtil.drawableToBitmap(pm.getApplicationIcon(request.uri.getHost())),
                                    Picasso.LoadedFrom.DISK
                            );
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) {
                                Console.loge("ImageLoader.load: " + e, e);
                            }
                        }
                        return null;
                    }
                })
                .build();
    }

    public void load(String url, ImageView img) {
        mPicasso.cancelRequest(img);
        mPicasso.load(url)
                .fit()
                .centerInside()
                .into(img);
    }

    public void load(String url, Target img) {
        mPicasso.load(url)
                //.fit()
                .resizeDimen(R.dimen.app_icon_size, R.dimen.app_icon_size)
                .centerInside()
                .into(img);
    }
}
