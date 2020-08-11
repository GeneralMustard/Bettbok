package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * BettFragment is responsible for the Fragment connected to a Bett.
 *
 */

public class BiteEditFragment extends Fragment {

    private static final String ARG_BITE_ID = "bite_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_CALENDAR = 0;
    private static final int REQUEST_PHOTO = 1;

    private Bite mBite;
    private EditText mPlacementEditText;
    private Button mDateButton;
    private ImageButton mZeroDaysImageButton;
    private File mImageFile;

    /**
     * newInstance will create and return a new instance of BiteFragment containing a UUID of a Bite.
     *
     * @param id The UUID of the Bite.
     * @return The BiteFragment containing the given UUID.
     */
    public static BiteEditFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BITE_ID, id);
        BiteEditFragment fragment = new BiteEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static BiteEditFragment newInstance() {
        return new BiteEditFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().getSerializable(ARG_BITE_ID) != null) {
            // Retrieve the UUID stored as an argument and find the Bite connected to it.
            UUID biteId = (UUID) getArguments().getSerializable(ARG_BITE_ID);
            mBite = BiteLab.get(getActivity()).getBite(biteId);
        } else {
            mBite = new Bite();
        }
        mImageFile = BiteLab.get(getActivity()).getImageFile(mBite);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_bite, container, false);

        mPlacementEditText = v.findViewById(R.id.placement_edit_text);
        mPlacementEditText.setText(mBite.getPlacement());
        mPlacementEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBite.setPlacement(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action
            }
        });

        mDateButton = v.findViewById(R.id.date_button);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager(); // TODO
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBite.getCalendar());
                // Make BiteEditFragment the target fragment for DatePickerFragment.
                dialog.setTargetFragment(BiteEditFragment.this, REQUEST_CALENDAR);
                dialog.show(manager, DIALOG_DATE);
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
        updateImageButton();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Update BiteLab's Bite when BiteFragment is done.
        //BiteLab.get(getActivity()).updateBite(mBite);
    }

    private void updateImageButton() {
        if (mImageFile == null || !mImageFile.exists()) {
            mZeroDaysImageButton.setImageDrawable(null);
        } else {
            Bitmap bm = ImageScaler.getScaledBitmap(mImageFile.getPath(), getActivity());
            mZeroDaysImageButton.setImageBitmap(bm);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_bite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Request to save the changes.
            case R.id.save_bite:
                // Update BiteLab's Bite when Bite is saved.
                if (BiteLab.get(getActivity()).biteExist(mBite.getId())) {
                    BiteLab.get(getActivity()).updateBite(mBite);
                } else {
                    BiteLab.get(getActivity()).addBite(mBite);
                }
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        // Collect the Calendar from the DatePickerFragment.
        if (requestCode == REQUEST_CALENDAR && data != null) {
            Calendar c = (Calendar) data.getSerializableExtra(DatePickerFragment.EXTRA_CALENDAR);
            mBite.setCalendar(c);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity()
                    , getActivity().getPackageName() + ".fileprovider"
                    , mImageFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateImageButton();
        }
    }

    private void updateDate() {
        Calendar c = mBite.getCalendar();
        mDateButton.setText("Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH)
                + " -" + c.get(Calendar.YEAR)); // TODO
    }
}