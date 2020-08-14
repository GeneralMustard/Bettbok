package se.umu.saha5924.bettbok;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.widget.ImageButton;

import java.io.File;

public class AsyncImageScaler extends AsyncTask<File, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private ImageButton photo;

    private int width;
    private int height;

    public AsyncImageScaler(Activity activity, ImageButton photo) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        width = size.x;
        height = size.y;
        this.photo = photo;
    }

    @Override
    protected Bitmap doInBackground(File... file) {
        // TODO skriv med egna ord
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file[0].getAbsolutePath(), bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/width, photoH/height));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(file[0].getAbsolutePath(), bmOptions);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
    }
/*TODO
    private static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        // Get the size of the saved image.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            inSampleSize = Math.round(Math.max(heightScale, widthScale));
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }*/
}
