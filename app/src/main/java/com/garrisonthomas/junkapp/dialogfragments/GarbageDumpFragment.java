package com.garrisonthomas.junkapp.dialogfragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.TabsActivity;
import com.garrisonthomas.junkapp.entryobjects.DumpObject;
import com.garrisonthomas.junkapp.entryobjects.TransferStationObject;
import com.garrisonthomas.junkapp.inputFilters.InputFilterMinMax;

import java.text.NumberFormat;
import java.util.ArrayList;

public class GarbageDumpFragment extends DialogFragmentHelper {

    private TextInputEditText etAddDumpWeight, etDumpReceiptNumber, etPercentPrevious, etEditCost;
    private TextView tvGrossCost;
    private ImageButton btnEditCost;
    private Spinner dumpNameSpinner;
    private double pricePerTonne;
    private double result;
    private String dumpNameString, currentJournalRef;
    private boolean costIsEditable;
    private LinearLayout dumpCostLayout;
    private NumberFormat currencyFormat;

    private ArrayList<TransferStationObject> transferStationObjectArrayList;
    private ArrayList<String> dumpNameArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.add_garbage_dump_layout, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);

        TextInputLayout enterWeightWrapper = (TextInputLayout) v.findViewById(R.id.enter_weight_wrapper);
        etAddDumpWeight = (TextInputEditText) enterWeightWrapper.getEditText();
        TextInputLayout enterReceiptNumberWrapper = (TextInputLayout) v.findViewById(R.id.enter_receipt_number_wrapper);
        etDumpReceiptNumber = (TextInputEditText) enterReceiptNumberWrapper.getEditText();
        TextInputLayout enterPercentPreviousWrapper = (TextInputLayout) v.findViewById(R.id.enter_percent_previous_wrapper);
        etPercentPrevious = (TextInputEditText) enterPercentPreviousWrapper.getEditText();
        etPercentPrevious.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});
        TextInputLayout editWeightWrapper = (TextInputLayout) v.findViewById(R.id.edit_dump_cost_wrapper);
        etEditCost = (TextInputEditText) editWeightWrapper.getEditText();

        currencyFormat = NumberFormat.getCurrencyInstance();

        tvGrossCost = (TextView) v.findViewById(R.id.tv_dump_gross_cost);

        View cancelSaveLayout = v.findViewById(R.id.garbage_cancel_save_button_bar);
        Button saveDump = (Button) cancelSaveLayout.findViewById(R.id.btn_save);
        Button cancelDump = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        btnEditCost = (ImageButton) v.findViewById(R.id.btn_edit_dump_cost);

        dumpNameSpinner = (Spinner) v.findViewById(R.id.spinner_dump_dialog);

        dumpCostLayout = (LinearLayout) v.findViewById(R.id.dump_cost_layout);

        transferStationObjectArrayList = TabsActivity.getTransferStationArrayList();

        for (TransferStationObject x : transferStationObjectArrayList) {

            dumpNameArray.add(x.getName());

        }

        dumpNameSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, dumpNameArray));

        dumpNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                dumpNameString = transferStationObjectArrayList.get(position).getName();
                pricePerTonne = transferStationObjectArrayList.get(position).getRate();

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

                    result = DialogFragmentHelper.calculateDump(pricePerTonne, weightInTonnes, minimum);

                    dumpCostLayout.setVisibility(View.VISIBLE);
                    tvGrossCost.setText(currencyFormat.format(result));

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

                if (!TextUtils.isEmpty(etAddDumpWeight.getText())
                        && !etAddDumpWeight.getText().toString().equals(".")
                        && (!TextUtils.isEmpty(etDumpReceiptNumber.getText()))) {

                    Firebase fbrDump = new Firebase(currentJournalRef + "dumps/" + dumpNameString + " (" +
                            String.valueOf(etDumpReceiptNumber.getText()) + ")");

                    DumpObject dump = new DumpObject();

                    dump.setDumpName(dumpNameString);
                    dump.setTonnage(Double.valueOf(etAddDumpWeight.getText().toString()));
                    dump.setDumpReceiptNumber(Integer.valueOf(etDumpReceiptNumber.getText().toString()));

                    dump.setGrossCost(!TextUtils.isEmpty(etEditCost.getText())
                            ? Double.valueOf(etEditCost.getText().toString())
                            : result);

                    dump.setPercentPrevious(!TextUtils.isEmpty(etPercentPrevious.getText())
                            ? Integer.valueOf(String.valueOf(etPercentPrevious.getText()))
                            : 0);

                    fbrDump.setValue(dump);

                    Toast.makeText(getActivity(), "Dump at " + dumpNameString + " saved", Toast.LENGTH_SHORT).show();

                    getActivity().getSupportFragmentManager().beginTransaction().remove(getParentFragment()).commit();

                } else {

                    Toast.makeText(getActivity(), "Please fill all required fields", Toast.LENGTH_SHORT).show();

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
