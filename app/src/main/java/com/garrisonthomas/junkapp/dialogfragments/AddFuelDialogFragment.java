package com.garrisonthomas.junkapp.dialogfragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.entryobjects.FuelObject;
import com.google.firebase.database.FirebaseDatabase;

import static com.garrisonthomas.junkapp.BaseActivity.preferences;

public class AddFuelDialogFragment extends DialogFragmentHelper {

    private TextInputEditText etFuelVendor, etFuelCost, etReceiptNumber;
    private TextInputLayout fuelVendorWrapper, fuelCostWrapper, receiptNumberWrapper;
    private String currentJournalRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.add_fuel_layout, container, false);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);

        fuelVendorWrapper = (TextInputLayout) v.findViewById(R.id.et_fuel_vendor_wrapper);
        etFuelVendor = (TextInputEditText) fuelVendorWrapper.getEditText();
        fuelCostWrapper = (TextInputLayout) v.findViewById(R.id.et_fuel_cost_wrapper);
        etFuelCost = (TextInputEditText) fuelCostWrapper.getEditText();
        receiptNumberWrapper = (TextInputLayout) v.findViewById(R.id.et_fuel_receipt_number_wrapper);
        etReceiptNumber = (TextInputEditText) receiptNumberWrapper.getEditText();

        etReceiptNumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        View cancelSaveLayout = v.findViewById(R.id.fuel_cancel_save_button_bar);
        Button saveFuel = (Button) cancelSaveLayout.findViewById(R.id.btn_save);
        Button cancelFuel = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        saveFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(etFuelVendor.getText())
                        && !TextUtils.isEmpty(etFuelCost.getText())
                        && !TextUtils.isEmpty(etReceiptNumber.getText())) {

                    FuelObject fuel = new FuelObject();

                    fuel.setFuelVendor(String.valueOf(etFuelVendor.getText()));
                    fuel.setFuelCost(Double.valueOf(String.valueOf(etFuelCost.getText())));
                    fuel.setFuelReceiptNumber(String.valueOf(etReceiptNumber.getText()));

                    FirebaseDatabase
                            .getInstance()
                            .getReference(currentJournalRef + "fuel/"
                                    + String.valueOf(etReceiptNumber.getText()))
                            .setValue(fuel);

                    Toast.makeText(getActivity(), "Fuel entry at " + String.valueOf(etFuelVendor.getText()) +
                            " saved", Toast.LENGTH_SHORT).show();

                    dismiss();

                } else {

                    if (TextUtils.isEmpty(etFuelVendor.getText())) {
                        fuelVendorWrapper.setErrorEnabled(true);
                        fuelVendorWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        fuelVendorWrapper.setErrorEnabled(false);
                    }
                    if (TextUtils.isEmpty(etFuelCost.getText())) {
                        fuelCostWrapper.setErrorEnabled(true);
                        fuelCostWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        fuelCostWrapper.setErrorEnabled(false);
                    }
                    if (TextUtils.isEmpty(etReceiptNumber.getText())) {
                        receiptNumberWrapper.setErrorEnabled(true);
                        receiptNumberWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        receiptNumberWrapper.setErrorEnabled(false);
                    }

                }
            }
        });

        cancelFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        setCancelable(false);
        return v;

    }
}
