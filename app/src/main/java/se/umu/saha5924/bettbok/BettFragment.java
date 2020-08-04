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
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.UUID;

public class BettFragment extends Fragment {

    private static final String ARG_BETT_ID = "bett_id";

    private Bett mBett;
    private EditText mPlaceringField;
    private Button mDatumButton;

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

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBett.setmPlacering(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDatumButton = v.findViewById(R.id.bett_datum);
        Calendar c = mBett.getmDatum();
        mDatumButton.setText("Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + " -" + c.get(Calendar.YEAR));
        mDatumButton.setEnabled(false);

        return v;
    }

    /*
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bett, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.bett_datum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(BettFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }*/
}