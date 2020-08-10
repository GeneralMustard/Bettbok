package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BiteFragment extends Fragment {

    private static final String ARG_BITE_ID = "bite_id";
    private static final int REQUEST_PHOTO = 0;

    private UUID mBiteId;
    private TextView mPlacementTextView;
    private TextView mDateTextView;
    private FloatingActionButton mEditFab;
    private ImageButton mZeroDaysImageButton;
    private ImageView mZeroDaysImageView;
    private File mImageFile;


    /**
     * newInstance will create and return a new instance of BiteFragment containing a UUID of a Bite.
     *
     * @param id The UUID of the Bite.
     * @return The BiteFragment containing the given UUID.
     */
    public static BiteFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BITE_ID, id);
        BiteFragment fragment = new BiteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Retrieve the UUID stored as an argument and find the Bite connected to it.
        mBiteId = (UUID) getArguments().getSerializable(ARG_BITE_ID);
        Bite b = BiteLab.get(getActivity()).getBite(mBiteId);
        mImageFile = BiteLab.get(getActivity()).getImageFile(b);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bite, container, false);

        mPlacementTextView = v.findViewById(R.id.placement_text_view);
        mDateTextView = v.findViewById(R.id.date_text_view);

        mEditFab = v.findViewById(R.id.fab_edit_bite);
        mEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BiteEditActivity.newIntent(getActivity(), mBiteId);
                startActivity(intent);
            }
        });


        mZeroDaysImageButton = v.findViewById(R.id.zero_days_image_button);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // User is not able to take photo if the device does not have a camera.
        boolean canTakePhoto = mImageFile != null &&
                captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        mZeroDaysImageButton.setEnabled(canTakePhoto);

        mZeroDaysImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Translate the local filepath for camera.
                Uri uri = FileProvider.getUriForFile(getActivity()
                        , getActivity().getPackageName() + ".fileprovider"
                        , mImageFile);

                // Send the filepath for camera to use.
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                // Grant permission to write to specific uri.
                for (ResolveInfo activity : cameraActivities)
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mZeroDaysImageView = v.findViewById(R.id.zero_days_image_view);
        updateImageView();

        updateUI();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateImageView() {
        if (mImageFile == null || !mImageFile.exists()) {
            mZeroDaysImageView.setImageDrawable(null);
        } else {
            Bitmap bm = ImageScaler.getScaledBitmap(mImageFile.getPath(), getActivity());
            mZeroDaysImageView.setImageBitmap(bm);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity()
                    , getActivity().getPackageName() + ".fileprovider"
                    , mImageFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateImageView();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // The user has requested a new Bite.
            case R.id.delete_bite:
                Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
                BiteLab.get(getActivity()).deleteBite(bite);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
        mPlacementTextView.setText(bite.getPlacement());

        Calendar c = bite.getCalendar();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDateTextView.setText(getString(R.string.show_date, day, month, year));
    }
}