package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

public class Photo {

    private ImageButton mImageButton;
    private File mImageFile;
    private Activity mActivity;
    private int mRequest;

    public Photo(Activity activity, View v, int layoutId, Bite bite, int request) {
        this.mImageButton = v.findViewById(layoutId);
        this.mActivity = activity;
        this.mRequest = request;
        this.mImageFile = BiteLab.get(activity).getImageFile(bite, request);

        mImageButton.setOnClickListener(new PhotoButton());
        updateImageButton();
    }

    public void updateImageButton() {
        if (mImageFile == null || !mImageFile.exists()) {
            mImageButton.setImageDrawable(null);
        } else {
            Bitmap bm = ImageScaler.getScaledBitmap(mImageFile.getPath(), mActivity);
            mImageButton.setImageBitmap(bm);
        }
    }

    public void handlePhotoRequest() {
        Uri uri = FileProvider.getUriForFile(mActivity
                , mActivity.getPackageName() + ".fileprovider"
                , mImageFile);
        mActivity.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        updateImageButton();
    }

    public void inactivateButton() {
        mImageButton.setEnabled(false);
    }

    private class PhotoButton implements View.OnClickListener {

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        public PhotoButton() {
            // User is not able to take photo if the device does not have a camera.
            boolean canTakePhoto = mImageFile != null &&
                    captureImage.resolveActivity(mActivity.getPackageManager()) != null;
            mImageButton.setEnabled(canTakePhoto);
        }

        @Override
        public void onClick(View v) {

            // Translate the local filepath for camera.
            Uri uri = FileProvider.getUriForFile(mActivity
                    , mActivity.getPackageName() + ".fileprovider"
                    , mImageFile);

            // Send the filepath for camera to use.
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> cameraActivities = mActivity.getPackageManager()
                    .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

            // Grant permission to write to specific uri.
            for (ResolveInfo activity : cameraActivities)
                mActivity.grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            mActivity.startActivityForResult(captureImage, mRequest);
        }
    }

}
