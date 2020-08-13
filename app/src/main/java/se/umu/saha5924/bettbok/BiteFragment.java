package se.umu.saha5924.bettbok;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.UUID;

public class BiteFragment extends Fragment {

    NavController mNavController;

    public static final String ARG_BITE_ID = "bite_id";
    private static final String DIALOG_REMOVE_BITE = "DialogRemoveBite";

    private UUID mBiteId;
    private TextView mPlacementTextView;
    private TextView mDateTextView;
    private TextView mDaysSinceBite;
    private TextView mStage;
    private FloatingActionButton mEditFab;


    private Photo mFirstPhoto;
    private Photo mSecondPhoto;
    private Photo mThirdPhoto;


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

        // Retrieve the UUID of a Bite stored as an argument.
        mBiteId = (UUID) getArguments().getSerializable(ARG_BITE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bite, container, false);

        mPlacementTextView = v.findViewById(R.id.placement_text_view);
        mDateTextView = v.findViewById(R.id.date_text_view);
        mDaysSinceBite = v.findViewById(R.id.days_since_bite);
        mStage = v.findViewById(R.id.stage);

        mEditFab = v.findViewById(R.id.fab_edit_bite);
        mEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = BiteEditActivity.newIntent(getActivity(), mBiteId);
                startActivity(intent);*/
                Bundle args = new Bundle();
                args.putSerializable(BiteEditFragment.ARG_BITE_ID, mBiteId);
                mNavController.navigate(R.id.action_biteFragment_to_biteEditFragment, args);
            }
        });

        Bite b = BiteLab.get(getActivity()).getBite(mBiteId);
        mFirstPhoto = new Photo
                (getActivity(), v, R.id.first_image_button, b, 1);
        mSecondPhoto = new Photo
                (getActivity(), v, R.id.second_image_button, b, 2);
        mThirdPhoto = new Photo
                (getActivity(), v, R.id.third_image_button, b, 3);

        mFirstPhoto.inactivateButton(); //TODO
        mSecondPhoto.inactivateButton();
        mThirdPhoto.inactivateButton();

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

        Log.e("onResume", "on resume called BiteFragment");

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
        switch (item.getItemId()) {
            // The user has requested a new Bite.
            case R.id.delete_bite:
                removeBiteDialog();
                /*FragmentManager fm = getParentFragmentManager();
                RemoveBiteDialog rb = new RemoveBiteDialog();
                rb.show(fm, DIALOG_REMOVE_BITE);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeBiteDialog() {
        AlertDialog removeBite = new AlertDialog.Builder(getActivity()).create();
        removeBite.setTitle(getString(R.string.remove_bite_dialog_title));
        removeBite.setMessage(getString(R.string.remove_bite_dialog_message));

        removeBite.setButton(AlertDialog.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
                BiteLab.get(getActivity()).deleteBite(bite);
                requireActivity().onBackPressed();
            }
        });

        removeBite.setButton(AlertDialog.BUTTON_NEGATIVE, "NEJ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nothing
            }
        });

        removeBite.show();
    }

    private void updateUI() {
        Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
        String s = bite.getPlacement(); //TODO
        mPlacementTextView.setText(bite.getPlacement());

        Calendar c = bite.getCalendar();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        mDateTextView.setText(getString(R.string.show_date, day, month, year));

        mDaysSinceBite.setText(getString(R.string.days_since_bite
                , bite.getDaysSinceBite()));

        mStage.setText(getString(R.string.show_stage, bite.getStage()));
    }

    //TODO
    public class RemoveBiteDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            //return super.onCreateDialog(savedInstanceState);
            AlertDialog removeBite = new AlertDialog.Builder(getActivity()).create();
            removeBite.setTitle(getString(R.string.remove_bite_dialog_title));
            removeBite.setMessage(getString(R.string.remove_bite_dialog_message));

            removeBite.setButton(AlertDialog.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bite bite = BiteLab.get(getActivity()).getBite(mBiteId);
                    BiteLab.get(getActivity()).deleteBite(bite);
                    getActivity().finish();
                }
            });

            removeBite.setButton(AlertDialog.BUTTON_NEGATIVE, "NEJ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Nothing
                }
            });

            return removeBite;
        }
    }

}
