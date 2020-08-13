package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;
import java.util.UUID;

/**
 * BiteEditFragment is responsible for the Fragment connected to a Bite.
 *
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

    private Bite mBite;
    private EditText mPlacementEditText;
    private Button mDateButton;
    private Button mStageButton;

    private Photo mFirstPhoto;
    private Photo mSecondPhoto;
    private Photo mThirdPhoto;

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
                BiteLab.get(getActivity()).updateBite(mBite);
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

        mStageButton = v.findViewById(R.id.stage_button);
        updateStage();
        mStageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager();
                StagePickerFragment dialog = new StagePickerFragment();
                dialog.setTargetFragment(BiteEditFragment.this, REQUEST_STAGE);
                dialog.show(manager, DIALOG_STAGE);
            }
        });

        mFirstPhoto = new Photo
                (getActivity(), v, R.id.first_image_button, mBite, REQUEST_FIRST_PHOTO);
        mSecondPhoto = new Photo
                (getActivity(), v, R.id.second_image_button, mBite, REQUEST_SECOND_PHOTO);
        mThirdPhoto = new Photo
                (getActivity(), v, R.id.third_image_button, mBite, REQUEST_THIRD_PHOTO);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.e("onPause", "onPause called BiteEditFragment");

        // Update BiteLab's Bite when BiteFragment is done.
        //BiteLab.get(getActivity()).updateBite(mBite);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirstPhoto.updateImageButton();
        mSecondPhoto.updateImageButton();
        mThirdPhoto.updateImageButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        // Collect the Calendar from the DatePickerFragment.
        if (requestCode == REQUEST_CALENDAR && data != null) {
            Calendar c = (Calendar) data.getSerializableExtra(DatePickerFragment.EXTRA_CALENDAR);
            mBite.setCalendar(c);
            BiteLab.get(getActivity()).updateBite(mBite);
            updateDate();
        } else if (requestCode == REQUEST_STAGE && data != null) {
            String s = (String) data.getSerializableExtra(StagePickerFragment.EXTRA_STAGE);
            mBite.setStage(s);
            BiteLab.get(getActivity()).updateBite(mBite);
            updateStage();
        } else if (requestCode == REQUEST_FIRST_PHOTO) {
            mFirstPhoto.handlePhotoRequest();
        } else if (requestCode == REQUEST_SECOND_PHOTO) {
            mSecondPhoto.handlePhotoRequest();
        } else if (requestCode == REQUEST_THIRD_PHOTO) {
            mThirdPhoto.handlePhotoRequest();
        }
    }

    private void updateDate() {
        Calendar c = mBite.getCalendar();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDateButton.setText(getString(R.string.show_date, day, month, year));
    }

    private void updateStage() {
        mStageButton.setText(getString(R.string.show_stage, mBite.getStage()));
    }
}