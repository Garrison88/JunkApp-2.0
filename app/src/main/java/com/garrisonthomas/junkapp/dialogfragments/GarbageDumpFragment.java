package com.garrisonthomas.junkapp.dialogfragments;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.entryobjects.DumpObject;
import com.garrisonthomas.junkapp.entryobjects.TransferStationObject;
import com.garrisonthomas.junkapp.inputFilters.InputFilterMinMax;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.garrisonthomas.junkapp.BaseActivity.preferences;

public class GarbageDumpFragment extends DialogFragmentHelper {

    private TextInputEditText etAddDumpWeight, etDumpReceiptNumber, etPercentPrevious, etEditCost;
    private TextInputLayout enterWeightWrapper, enterReceiptNumberWrapper;
    private CheckBox checkBoxAfterHours;
    private TextView tvGrossCost;
    private ImageButton btnEditCost;
    private Spinner dumpNameSpinner;
    private double pricePerTonne, result;
    private String dumpNameString, currentJournalRef;
    private boolean costIsEditable;
    private LinearLayout dumpCostLayout, afterHoursLayout;

    private ArrayList<TransferStationObject> transferStationObjectArrayList;
    private ArrayList<String> dumpNameArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.add_garbage_dump_layout, container, false);

        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);

        enterWeightWrapper = (TextInputLayout) v.findViewById(R.id.enter_weight_wrapper);
        etAddDumpWeight = (TextInputEditText) enterWeightWrapper.getEditText();
        enterReceiptNumberWrapper = (TextInputLayout) v.findViewById(R.id.enter_receipt_number_wrapper);
        etDumpReceiptNumber = (TextInputEditText) enterReceiptNumberWrapper.getEditText();
        TextInputLayout enterPercentPreviousWrapper = (TextInputLayout) v.findViewById(R.id.enter_percent_previous_wrapper);
        etPercentPrevious = (TextInputEditText) enterPercentPreviousWrapper.getEditText();
        etPercentPrevious.setFilters(new InputFilter[]{new InputFilterMinMax(1, 100)});
        TextInputLayout editWeightWrapper = (TextInputLayout) v.findViewById(R.id.edit_dump_cost_wrapper);
        etEditCost = (TextInputEditText) editWeightWrapper.getEditText();

        tvGrossCost = (TextView) v.findViewById(R.id.tv_dump_gross_cost);

        checkBoxAfterHours = (CheckBox) v.findViewById(R.id.check_box_after_hours);

        View cancelSaveLayout = v.findViewById(R.id.garbage_cancel_save_button_bar);

        Button saveDump = (Button) cancelSaveLayout.findViewById(R.id.btn_save),
                cancelDump = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        btnEditCost = (ImageButton) v.findViewById(R.id.btn_edit_dump_cost);

        dumpNameSpinner = (Spinner) v.findViewById(R.id.spinner_dump_dialog);

        dumpCostLayout = (LinearLayout) v.findViewById(R.id.dump_cost_layout);
        afterHoursLayout = (LinearLayout) v.findViewById(R.id.linear_layout_after_hours);

        transferStationObjectArrayList = new ArrayList<>();
        dumpNameArray = new ArrayList<>();

        FirebaseDatabase
                .getInstance()
                .getReference("dumpInfo")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        transferStationObjectArrayList.add(dataSnapshot.getValue(TransferStationObject.class));

                        dumpNameArray.add(dataSnapshot.getValue(TransferStationObject.class).getName());

                        dumpNameSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_item, dumpNameArray));

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        checkBoxAfterHours.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                etAddDumpWeight.setText("");
            }
        });

        dumpNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                etAddDumpWeight.setText("");
                etEditCost.setText("");
                costIsEditable = false;
                dumpNameString = transferStationObjectArrayList.get(position).getName();
                pricePerTonne = transferStationObjectArrayList.get(position).getRate();

                checkBoxAfterHours.setChecked(false);

                afterHoursLayout.setVisibility(dumpNameString.equals("Tor Can")
                        ? View.VISIBLE
                        : View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        etAddDumpWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String input = etAddDumpWeight.getText().toString();

                if (!input.equals("")
                        && !input.equals(".")) {

                    double weightInTonnes = Double.valueOf(input);

                    int minimum = transferStationObjectArrayList.get(dumpNameSpinner.getSelectedItemPosition()).getMinimum();

                    result = (checkBoxAfterHours.isChecked() && dumpNameString.equals("Tor Can")
                            ? DialogFragmentHelper.calculateDump(pricePerTonne, weightInTonnes, minimum) + 25
                            : DialogFragmentHelper.calculateDump(pricePerTonne, weightInTonnes, minimum));

                    dumpCostLayout.setVisibility(View.VISIBLE);
                    tvGrossCost.setText(currencyFormat.format(result));

                    etEditCost.setHint(currencyFormat.format(result));

                } else {
                    dumpCostLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tonnageString = etAddDumpWeight.getText().toString(),
                        receiptNumberString = etDumpReceiptNumber.getText().toString();

                if (!tonnageString.equals("")
                        && !tonnageString.equals(".")
                        && !receiptNumberString.equals("")) {

                    DumpObject dump = new DumpObject();

                    dump.setDumpName(dumpNameString);
                    dump.setTonnage(Double.valueOf(tonnageString));
                    dump.setDumpReceiptNumber(Integer.valueOf(receiptNumberString));

                    dump.setGrossCost(!TextUtils.isEmpty(etEditCost.getText())
                            ? Double.valueOf(etEditCost.getText().toString())
                            : result);

                    dump.setPercentPrevious(!TextUtils.isEmpty(etPercentPrevious.getText())
                            ? Integer.valueOf(String.valueOf(etPercentPrevious.getText()))
                            : 0);

                    FirebaseDatabase
                            .getInstance()
                            .getReference(currentJournalRef + "dumps/" + dumpNameString + " (" +
                                    receiptNumberString + ")")
                            .setValue(dump);

                    Toast.makeText(getActivity(), "Dump at " + dumpNameString + " saved", Toast.LENGTH_SHORT).show();

                    getActivity().getSupportFragmentManager().beginTransaction().remove(getParentFragment()).commit();

                } else {

                    if (tonnageString.equals("")
                            || tonnageString.equals(".")) {
                        enterWeightWrapper.setErrorEnabled(true);
                        enterWeightWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        enterWeightWrapper.setErrorEnabled(false);
                    }
                    if (receiptNumberString.equals("")) {
                        enterReceiptNumberWrapper.setErrorEnabled(true);
                        enterReceiptNumberWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        enterReceiptNumberWrapper.setErrorEnabled(false);
                    }

                }
            }
        });

        cancelDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(getParentFragment()).commit();
            }
        });

        btnEditCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!costIsEditable) {
                    etEditCost.setText("");
                    tvGrossCost.setVisibility(View.GONE);
                    etEditCost.setVisibility(View.VISIBLE);
                    etEditCost.setHint(tvGrossCost.getText().toString());
                    etEditCost.requestFocus();
                    btnEditCost.setImageResource(R.drawable.ic_close_white_24dp);
                    costIsEditable = true;
                } else {
                    etEditCost.setText("");
                    tvGrossCost.setVisibility(View.VISIBLE);
                    etEditCost.setVisibility(View.GONE);
                    btnEditCost.setImageResource(R.drawable.ic_edit_white_24dp);
                    costIsEditable = false;
                }
            }
        });

        return v;

    }
}
