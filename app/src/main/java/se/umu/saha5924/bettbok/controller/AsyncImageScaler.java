package se.umu.saha5924.bettbok.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

/**
 * AsyncImageScaler is responsible for, in a separate thread, scaling an
 * image on a given filepath to fit a given ImageView.
 */
public class AsyncImageScaler extends AsyncTask<File, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private ImageView view;

    private int width;
    private int height;

    /**
     * Constructor for AsyncImageScaler.
     *
     * @param activity The active Activity.
     * @param view The ImageView to display the image.
     */
    public AsyncImageScaler(Activity activity, ImageView view) {
        // Use a conservative estimate of the size of the screen.
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        width = size.x;
        height = size.y;

        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(File... file) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        // Read the File of the image.
        BitmapFactory.decodeFile(file[0].getAbsolutePath(), bmOptions);

        // The dimensions of the image.
        int srcImageWidth = bmOptions.outWidth;
        int srcImageHeight = bmOptions.outHeight;

        // Determine how much to scale down the image
        int inSampleSize = Math.max(1, Math.min(srcImageWidth/width, srcImageHeight/height));

        // Make the photo into a Bitmap with suitable size.
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = inSampleSize;

        // Read in and return the final Bitmap.
        return BitmapFactory.decodeFile(file[0].getAbsolutePath(), bmOptions);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }
}
