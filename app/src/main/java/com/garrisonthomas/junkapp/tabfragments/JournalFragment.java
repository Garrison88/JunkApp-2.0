package com.garrisonthomas.junkapp.tabfragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.garrisonthomas.junkapp.DumpTabDialogFragment;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.Utils;
import com.garrisonthomas.junkapp.dialogfragments.AddFuelDialogFragment;
import com.garrisonthomas.junkapp.dialogfragments.AddJobDialogFragment;
import com.garrisonthomas.junkapp.dialogfragments.AddQuoteDialogFragment;
import com.garrisonthomas.junkapp.dialogfragments.CreateJournalDialogFragment;
import com.garrisonthomas.junkapp.dialogfragments.ViewDumpDialogFragment;
import com.garrisonthomas.junkapp.dialogfragments.ViewFuelDialogFragment;
import com.garrisonthomas.junkapp.dialogfragments.ViewJobDialogFragment;
import com.garrisonthomas.junkapp.dialogfragments.ViewQuoteDialogFragment;
import com.garrisonthomas.junkapp.entryobjects.DailyJournalObject;
import com.garrisonthomas.junkapp.entryobjects.DumpObject;
import com.garrisonthomas.junkapp.entryobjects.JobObject;
import com.garrisonthomas.junkapp.entryobjects.RebateObject;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.garrisonthomas.junkapp.BaseActivity.auth;

public class JournalFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Bind(R.id.tv_todays_crew)
    TextView todaysCrew;
    @Bind(R.id.tv_todays_truck)
    TextView todaysTruck;
    @Bind(R.id.floating_action_menu)
    FloatingActionMenu FAM;
    @Bind(R.id.btn_add_job)
    FloatingActionButton addJob;
    @Bind(R.id.btn_view_job)
    Button viewJob;
    @Bind(R.id.btn_add_quote)
    FloatingActionButton addQuote;
    @Bind(R.id.btn_view_quote)
    Button viewQuote;
    @Bind(R.id.btn_add_dump)
    FloatingActionButton addDump;
    @Bind(R.id.btn_view_dump)
    Button viewDump;
    @Bind(R.id.btn_view_fuel)
    Button viewFuel;
    @Bind(R.id.btn_add_fuel)
    FloatingActionButton addFuel;
    @Bind(R.id.btn_create_new_journal)
    Button createJournal;
    @Bind(R.id.jobs_spinner)
    Spinner jobsSpinner;
    @Bind(R.id.quotes_spinner)
    Spinner quotesSpinner;
    @Bind(R.id.dumps_spinner)
    Spinner dumpsSpinner;
    @Bind(R.id.fuel_spinner)
    Spinner fuelSpinner;
    @Bind(R.id.tv_total_income)
    TextView tvTotalIncome;
    @Bind(R.id.tv_dump_cost)
    TextView tvDumpCost;
    @Bind(R.id.curret_journal_relative_layout)
    RelativeLayout currentJournalRelativeLayout;

    private String selectedJobSID, selectedQuoteSID, dumpSpinnerText, fuelReceiptNumber,
            spDriver, spNavigator, currentJournalRef;

    private DatabaseReference infoRef;

    private int dumpReceiptNumber, totalGrossProfit, totalDumpCost;

    private ArrayList<String> jobsArray = new ArrayList<>(), quotesArray = new ArrayList<>(),
            dumpsArray = new ArrayList<>(), fuelArray = new ArrayList<>();

    private NumberFormat currencyFormat;

    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        final View v = inflater.inflate(R.layout.journal_layout, container, false);

        ButterKnife.bind(this, v);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);

        currencyFormat = NumberFormat.getCurrencyInstance();

        if (currentJournalRef != null) {

            infoRef = FirebaseDatabase
                    .getInstance()
                    .getReference(currentJournalRef + "info");

            viewJob.setOnClickListener(this);
            viewQuote.setOnClickListener(this);
            viewDump.setOnClickListener(this);
            viewFuel.setOnClickListener(this);
            addJob.setOnClickListener(this);
            addQuote.setOnClickListener(this);
            addDump.setOnClickListener(this);
            addFuel.setOnClickListener(this);

            jobsSpinner.setOnItemSelectedListener(this);
            quotesSpinner.setOnItemSelectedListener(this);
            dumpsSpinner.setOnItemSelectedListener(this);
            fuelSpinner.setOnItemSelectedListener(this);

            // populate spinners
            Utils.populateEntrySpinner(getActivity(), currentJournalRef + "jobs", jobsArray, jobsSpinner, true);
            Utils.populateEntrySpinner(getActivity(), currentJournalRef + "quotes", quotesArray, quotesSpinner, true);
            Utils.populateEntrySpinner(getActivity(), currentJournalRef + "dumps", dumpsArray, dumpsSpinner, false);
            Utils.populateEntrySpinner(getActivity(), currentJournalRef + "rebate", dumpsArray, dumpsSpinner, false);
            Utils.populateEntrySpinner(getActivity(), currentJournalRef + "fuel", fuelArray, fuelSpinner, false);

            //query grossSale children of SID nodes to find real-time daily profit
            FirebaseDatabase
                    .getInstance()
                    .getReference(currentJournalRef + "jobs")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            JobObject jobObject = dataSnapshot.getValue(JobObject.class);

                            totalGrossProfit += Math.round(jobObject.getGrossSale());

                            updateUI();


                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                            JobObject jobObject = dataSnapshot.getValue(JobObject.class);

                            totalGrossProfit -= Math.round(jobObject.getGrossSale());

                            updateUI();

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            // query dumps to find percentOnDumps
            DatabaseReference dumpsRef = FirebaseDatabase.getInstance().getReference(currentJournalRef + "dumps");
            dumpsRef
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            DumpObject dumpObject = dataSnapshot.getValue(DumpObject.class);

                            totalDumpCost += (dumpObject.getPercentPrevious() != 0)
                                    ? Math.round(dumpObject.getGrossCost() * ((100 - dumpObject.getPercentPrevious()) * 0.01))
                                    : Math.round(dumpObject.getGrossCost());

                            updateUI();


                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                            DumpObject dumpObject = dataSnapshot.getValue(DumpObject.class);

                            totalDumpCost -= (dumpObject.getPercentPrevious() != 0)
                                    ? Math.round(dumpObject.getGrossCost() * ((100 - dumpObject.getPercentPrevious()) * 0.01))
                                    : Math.round(dumpObject.getGrossCost());

                            updateUI();

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });

            // query rebate to find percentOnDumps
            FirebaseDatabase
                    .getInstance()
                    .getReference(currentJournalRef + "rebate")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            RebateObject rebateObject = dataSnapshot.getValue(RebateObject.class);

                            totalDumpCost -= (rebateObject.getPercentPrevious() != 0)
                                    ? Math.round(rebateObject.getRebateAmount() * ((100 - rebateObject.getPercentPrevious()) * 0.01))
                                    : Math.round(rebateObject.getRebateAmount());

                            updateUI();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                            RebateObject rebateObject = dataSnapshot.getValue(RebateObject.class);

                            totalDumpCost += (rebateObject.getPercentPrevious() != 0)
                                    ? Math.round(rebateObject.getRebateAmount() * ((100 - rebateObject.getPercentPrevious()) * 0.01))
                                    : Math.round(rebateObject.getRebateAmount());

                            updateUI();

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });

            //query database to find driver, nav, and truck number and set them
            FirebaseDatabase
                    .getInstance()
                    .getReference(currentJournalRef + "info")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            DailyJournalObject djObject = dataSnapshot.getValue(DailyJournalObject.class);
                            spDriver = djObject.getDriver();
                            spNavigator = djObject.getNavigator();
                            String truckString = "Truck " + djObject.getTruckNumber();
                            String crewString = "Driver: " + spDriver + "\n" + "Nav: " + spNavigator;
                            todaysCrew.setText(crewString);
                            todaysTruck.setText(truckString);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        } else {

            createJournal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (auth.getCurrentUser() == null) {

                        Toast.makeText(getActivity(), "Please login first", Toast.LENGTH_LONG).show();

                    } else {

                        FragmentTransaction ft = getFragmentManager().beginTransaction();

                        Fragment prev = getFragmentManager().findFragmentByTag("fragCreateJournal");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);

                        // Create and show the dialog.
                        DialogFragment newFragment = CreateJournalDialogFragment.newInstance(1);
                        newFragment.show(ft, "fragCreateJournal");

                    }
                }
            });
        }

        return v;

    }

    public void updateUI() {

        currencyFormat.setMinimumFractionDigits(0);

        String jobNumbersString = "Gross Profit: " + currencyFormat.format(totalGrossProfit)
                + POG(totalGrossProfit);

        String dumpNumbersString = "Dump Cost: " + currencyFormat.format(totalDumpCost)
                + POD(totalDumpCost, totalGrossProfit);

        tvTotalIncome.setText(jobNumbersString);
        tvDumpCost.setText(dumpNumbersString);

    }

    public String POG(int TGP) {

        infoRef
                .child("totalGrossProfit")
                .setValue(TGP);

        infoRef
                .child("percentOfGoal")
                .setValue(Math.round(100 * (TGP / 1400f)));

        return (TGP > 1)
                ? " (" + String.valueOf(Math.round(100 * (TGP / 1400f))) + "%)"
                : "";

    }

    public String POD(int TDC, int TGP) {

        infoRef
                .child("totalDumpCost")
                .setValue(TDC);

        infoRef
                .child("percentOnDumps")
                .setValue(TGP > 1 && TDC > 1
                        ? Math.round((100 * (TDC / (float) TGP)))
                        : 0);

        return TGP > 1 && TDC > 1
                ? " (" + String.valueOf(Math.round((100 * (TDC / (float) TGP)))) + "%)"
                : "";

    }

    @Override
    public void onResume() {
        super.onResume();
        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);
        if (currentJournalRef != null) {
            createJournal.setVisibility(View.GONE);
            currentJournalRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            createJournal.setVisibility(View.VISIBLE);
            currentJournalRelativeLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {

        FragmentManager manager = getActivity().getSupportFragmentManager();
        FAM.close(false);

        switch (view.getId()) {

            case R.id.btn_view_job:

                if (!jobsArray.isEmpty()) {

                    ViewJobDialogFragment vjDialogFragment = new ViewJobDialogFragment();

                    if (manager.findFragmentByTag("fragViewJob") != null) {
                        manager.beginTransaction().remove(manager.findFragmentByTag("fragViewJob")).commit();
                    }

                    Bundle vjBundle = new Bundle();
                    vjBundle.putString("jobSpinnerSID", selectedJobSID);
                    vjBundle.putString(getString(R.string.sp_current_journal_ref), currentJournalRef);
                    vjDialogFragment.setArguments(vjBundle);
                    vjDialogFragment.show(manager, "fragViewJob");

                }

                break;

            case R.id.btn_view_quote:

                if (!quotesArray.isEmpty()) {

                    ViewQuoteDialogFragment vqDialogFragment = new ViewQuoteDialogFragment();

                    if (manager.findFragmentByTag("fragViewQuote") != null) {
                        manager.beginTransaction().remove(manager.findFragmentByTag("fragViewQuote")).commit();
                    }

                    Bundle vqBundle = new Bundle();
                    vqBundle.putString("quoteSpinnerSID", selectedQuoteSID);
                    vqBundle.putString(getString(R.string.sp_current_journal_ref), currentJournalRef);
                    vqDialogFragment.setArguments(vqBundle);
                    vqDialogFragment.show(manager, "fragViewQuote");

                }

                break;

            case R.id.btn_view_dump:

                if (!dumpsArray.isEmpty()) {

                    ViewDumpDialogFragment vdDialogFragment = new ViewDumpDialogFragment();

                    if (manager.findFragmentByTag("fragViewDump") != null) {
                        manager.beginTransaction().remove(manager.findFragmentByTag("fragViewDump")).commit();
                    }

                    Bundle vdBundle = new Bundle();
                    vdBundle.putString("dumpName", dumpSpinnerText);
                    vdBundle.putInt("dumpReceiptNumber", dumpReceiptNumber);
                    vdBundle.putString(getString(R.string.sp_current_journal_ref), currentJournalRef);
                    vdDialogFragment.setArguments(vdBundle);
                    vdDialogFragment.show(manager, "fragViewDump");

                }

                break;

            case R.id.btn_view_fuel:

                if (!fuelArray.isEmpty()) {

                    ViewFuelDialogFragment vfDialogFragment = new ViewFuelDialogFragment();

                    if (manager.findFragmentByTag("fragViewFuel") != null) {
                        manager.beginTransaction().remove(manager.findFragmentByTag("fragViewFuel")).commit();
                    }

                    Bundle vfBundle = new Bundle();
                    vfBundle.putString("fuelReceiptNumber", fuelReceiptNumber);
                    vfBundle.putString(getString(R.string.sp_current_journal_ref), currentJournalRef);
                    vfDialogFragment.setArguments(vfBundle);

                    vfDialogFragment.show(manager, "fragViewFuel");

                }

                break;

            case R.id.btn_add_job:

                AddJobDialogFragment ajFragment = new AddJobDialogFragment();

                if (manager.findFragmentByTag("fragAddJob") != null) {
                    manager.beginTransaction().remove(manager.findFragmentByTag("fragAddJob")).commit();
                }

                ajFragment.show(manager, "fragAddJob");


                break;
            case R.id.btn_add_quote:

                AddQuoteDialogFragment aqFragment = new AddQuoteDialogFragment();

                if (manager.findFragmentByTag("fragAddQuote") != null) {
                    manager.beginTransaction().remove(manager.findFragmentByTag("fragAddQuote")).commit();
                }

                aqFragment.show(manager, "fragAddQuote");

                break;
            case R.id.btn_add_dump:

                DumpTabDialogFragment dumpDialog = new DumpTabDialogFragment();

                if (manager.findFragmentByTag("fragAddDump") != null) {
                    manager.beginTransaction().remove(manager.findFragmentByTag("fragAddDump")).commit();
                }

                dumpDialog.show(manager, "fragAddDump");

                break;
            case R.id.btn_add_fuel:

                AddFuelDialogFragment afFragment = new AddFuelDialogFragment();

                if (manager.findFragmentByTag("fragAddFuel") != null) {
                    manager.beginTransaction().remove(manager.findFragmentByTag("fragAddFuel")).commit();
                }

                afFragment.show(manager, "fragAddFuel");

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()) {

            case R.id.jobs_spinner:

                selectedJobSID = (String) jobsSpinner.getItemAtPosition(i);

                break;

            case R.id.quotes_spinner:

                selectedQuoteSID = String.valueOf(quotesSpinner.getItemAtPosition(i));

                break;

            case R.id.dumps_spinner:

                dumpSpinnerText = (String) dumpsSpinner.getItemAtPosition(i);

                Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(dumpSpinnerText);
                while (m.find()) {
                    dumpReceiptNumber = Integer.parseInt(m.group(1));
                }

                break;

            case R.id.fuel_spinner:

                fuelReceiptNumber = (String) fuelSpinner.getItemAtPosition(i);

                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//        super.onSaveInstanceState(outState);
//        outState.putString("tab", "JournalFragment"); //save the tab selected
//
//    }

}