package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
 * BiteEditFragment is responsible for the Fragment connected to a Bite.
 */
public class BiteEditFragment extends Fragment {

    public static final String ARG_BITE_ID = "bite_id";

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_STAGE = "DialogStage";

    private static final int REQUEST_CALENDAR = 0;
    private static final int REQUEST_FIRST_PHOTO = 1;
    private static final int REQUEST_SECOND_PHOTO = 2;
    private static final int REQUEST_THIRD_PHOTO = 3;
    private static final int REQUEST_STAGE = 4;

    private Bite mBite;         // Bite to edit.
    private Button mDateButton; // Button for changing date.
    private Button mStageButton;// Button for changing stage.

    private Photo mFirstPhoto;  // Photo showing bite after 0 days.
    private Photo mSecondPhoto; // Photo showing bite after 14 days.
    private Photo mThirdPhoto;  // Photo showing bite after 28 days.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the UUID stored as an argument and find the Bite connected to it.
        assert getArguments() != null;
        UUID biteId = (UUID) getArguments().getSerializable(ARG_BITE_ID);
        assert biteId != null;
        mBite = BiteLab.get(getActivity()).getBite(biteId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_bite, container, false);

        initPlacementEdit(v);
        initDateButton(v);
        initStageButton(v);
        initPhotos(v);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_CALENDAR && data != null) {
            // Collect Calendar from DatePickerDialog.
            Calendar c = (Calendar) data.getSerializableExtra(DatePickerFragment.EXTRA_CALENDAR);
            mBite.setCalendar(c);
            BiteLab.get(getActivity()).updateBite(mBite);
            updateUIDate();

        } else if (requestCode == REQUEST_STAGE && data != null) {
            // Collect stage as String from StagePickerDialog.
            String s = (String) data.getSerializableExtra(StagePickerFragment.EXTRA_STAGE);
            mBite.setStage(s);
            BiteLab.get(getActivity()).updateBite(mBite);
            updateUIStage();

        } else if (requestCode == REQUEST_FIRST_PHOTO) {
            mFirstPhoto.handlePhotoRequest();

        } else if (requestCode == REQUEST_SECOND_PHOTO) {
            mSecondPhoto.handlePhotoRequest();

        } else if (requestCode == REQUEST_THIRD_PHOTO) {
            mThirdPhoto.handlePhotoRequest();

        }
    }

    @Override
    public void onResume() {
        mFirstPhoto.updateImageButton();
        mSecondPhoto.updateImageButton();
        mThirdPhoto.updateImageButton();
        super.onResume();
    }

    // Initiate EditText field for the placement of Bite.
    private void initPlacementEdit(View v) {
        EditText mPlacementEditText = v.findViewById(R.id.placement_edit_text);
        mPlacementEditText.setText(mBite.getPlacement());
        mPlacementEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBite.setPlacement(s.toString());
                BiteLab.get(getActivity()).updateBite(mBite);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action
            }
        });
    }

    // Initiate Button for the Date of Bite.
    private void initDateButton(View v) {
        mDateButton = v.findViewById(R.id.date_button);
        updateUIDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBite.getCalendar());
                // Make BiteEditFragment the target fragment for DatePickerFragment.
                dialog.setTargetFragment(BiteEditFragment.this, REQUEST_CALENDAR);
                dialog.show(manager, DIALOG_DATE);
            }
        });
    }

    // Initiate Button for the stage of Bite.
    private void initStageButton(View v) {
        mStageButton = v.findViewById(R.id.stage_button);
        updateUIStage();
        mStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                StagePickerFragment dialog = new StagePickerFragment();
                dialog.setTargetFragment(BiteEditFragment.this, REQUEST_STAGE);
                dialog.show(manager, DIALOG_STAGE);
            }
        });
    }

    // Initiate the Photos of Bite.
    private void initPhotos(View v) {
        mFirstPhoto = new Photo
                (getActivity(), v, R.id.first_image_button
                        , BiteLab.get(getActivity()).getFirstImageFile(mBite));
        mSecondPhoto = new Photo
                (getActivity(), v, R.id.second_image_button
                        , BiteLab.get(getActivity()).getSecondImageFile(mBite));
        mThirdPhoto = new Photo
                (getActivity(), v, R.id.third_image_button
                        , BiteLab.get(getActivity()).getThirdImageFile(mBite));

        // Activate Button on Photos to take new photos.
        mFirstPhoto.activateButton(REQUEST_FIRST_PHOTO);
        mSecondPhoto.activateButton(REQUEST_SECOND_PHOTO);
        mThirdPhoto.activateButton(REQUEST_THIRD_PHOTO);
    }

    // Update text on Button for the Date to reflect the current state of Bite.
    private void updateUIDate() {
        Calendar c = mBite.getCalendar();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDateButton.setText(getString(R.string.show_date, day, month, year));
    }

    // Update text on Button for the stage to reflect the current state of Bite.
    private void updateUIStage() {
        mStageButton.setText(getString(R.string.show_stage, mBite.getStage()));
    }
}