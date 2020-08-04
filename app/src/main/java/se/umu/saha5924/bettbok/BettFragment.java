package se.umu.saha5924.bettbok;

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

import java.util.Calendar;
import java.util.UUID;

/**
 * BettFragment is responsible for the Fragment connected to a Bett.
 *
 */

public class BettFragment extends Fragment {

    private static final String ARG_BETT_ID = "bett_id";

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
        Calendar c = mBett.getmDatum();
        mDatumButton.setText("Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH)
                + " -" + c.get(Calendar.YEAR)); //TODO
        mDatumButton.setEnabled(false);

        return v;
    }
}