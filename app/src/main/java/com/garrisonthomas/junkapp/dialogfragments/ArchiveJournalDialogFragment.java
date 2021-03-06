package com.garrisonthomas.junkapp.dialogfragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.TabsActivity;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.garrisonthomas.junkapp.BaseActivity.preferences;

public class ArchiveJournalDialogFragment extends DialogFragmentHelper {

    private EditText endOfDayNotes;
    private Button dEndTime, nEndTime;
    private String currentJournalRef, driver, driverStartTime, navigator, navStartTime, loadString, fuelString;
    private String[] endLoadArray, endFuelArray;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Archive Journal");

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.archive_journal_layout, container, false);

        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);
        driver = preferences.getString("driver", null);
        driverStartTime = preferences.getString("driverStartTime", null);
        navigator = preferences.getString("navigator", null);
        navStartTime = preferences.getString("navStartTime", null);

        endOfDayNotes = v.findViewById(R.id.end_day_notes);

        dEndTime = v.findViewById(R.id.driver_end_time);
        dEndTime.setText(driver);
        dEndTime.setTransformationMethod(null);
        nEndTime = v.findViewById(R.id.nav_end_time);
        nEndTime.setText(navigator);
        nEndTime.setTransformationMethod(null);

        View cancelSaveLayout = v.findViewById(R.id.archive_cancel_save_button_bar);

        Button cancel = cancelSaveLayout.findViewById(R.id.btn_cancel),
                archive = cancelSaveLayout.findViewById(R.id.btn_save);
        archive.setText("ARCHIVE");

        endLoadArray = v.getResources().getStringArray(R.array.end_day_load);
        endFuelArray = v.getResources().getStringArray(R.array.end_day_fuel);

        final Spinner endLoad = v.findViewById(R.id.end_day_load),
                endFuel = v.findViewById(R.id.end_day_fuel);

        endLoad.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, endLoadArray));

        endFuel.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, endFuelArray));

        endLoad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                loadString = endLoadArray[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                createTimePickerDialog(getActivity(), dEndTime, driver + " Out").show();
            }
        });

        nEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog(getActivity(), nEndTime, navigator + " Out").show();
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

        final String driverEndTime = dEndTime.getText().toString();
        final String navEndTime = nEndTime.getText().toString();

        final ProgressDialog pDialog = ProgressDialog.show(getActivity(), null,
                "Archiving journal...", true);

        DatabaseReference fbrJournal = FirebaseDatabase.getInstance()
                .getReference(currentJournalRef + "info");

        fbrJournal.child("driverTime").setValue(driverStartTime + "-" + driverEndTime);
        fbrJournal.child("navTime").setValue(navStartTime + "-" + navEndTime);
        fbrJournal.child("endOfDayNotes").setValue("Load: " + loadString + ". Fuel: " + fuelString +
                ". Notes: " + endOfDayNotes.getText().toString());
        fbrJournal.child("archived").setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
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

