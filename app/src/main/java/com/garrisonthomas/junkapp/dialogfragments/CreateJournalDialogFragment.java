package com.garrisonthomas.junkapp.dialogfragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.garrisonthomas.junkapp.BaseActivity.firebaseConnected;
import static com.garrisonthomas.junkapp.BaseActivity.preferences;

public class CreateJournalDialogFragment extends DialogFragmentHelper {

    private Spinner truckSpinner;
    private String[] truckArray;
    private Button dStartTime, nStartTime;
    private EditText etDriver, etNavigator;
    private String truckSelected, currentJournalString;
    private boolean journalExists;
    private TextView errorText;

    private Date date = new Date();
    private String currentYear = new SimpleDateFormat("yyyy", Locale.CANADA).format(date);
    private String currentMonth = new SimpleDateFormat("MMMM", Locale.CANADA).format(date);
    private String currentDay = new SimpleDateFormat("dd", Locale.CANADA).format(date);
    private String todaysDate = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.CANADA).format(date);

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

        truckSpinner = v.findViewById(R.id.truck_spinner);
        truckArray = v.getResources().getStringArray(R.array.truck_number);

        dStartTime = v.findViewById(R.id.driver_start_time);
        dStartTime.setTransformationMethod(null);
        nStartTime = v.findViewById(R.id.nav_start_time);
        nStartTime.setTransformationMethod(null);

        View cancelSaveLayout = v.findViewById(R.id.journal_cancel_save_button_bar);

        Button createJournal = cancelSaveLayout.findViewById(R.id.btn_save),
                cancel = cancelSaveLayout.findViewById(R.id.btn_cancel);
        createJournal.setText("CREATE");

        journalExists = true;

        etDriver = v.findViewById(R.id.et_driver);
        etNavigator = v.findViewById(R.id.et_navigator);

        truckSpinner.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, truckArray));
        truckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                final ProgressDialog pDialog = ProgressDialog.show(getActivity(), null,
                        "Checking availability...", true);

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

                                    pDialog.dismiss();

                                } else if (!snapshot.exists()) {

                                    errorText = (TextView) truckSpinner.getSelectedView();
                                    errorText.setError(null);
                                    journalExists = false;

                                    pDialog.dismiss();

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
                        && firebaseConnected
                        && !TextUtils.isEmpty(etDriver.getText())
                        && !dStartTime.getText().toString().equals("Start")) {

                    createJournal();


                } else if (journalExists) {
                    Toast.makeText(getActivity(), "A journal for this truck already exists", Toast.LENGTH_SHORT).show();
                } else if (!firebaseConnected) {
                    Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please enter a driver", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setCancelable(false);

        return v;

    }

    private void createJournal() {

        final ProgressDialog pDialog = ProgressDialog.show(getActivity(), null,
                "Creating journal...", true);

        final String driverString = etDriver.getText().toString();
        final String navigatorString = etNavigator.getText().toString();
        final String driverStartTime = dStartTime.getText().toString();
        final String navStartTime = nStartTime.getText().toString();

//                    fbrJournal.child("journalAuthor").setValue(auth.getCurrentUser().getEmail());

        DailyJournalObject journal = new DailyJournalObject();

        journal.setDate(todaysDate);
        journal.setDriver(driverString);
        journal.setDriverTime(driverStartTime);
        journal.setNavigator(navigatorString);
        journal.setNavTime(navStartTime);
        journal.setTruckNumber(truckSelected);
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
                            editor.putString("driverStartTime", driverStartTime);
                            editor.putString("navigator", navigatorString);
                            editor.putString("navStartTime", navStartTime);
                            editor.apply();

                            ((TabsActivity) getActivity()).notifyJournalChanged();

                            pDialog.dismiss();
                            dismissAllowingStateLoss();

                        }

                    }
                });

    }
}