package com.garrisonthomas.junkapp.dialogfragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.garrisonthomas.junkapp.App;
import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.TabsActivity;
import com.garrisonthomas.junkapp.entryobjects.DailyJournalObject;

public class CreateJournalDialogFragment extends DialogFragmentHelper {

    private Spinner truckSpinner;
    private String[] truckArray;
    private Button cancel, createJournal, dStartTime, nStartTime;
    private EditText etDriver, etNavigator;
    private String truckSelected, currentJournalString;
    private SharedPreferences preferences;
    private ProgressDialog pDialog;
    private Firebase currentJournalRef;
    private boolean journalExists;
    TextView errorText;

    public static CreateJournalDialogFragment newInstance(int num) {
        CreateJournalDialogFragment f = new CreateJournalDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(todaysDate);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.add_daily_journal_layout, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        truckSpinner = (Spinner) v.findViewById(R.id.truck_spinner);
        truckArray = v.getResources().getStringArray(R.array.truck_number);

        dStartTime = (Button) v.findViewById(R.id.driver_start_time);
        dStartTime.setTransformationMethod(null);
        nStartTime = (Button) v.findViewById(R.id.nav_start_time);
        nStartTime.setTransformationMethod(null);

        View cancelSaveLayout = v.findViewById(R.id.journal_cancel_save_button_bar);
        createJournal = (Button) cancelSaveLayout.findViewById(R.id.btn_save);
        createJournal.setText("CREATE");
        cancel = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        journalExists = true;

        etDriver = (EditText) v.findViewById(R.id.et_driver);
        etNavigator = (EditText) v.findViewById(R.id.et_navigator);

        truckSpinner.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, truckArray));
        truckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

//                journalExists = true;

                truckSelected = truckArray[position];

                currentJournalString = App.FIREBASE_URL + "journals/" +
                        currentYear + "/" + currentMonth + "/" + currentDay + "/T" + truckSelected + "/";

                currentJournalRef = new Firebase(currentJournalString);

                currentJournalRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            errorText = (TextView) truckSpinner.getSelectedView();
                            errorText.setError("");
                            journalExists = true;

                        } else if (!snapshot.exists()) {

                            errorText = (TextView) truckSpinner.getSelectedView();
                            errorText.setError(null);
                            journalExists = false;

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        dStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog(getActivity(), dStartTime, "Driver").show();
            }
        });

        nStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog(getActivity(), nStartTime, "Navigator").show();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        createJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!journalExists
                        && !TextUtils.isEmpty(etDriver.getText())
                        && !dStartTime.getText().toString().equals("Start")) {

                    createJournal();


                } else if (journalExists) {
                    Toast.makeText(getActivity(), "A journal for this truck already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please enter a driver", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setCancelable(false);

        return v;

    }

    private void createJournal() {

        pDialog = ProgressDialog.show(getActivity(), null,
                "Creating journal...", true);

        final String driverString = etDriver.getText().toString();
        final String navigatorString = etNavigator.getText().toString();
        final String driverST = dStartTime.getText().toString();
        final String navST = nStartTime.getText().toString();

//                    fbrJournal.child("journalAuthor").setValue(auth.getCurrentUser().getEmail());

        DailyJournalObject journal = new DailyJournalObject();

        journal.setDate(todaysDate);
        journal.setDriver(driverString);
        journal.setDriverStartTime(driverST);
        journal.setNavigator(navigatorString);
        journal.setNavStartTime(navST);
        journal.setTruckNumber(truckSelected);

        Firebase firebaseRef = new Firebase(currentJournalString + "info");

        firebaseRef.setValue(journal, new Firebase.CompletionListener() {

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                if (firebaseError != null) {

                    System.out.println("Data could not be saved. " + firebaseError.getMessage());

                } else {

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(getString(R.string.sp_current_journal_ref), currentJournalString);
                    editor.putString("driver", driverString);
                    editor.putString("navigator", navigatorString);
                    editor.apply();

                    ((TabsActivity) getActivity()).notifyJournalChanged();

                    pDialog.dismiss();
                    dismissAllowingStateLoss();

                }
            }
        });

    }
}