package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;
import java.util.UUID;

/**
 * BettFragment is responsible for the Fragment connected to a Bett.
 *
 */

public class BiteEditFragment extends Fragment {

    private static final String ARG_BETT_ID = "bett_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_CALENDAR = 0;

    private Bite mBite;
    private EditText mPlaceringField;
    private Button mDatumButton;

    /**
     * newInstance will create and return a new instance of BiteFragment containing a UUID of a Bite.
     *
     * @param id The UUID of the Bite.
     * @return The BiteFragment containing the given UUID.
     */
    public static BiteEditFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BETT_ID, id);
        BiteEditFragment fragment = new BiteEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Retrieve the UUID stored as an argument and find the Bite connected to it.
        UUID bettId = (UUID) getArguments().getSerializable(ARG_BETT_ID);
        mBite = BiteLab.get(getActivity()).getBite(bettId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_bite, container, false);

        mPlaceringField = v.findViewById(R.id.bite_placement_edit_text);
        mPlaceringField.setText(mBite.getPlacement());
        mPlaceringField.addTextChangedListener(new TextWatcher() {
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

        mDatumButton = v.findViewById(R.id.bite_date_button);
        updateDate();
        mDatumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager(); // TODO
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBite.getCalendar());
                // Make BiteFragment the target fragment for DatePickerFragment.
                dialog.setTargetFragment(BiteEditFragment.this, REQUEST_CALENDAR);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Update BiteLab's Bite when BiteFragment is done.
        //BiteLab.get(getActivity()).updateBite(mBite);
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
                BiteLab.get(getActivity()).updateBite(mBite);
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
        }
    }

    private void updateDate() {
        Calendar c = mBite.getCalendar();
        mDatumButton.setText("Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH)
                + " -" + c.get(Calendar.YEAR)); // TODO
    }
}