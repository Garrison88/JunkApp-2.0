package com.garrisonthomas.junkapp.dialogfragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.TabsActivity;

public class ArchiveJournalDialogFragment extends DialogFragmentHelper {

    private SharedPreferences preferences;
    private EditText endOfDayNotes;
    private Button cancel, archive, dEndTime, nEndTime;
    private String currentJournalRef, driver, navigator, loadString, fuelString;
    private String[] endLoadArray, endFuelArray;
    private ProgressDialog pDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Archive Journal");

//        Bundle eodBundle = getArguments();
//
//        driver = eodBundle.getString("driver");
//        navigator = eodBundle.getString("navigator");

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.archive_journal_layout, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);
        driver = preferences.getString("driver", null);
        navigator = preferences.getString("navigator", null);

        endOfDayNotes = (EditText) v.findViewById(R.id.end_day_notes);

        dEndTime = (Button) v.findViewById(R.id.driver_end_time);
        dEndTime.setText(driver);
        dEndTime.setTransformationMethod(null);
        nEndTime = (Button) v.findViewById(R.id.nav_end_time);
        nEndTime.setText(navigator);
        nEndTime.setTransformationMethod(null);

        View cancelSaveLayout = v.findViewById(R.id.archive_cancel_save_button_bar);
        archive = (Button) cancelSaveLayout.findViewById(R.id.btn_save);
        archive.setText("ARCHIVE");
        cancel = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        endLoadArray = v.getResources().getStringArray(R.array.end_day_load);
        endFuelArray = v.getResources().getStringArray(R.array.end_day_fuel);

        final Spinner endLoad = (Spinner) v.findViewById(R.id.end_day_load);
        final Spinner endFuel = (Spinner) v.findViewById(R.id.end_day_fuel);

        endLoad.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, endLoadArray));
        endLoad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                loadString = endLoadArray[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        endFuel.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, endFuelArray));
        endFuel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                fuelString = endFuelArray[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog(getActivity(), dEndTime, "Driver Out").show();
            }
        });

        nEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog(getActivity(), nEndTime, "Nav Out").show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String DET = dEndTime.getText().toString();
                final String NET = nEndTime.getText().toString();

                if (!DET.equals(driver)
                        && !NET.equals(navigator)
                        && endFuel.getSelectedItemPosition() != 0
                        && endLoad.getSelectedItemPosition() != 0) {
                    confirmJournalArchive();
                } else {
                    Toast.makeText(getActivity(), "Please clock out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setCancelable(false);
        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private AlertDialog confirmJournalArchive() {

        return new AlertDialog.Builder(getActivity())
                .setTitle("Archive Journal?")
                .setMessage("You will no longer be able to view or edit this journal")
                .setIcon(R.drawable.ic_warning_white_24dp)

                .setPositiveButton("archive", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                        archiveJournal();

                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .show();
    }

    public void archiveJournal() {

        final String DET = dEndTime.getText().toString();
        final String NET = nEndTime.getText().toString();

        pDialog = ProgressDialog.show(getActivity(), null,
                "Archiving journal...", true);

        Firebase fbrJournal = new Firebase(currentJournalRef + "info");

        fbrJournal.child("driverEndTime").setValue(DET);
        fbrJournal.child("navEndTime").setValue(NET);
//        fbrJournal.child("percentOfGoal").setValue(percentOfGoal);
//        fbrJournal.child("totalGrossProfit").setValue(totalGrossProfit);
//        fbrJournal.child("percentOnDumps").setValue(percentOnDumps);
//        fbrJournal.child("totalDumpCost").setValue(totalDumpCost);
        fbrJournal.child("endOfDayNotes").setValue("Load: " + loadString + ". Fuel: " + fuelString +
                ". Notes: " + endOfDayNotes.getText().toString());
        fbrJournal.child("archived").setValue(true, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("currentJournalRef", null);
                editor.putString("driver", null);
                editor.putString("navigator", null);
                editor.apply();
                Toast.makeText(getActivity(), "Journal successfully archived",
                        Toast.LENGTH_SHORT).show();

                ((TabsActivity) getActivity()).notifyJournalChanged();

                pDialog.dismiss();

                dismiss();
            }
        });
    }
}