package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
 * BettFragment is responsible for the Fragment connected to a Bett.
 *
 */

public class BettFragment extends Fragment {

    private static final String ARG_BETT_ID = "bett_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_CALENDAR = 0;

    private Bett mBett;
    private EditText mPlaceringField;
    private Button mDatumButton;

    /**
     * newInstance will create and return a BettFragment containing a UUID of a Bett.
     *
     * @param id The UUID of the Bett.
     * @return The BettFragment containing the given UUID.
     */
    public static BettFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BETT_ID, id);
        BettFragment fragment = new BettFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the UUID stored as an argument and find the Bett connected to it.
        UUID bettId = (UUID) getArguments().getSerializable(ARG_BETT_ID);
        mBett = BettLab.getBett(bettId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bett, container, false);

        mPlaceringField = v.findViewById(R.id.bett_placering);
        mPlaceringField.setText(mBett.getmPlacering());
        mPlaceringField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBett.setmPlacering(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action
            }
        });

        mDatumButton = v.findViewById(R.id.bett_datum);
        updateDate();
        mDatumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getParentFragmentManager(); // TODO
                DatePickerFragment dialog = DatePickerFragment.newInstance(mBett.getmDatum());
                // Make BiteFragment the target fragment for DatePickerFragment.
                dialog.setTargetFragment(BettFragment.this, REQUEST_CALENDAR);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        // Collect the Calendar from the DatePickerFragment.
        if (requestCode == REQUEST_CALENDAR && data != null) {
            Calendar c = (Calendar) data.getSerializableExtra(DatePickerFragment.EXTRA_CALENDAR);
            mBett.setmDatum(c);
            updateDate();
        }
    }

    private void updateDate() {
        Calendar c = mBett.getmDatum();
        mDatumButton.setText("Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH)
                + " -" + c.get(Calendar.YEAR)); // TODO
    }
}