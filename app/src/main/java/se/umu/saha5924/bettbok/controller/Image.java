package se.umu.saha5924.bettbok.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.List;

/**
 * Image is responsible for displaying an image on a given filepath.
 * The image can be activated to function as a button, which will start the camera.
 */
public class Image {

    private ImageButton mImageButton; // ImageButton for displaying image and to start camera.
    private File mImageFile;    // File of the image.
    private Fragment mFragment; // Active Fragment.

    /**
     * Constructor for Photo.
     * Will show the image of the given file on the given layout.
     * If there is no file on given path, the image will be left empty.
     * The Image will not be clickable by default.
     *
     * @param fragment The active Fragment.
     * @param view The active View.
     * @param layoutId The id of the layout to show image.
     * @param file The file of the image that should be shown.
     */
    public Image(Fragment fragment, View view, int layoutId, File file) {
        mFragment = fragment;
        mImageButton = view.findViewById(layoutId);
        mImageButton.setEnabled(false);
        mImageFile = file;

        updateImageButton();
    }

    /**
     * Will update the image if a file exists.
     */
    public void updateImageButton() {
        if (mImageFile == null || !mImageFile.exists()) {
            mImageButton.setImageDrawable(null);
        } else {
            // Set a scaled photo from the File on ImageButton
            new AsyncImageScaler(mFragment.getActivity(), mImageButton).execute(mImageFile);
        }
    }

    /**
     * Will enable the image to be pressed.
     * Pressing the image will start the camera.
     *
     * @param request int representing the request for collecting photo taken by camera.
     */
    public void activateButton(int request) {
        mImageButton.setEnabled(true);
        mImageButton.setOnClickListener(new PhotoButton(request));
    }

    /**
     * Will revoke permission for camera to write to Uri.
     */
    public void handlePhotoRequest() {
        Activity a = mFragment.getActivity();
        if (a == null) return;

        Uri uri = FileProvider.getUriForFile(a
                , a.getPackageName() + ".fileprovider"
                , mImageFile);
        a.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        updateImageButton();
    }

    // PhotoButton will make the photo clickable to take a new photo.
    private class PhotoButton implements View.OnClickListener {

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        private int mRequest; // Request used for retrieving the photo taken by the camera.

        public PhotoButton(int request) {
            // User is not able to take photo if the device does not have a camera.
            boolean canTakePhoto = mImageFile != null &&
                    captureImage.resolveActivity(mFragment.getActivity().getPackageManager()) != null;
            mImageButton.setEnabled(canTakePhoto);

            mRequest = request;
        }

        @Override
        public void onClick(View v) {
            Activity a = mFragment.getActivity();

            // Translate the local filepath into Uri for camera.
            Uri uri = FileProvider.getUriForFile(a
                    , a.getPackageName() + ".fileprovider"
                    , mImageFile);

            // Send the filepath for camera to use.
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> cameraActivities = a.getPackageManager()
                    .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

            // Grant permission to write to specific uri.
            for (ResolveInfo activity : cameraActivities)
                a.grantUriPermission(activity.activityInfo.packageName, uri
                        , Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            mFragment.startActivityForResult(captureImage, mRequest);
        }
    }
}
