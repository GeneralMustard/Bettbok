package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

public class Photo {

    private ImageButton mImageButton; // ImageButton for displaying the photo and to take new photo
    private File mImageFile;    // File used for photo.
    private Activity mActivity; // Active Activity.
    private int mRequest;       // Request used for retrieving the photo taken by the camera.

    /**
     * Constructor for Photo.
     * Will show the photo of the given file on the given layout.
     * If there is no File on given path, the photo will be left empty.
     * The Photo will not be clickable by default.
     *
     * @param activity The active Activity.
     * @param view The active View.
     * @param layoutId The id of the layout to show photo.
     * @param file The File of the photo that should be shown.
     */
    public Photo(Activity activity, View view, int layoutId, File file) {
        mImageButton = view.findViewById(layoutId);
        mActivity = activity;
        mImageFile = file;
        mImageButton.setEnabled(false);
        updateImageButton();
    }

    /**
     * Will update the photo if a File exists.
     */
    public void updateImageButton() {
        if (mImageFile == null || !mImageFile.exists()) {
            mImageButton.setImageDrawable(null);
        } else {
            // Set a scaled photo from the File on ImageButton
            new AsyncImageScaler(mActivity, mImageButton).execute(mImageFile);
        }
    }

    /**
     * Will enable the photo to be pressed.
     * Pressing the photo will start the camera.
     *
     * @param request int representing the request for collecting photo taken by camera.
     */
    public void activateButton(int request) {
        mRequest = request;
        mImageButton.setEnabled(true);
        mImageButton.setOnClickListener(new PhotoButton());
    }

    /**
     * Will revoke permission for camera to write to Uri.
     */
    public void handlePhotoRequest() {
        Uri uri = FileProvider.getUriForFile(mActivity
                , mActivity.getPackageName() + ".fileprovider"
                , mImageFile);
        mActivity.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    // PhotoButton will make the photo clickable to take a new photo.
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

            // Translate the local filepath into Uri for camera.
            Uri uri = FileProvider.getUriForFile(mActivity
                    , mActivity.getPackageName() + ".fileprovider"
                    , mImageFile);

            // Send the filepath for camera to use.
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> cameraActivities = mActivity.getPackageManager()
                    .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

            // Grant permission to write to specific uri.
            for (ResolveInfo activity : cameraActivities)
                mActivity.grantUriPermission(activity.activityInfo.packageName, uri
                        , Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            mActivity.startActivityForResult(captureImage, mRequest);
        }
    }
}
