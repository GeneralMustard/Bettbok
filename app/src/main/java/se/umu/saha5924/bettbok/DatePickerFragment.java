package se.umu.saha5924.bettbok;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_CALENDAR = "se.umu.saha5924.bettbok.calendar";
    private static final String ARG_CALENDAR = "calendar";

    private DatePicker mDatePicker;

    /**
     * newInstance will create and return a DatePickerFragment
     * containing a Calendar as an argument in the Bundle.
     *
     * @param calendar The calendar for the argument.
     * @return The created DatePickerFragment.
     */
    public static DatePickerFragment newInstance(Calendar calendar) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALENDAR, calendar);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        // Retrieve the Calendar from the Bundle and use it to get the year, month and day.
        Calendar c = (Calendar) getArguments().getSerializable(ARG_CALENDAR);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Let the DatePicker show the saved date for the Bite.
        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // When OK is pressed, send back the chosen Calendar.
                        Calendar c = Calendar.getInstance();
                        c.set(mDatePicker.getYear()
                                , mDatePicker.getMonth()
                                , mDatePicker.getDayOfMonth());
                        sendResult(c);
                    }
                })
                .create();
    }

    // The Fragment passes the chosen Calendar back to the host as an extra.
    private void sendResult(Calendar c) {
        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CALENDAR, c);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
