package se.umu.saha5924.bettbok;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.UUID;

public class BiteFragment extends Fragment {

    public static final String ARG_BITE_ID = "bite_id";

    NavController mNavController;

    private UUID mBiteId;                   // Id of Bite.
    private TextView mPlacementTextView;    // TextView for showing placement of Bite.
    private TextView mDateTextView;         // TextView for showing date of Bite.
    private TextView mDaysSinceBiteTextView;// TextView for showing number of days since Bite.
    private TextView mStageTextView;        // TextView for showing stage of tick..

    private Photo mFirstPhoto;  // Photo showing bite after 0 days.
    private Photo mSecondPhoto; // Photo showing bite after 14 days.
    private Photo mThirdPhoto;  // Photo showing bite after 28 days.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Retrieve UUID of a Bite stored as an argument in the Bundle.
        assert getArguments() != null;
        mBiteId = (UUID) getArguments().getSerializable(ARG_BITE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bite, container, false);

        mPlacementTextView = v.findViewById(R.id.placement_text_view);
        mDateTextView = v.findViewById(R.id.date_text_view);
        mDaysSinceBiteTextView = v.findViewById(R.id.days_since_bite);
        mStageTextView = v.findViewById(R.id.stage);

        // A floating action button that will navigate to BiteEditFragment.
        FloatingActionButton mEditFab = v.findViewById(R.id.fab_edit_bite);
        mEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass id of Bite to BiteEditFragment as argument in Bundle.
                Bundle args = new Bundle();
                args.putSerializable(BiteEditFragment.ARG_BITE_ID, mBiteId);
                mNavController.navigate(R.id.action_biteFragment_to_biteEditFragment, args);
            }
        });

        initPhotos(v);
        updateUI();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        mFirstPhoto.updateImageButton();
        mSecondPhoto.updateImageButton();
        mThirdPhoto.updateImageButton();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Request for deleting Bite.
        if (item.getItemId() == R.id.delete_bite) {
            removeBiteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Initiate the photos of the Bite.
    private void initPhotos(View v) {
        Bite b = BiteLab.get(getActivity()).getBite(mBiteId);
        mFirstPhoto = new Photo
                (getActivity(), v, R.id.first_image_button, b, 1);
        mSecondPhoto = new Photo
                (getActivity(), v, R.id.second_image_button, b, 2);
        mThirdPhoto = new Photo
                (getActivity(), v, R.id.third_image_button, b, 3);

        // Inactivate the possibility to press Photo when in BiteFragment.
        mFirstPhoto.inactivateButton();
        mSecondPhoto.inactivateButton();
        mThirdPhoto.inactivateButton();
    }

    // Update UI to show current information of Bite.
    private void updateUI() {
        Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);

        mPlacementTextView.setText(bite.getPlacement());

        Calendar c = bite.getCalendar();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDateTextView.setText(getString(R.string.show_date, day, month, year));

        mDaysSinceBiteTextView.setText(getString(R.string.days_since_bite
                , bite.getDaysSinceBite()));

        mStageTextView.setText(getString(R.string.show_stage, bite.getStage()));
    }

    // Shows a dialog to confirm that Bite should be removed.
    private void removeBiteDialog() {
        AlertDialog removeBiteDialog = new AlertDialog.Builder(getActivity()).create();
        removeBiteDialog.setTitle(getString(R.string.remove_bite_dialog_title));
        removeBiteDialog.setMessage(getString(R.string.remove_bite_dialog_message));

        removeBiteDialog.setButton(AlertDialog.BUTTON_POSITIVE
                , "JA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // If confirmed, Bite is removed and navigation back to BiteListFragment.
                Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
                BiteLab.get(getActivity()).deleteBite(bite);
                requireActivity().onBackPressed();
            }
        });

        removeBiteDialog.setButton(AlertDialog.BUTTON_NEGATIVE
                , "NEJ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nothing
            }
        });

        removeBiteDialog.show();
    }
}
