package se.umu.saha5924.bettbok;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.UUID;

public class BiteFragment extends Fragment {

    private static final String ARG_BITE_ID = "bite_id";
    private static final int REQUEST_IS_EDITED = 0;

    private UUID mBiteId;
    private TextView mPlacementTextView;
    private TextView mDateTextView;
    private FloatingActionButton mEditFab;

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
        //mBite = BiteLab.get(getActivity()).getBite(biteId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bite, container, false);

        mPlacementTextView = v.findViewById(R.id.placement_text_view);
        //mPlacementTextView.setText(mBite.getPlacement());

        mDateTextView = v.findViewById(R.id.date_text_view);
        //mDateTextView.setText(getDate());

        mEditFab = v.findViewById(R.id.fab_edit_bite);
        mEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BiteEditActivity.newIntent(getActivity(), mBiteId);
                startActivity(intent);
            }
        });

        updateUI();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
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
                // delete
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getDate() {
        Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
        Calendar c = bite.getCalendar();
        return "Den " + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH)
                + " -" + c.get(Calendar.YEAR); // TODO
    }

    private void updateUI() {
        Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
        mPlacementTextView.setText(bite.getPlacement());
        mDateTextView.setText(getDate());
    }
}
