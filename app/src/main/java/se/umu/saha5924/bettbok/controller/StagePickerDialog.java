package se.umu.saha5924.bettbok.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import se.umu.saha5924.bettbok.R;

/**
 * StagePickerDialog is responsible for the dialog that lets
 * the user choose the stage of the tick that made the bite.
 */

public class StagePickerDialog extends DialogFragment {

    public static final String EXTRA_STAGE = "se.umu.saha5924.bettbok.stage";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.stage_dialog_title)
                .setItems(R.array.stages_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Find the stage chosen by the user and send it to the host.
                        String[] tmp = getResources().getStringArray(R.array.stages_array);
                        sendResult(tmp[which]);
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Nothing
                        }
                });
        return builder.create();
    }

    // The Fragment passes the chosen stage back to the host as an extra.
    private void sendResult(String s) {
        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_STAGE, s);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
