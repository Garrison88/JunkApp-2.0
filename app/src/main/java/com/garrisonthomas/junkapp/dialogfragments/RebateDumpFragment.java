package com.garrisonthomas.junkapp.dialogfragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.entryobjects.RebateObject;
import com.garrisonthomas.junkapp.inputFilters.InputFilterMinMax;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Garrison on 2016-07-04.
 */
public class RebateDumpFragment extends Fragment {

    private TextInputEditText etRebateTonnage, etRebateAmount, etRebateReceiptNumber, etPercentPrevious;
    private TextInputLayout enterTonnageWrapper, enterAmountWrapper, enterReceiptNumberWrapper,
            enterPercentPreviousWrapper;
    private String[] materialTypeArray, rebateLocationArray;
    private double tonnageMultiplier;
    private String materialTypeString, rebateLocationString, firebaseJournalRef;

    private int mPage;

    public static final String ARG_PAGE = "ARG_PAGE";

    public static RebateDumpFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RebateDumpFragment fragment = new RebateDumpFragment();
        fragment.setArguments(args);
        return fragment;
//        return args.getView(page).constructFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.add_rebate_dump_layout, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        firebaseJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);

        enterTonnageWrapper = (TextInputLayout) v.findViewById(R.id.enter_rebate_tonnage_wrapper);
        etRebateTonnage = (TextInputEditText) enterTonnageWrapper.getEditText();
        enterAmountWrapper = (TextInputLayout) v.findViewById(R.id.enter_rebate_amount_wrapper);
        etRebateAmount = (TextInputEditText) enterAmountWrapper.getEditText();
        enterReceiptNumberWrapper = (TextInputLayout) v.findViewById(R.id.rebate_receipt_number_wrapper);
        etRebateReceiptNumber = (TextInputEditText) enterReceiptNumberWrapper.getEditText();
        enterPercentPreviousWrapper = (TextInputLayout) v.findViewById(R.id.rebate_percent_previous_wrapper);
        etPercentPrevious = (TextInputEditText) enterPercentPreviousWrapper.getEditText();
        etPercentPrevious.setFilters(new InputFilter[]{new InputFilterMinMax("1", "100")});

        View cancelSaveLayout = v.findViewById(R.id.rebate_cancel_save_button_bar);
        Button saveRebate = (Button) cancelSaveLayout.findViewById(R.id.btn_save);
        Button cancelRebate = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        materialTypeArray = getResources().getStringArray(R.array.material);
        rebateLocationArray = getResources().getStringArray(R.array.rebate_location);

        Spinner materialTypeSpinner = (Spinner) v.findViewById(R.id.spinner_material_type);
        Spinner rebateLocationSpinner = (Spinner) v.findViewById(R.id.spinner_rebate_location);
        Spinner weightUnitSpinner = (Spinner) v.findViewById(R.id.spinner_weight_unit);

        materialTypeSpinner.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, materialTypeArray));

        rebateLocationSpinner.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, rebateLocationArray));

        List<String> weightUnitArray = new ArrayList<>();
        weightUnitArray.add("kg");
        weightUnitArray.add("lb");
        weightUnitArray.add("t");

        weightUnitSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, weightUnitArray));

        materialTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                materialTypeString = materialTypeArray[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        rebateLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                rebateLocationString = rebateLocationArray[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        weightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                etRebateTonnage.setText("");

                switch (position) {
                    // weight is in kg
                    case 0:
                        tonnageMultiplier = 0.001;
                        etRebateTonnage.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    // weight is in lb
                    case 1:
                        tonnageMultiplier = 0.000453592;
                        etRebateTonnage.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    // weight is in tonnes
                    case 2:
                        tonnageMultiplier = 1;
                        etRebateTonnage.setInputType(InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        saveRebate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((!TextUtils.isEmpty(etRebateTonnage.getText())
                        && (!TextUtils.isEmpty(etRebateAmount.getText())))
                        && (!TextUtils.isEmpty(etRebateReceiptNumber.getText()))) {

                    Firebase fbrRebate = new Firebase(firebaseJournalRef + "rebate/" + rebateLocationString + " (" +
                            String.valueOf(etRebateReceiptNumber.getText()) + ")");

                    RebateObject rebate = new RebateObject();

                    rebate.setRebateLocation(rebateLocationString);
                    rebate.setRebateAmount(Integer.valueOf(String.valueOf(etRebateAmount.getText())));
                    rebate.setReceiptNumber(Integer.valueOf(String.valueOf(etRebateReceiptNumber.getText())));
                    rebate.setRebateTonnage(Math.round((tonnageMultiplier
                            * Double.valueOf(String.valueOf(etRebateTonnage.getText()))) * 100.00) / 100.00);
                    rebate.setMaterialType(materialTypeString);
                    rebate.setPercentPrevious(!TextUtils.isEmpty(etPercentPrevious.getText())
                            ? Integer.valueOf(String.valueOf(etPercentPrevious.getText()))
                            : 0);

                    fbrRebate.setValue(rebate);

                    Toast.makeText(getActivity(), "Rebate from " + rebateLocationString + " saved", Toast.LENGTH_SHORT).show();

                    getActivity().getSupportFragmentManager().beginTransaction().remove(getParentFragment()).commit();

                } else {

                    if (TextUtils.isEmpty(etRebateTonnage.getText())) {
                        enterTonnageWrapper.setErrorEnabled(true);
                        enterTonnageWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        enterTonnageWrapper.setErrorEnabled(false);
                    }
                    if (TextUtils.isEmpty(etRebateAmount.getText())) {
                        enterAmountWrapper.setErrorEnabled(true);
                        enterAmountWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        enterAmountWrapper.setErrorEnabled(false);
                    }

                }
            }
        });

        cancelRebate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(getParentFragment()).commit();
            }
        });

        return v;

    }

}
