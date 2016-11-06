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

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.TabsActivity;
import com.garrisonthomas.junkapp.entryobjects.DailyJournalObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateJournalDialogFragment extends DialogFragmentHelper {

    private Spinner truckSpinner;
    private String[] truckArray;
    private Button dStartTime, nStartTime;
    private EditText etDriver, etNavigator;
    private String truckSelected, currentJournalString;
    private SharedPreferences preferences;
    private ProgressDialog pDialog;
    private DatabaseReference currentJournalRef;
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

//        errorText = (TextView) truckSpinner.getSelectedView();
//        errorText.setError("");

        dStartTime = (Button) v.findViewById(R.id.driver_start_time);
        dStartTime.setTransformationMethod(null);
        nStartTime = (Button) v.findViewById(R.id.nav_start_time);
        nStartTime.setTransformationMethod(null);

        View cancelSaveLayout = v.findViewById(R.id.journal_cancel_save_button_bar);
        Button createJournal = (Button) cancelSaveLayout.findViewById(R.id.btn_save);
        createJournal.setText("CREATE");
        Button cancel = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        journalExists = true;

        etDriver = (EditText) v.findViewById(R.id.et_driver);
        etNavigator = (EditText) v.findViewById(R.id.et_navigator);

//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    System.out.println("connected");
//                } else {
//                    System.out.println("not connected");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                System.err.println("Listener was cancelled");
//            }
//        });

        truckSpinner.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, truckArray));
        truckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                truckSelected = truckArray[position];

                currentJournalString = "journals/" +
                        currentYear + "/" + currentMonth + "/" + currentDay + "/T" + truckSelected + "/";

                FirebaseDatabase
                        .getInstance()
                        .getReference(currentJournalString)
                        .addValueEventListener(new ValueEventListener() {
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
                            public void onCancelled(DatabaseError firebaseError) {

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

//        DatabaseReference firebaseRef = ;

        FirebaseDatabase
                .getInstance()
                .getReference(currentJournalString + "info")
                .setValue(journal, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError != null) {

                            System.out.println("Data could not be saved. " + databaseError.getMessage());

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