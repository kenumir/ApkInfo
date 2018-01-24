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
import com.wt.apkinfo.BuildConfig;

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
                        return data.uri.getScheme().equals("app");
                    }
                    @Override
                    public Result load(Request request, int networkPolicy) throws IOException {
                        try {
                            return new Result(
                                    drawableToBitmap(pm.getApplicationIcon(request.uri.getHost())),
                                    Picasso.LoadedFrom.DISK
                            );
                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) {
                                Console.loge("" + e, e);
                            }
                        }
                        return null;
                    }
                })
                .build();
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void load(String url, ImageView img) {
        mPicasso.load(url)
                .fit()
                .centerInside()
                .into(img);
    }
}
