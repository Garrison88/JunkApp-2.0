package com.garrisonthomas.junkapp.dialogfragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.Utils;
import com.garrisonthomas.junkapp.entryobjects.JobObject;
import com.garrisonthomas.junkapp.inputFilters.CustomTextWatcher;
import com.google.firebase.database.FirebaseDatabase;

import info.hoang8f.android.segmented.SegmentedGroup;

import static com.garrisonthomas.junkapp.BaseActivity.preferences;

public class AddJobDialogFragment extends DialogFragmentHelper {

    private TextInputEditText etSID, etGrossSale, etNetSale, etReceiptNumber, etJobNotes;
    private TextInputLayout enterSIDWrapper, enterGrossSaleWrapper, enterNetSaleWrapper,
            enterReceiptNumberWrapper, enterJobNotesWrapper;
    private Button startTime, endTime;
    private Spinner payTypeSpinner;
    private RadioButton commButton, resButton, cancellationButton;
    private String[] payTypeArray;
    private String payTypeString;
    private String currentJournalRef;
    private Long creditCardNumber;
    private Integer creditCardExpDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.add_job_layout, container, false);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        currentJournalRef = preferences.getString(getString(R.string.sp_current_journal_ref), null);

        enterSIDWrapper = (TextInputLayout) v.findViewById(R.id.enter_sid_wrapper);
        etSID = (TextInputEditText) enterSIDWrapper.getEditText();
        etSID.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
        etSID.addTextChangedListener(new CustomTextWatcher(5, 2, '-'));

        enterGrossSaleWrapper = (TextInputLayout) v.findViewById(R.id.enter_gross_sale_wrapper);
        etGrossSale = (TextInputEditText) enterGrossSaleWrapper.getEditText();

        enterNetSaleWrapper = (TextInputLayout) v.findViewById(R.id.enter_net_sale_wrapper);
        etNetSale = (TextInputEditText) enterNetSaleWrapper.getEditText();

        enterReceiptNumberWrapper = (TextInputLayout) v.findViewById(R.id.enter_receipt_number_wrapper);
        etReceiptNumber = (TextInputEditText) enterReceiptNumberWrapper.getEditText();

        enterJobNotesWrapper = (TextInputLayout) v.findViewById(R.id.enter_job_notes_wrapper);
        enterJobNotesWrapper.setHint(getString(R.string.add_job_notes_hint));
        etJobNotes = (TextInputEditText) enterJobNotesWrapper.getEditText();

        resButton = (RadioButton) v.findViewById(R.id.switch_residential);
        commButton = (RadioButton) v.findViewById(R.id.switch_commercial);
        cancellationButton = (RadioButton) v.findViewById(R.id.switch_cancellation);

        payTypeArray = getResources().getStringArray(R.array.job_pay_type);

        payTypeSpinner = (Spinner) v.findViewById(R.id.spinner_pay_type);

        startTime = (Button) v.findViewById(R.id.job_start_time);
        endTime = (Button) v.findViewById(R.id.job_end_time);

        View cancelSaveLayout = v.findViewById(R.id.job_cancel_save_button_bar);

        Button saveJob = (Button) cancelSaveLayout.findViewById(R.id.btn_save),
                cancelJob = (Button) cancelSaveLayout.findViewById(R.id.btn_cancel);

        // handle setting of jobType to "Cancellation" by auto-filling receiptNumber, gross, and net sale
        // and setting payType to "Cancellation"
        SegmentedGroup segmentedGroup = (SegmentedGroup) v.findViewById(R.id.job_type_segmented_group);
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                etReceiptNumber.setEnabled(checkedId != R.id.switch_cancellation);
                etGrossSale.setEnabled(checkedId != R.id.switch_cancellation);
                etNetSale.setEnabled(checkedId != R.id.switch_cancellation);
                payTypeSpinner.setEnabled(checkedId != R.id.switch_cancellation);
                enterJobNotesWrapper.setHint(checkedId == R.id.switch_cancellation ? "Reason" : getString(R.string.add_job_notes_hint));

            }
        });

        payTypeSpinner.setAdapter(new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, payTypeArray));

        payTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                payTypeString = payTypeArray[position];

                if (position == 3 || position == 4) {
                    showCreditCardAlertDialog(payTypeString);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        etGrossSale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!TextUtils.isEmpty(etGrossSale.getText())) {

                    final double doubleValue = Double.valueOf(etGrossSale.getText().toString());

                    etNetSale.setText(String.valueOf(Utils.calculateTax(doubleValue)));

                } else {

                    etNetSale.setText("");

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog(getActivity(), startTime, "Start").show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimePickerDialog(getActivity(), endTime, "End").show();
            }
        });

        saveJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateEditTextLength(etSID, 5, 8)
                        && (!TextUtils.isEmpty(etGrossSale.getText()) || !etGrossSale.isEnabled())
                        && (!TextUtils.isEmpty(etNetSale.getText()) || !etNetSale.isEnabled())
                        && (validateEditTextLength(etReceiptNumber, 5, 5) || !etReceiptNumber.isEnabled())
                        && (payTypeSpinner.getSelectedItemPosition() != 0) || !payTypeSpinner.isEnabled()
                        && (!startTime.getText().equals("") && !endTime.getText().equals(""))) {

                    JobObject job = new JobObject();

                    String timeString = String.valueOf(startTime.getText()) + "-" + String.valueOf(endTime.getText());
                    String stringSID = String.valueOf(etSID.getText());
                    int intSID = Integer.valueOf(stringSID.replaceAll("[-]", ""));
                    job.setSID(intSID);
                    job.setTime(timeString);
                    job.setJobNotes(String.valueOf(etJobNotes.getText()));

                    if (cancellationButton.isChecked()) {
                        job.setJobType(String.valueOf(cancellationButton.getText()));
                        job.setGrossSale(0.0);
                        job.setNetSale(0.0);
                        job.setPayType(null);
                        job.setReceiptNumber(0);
                    } else {
                        job.setGrossSale(Double.valueOf(etGrossSale.getText().toString()));
                        job.setNetSale(Double.valueOf(etNetSale.getText().toString()));
                        job.setReceiptNumber(Integer.valueOf(etReceiptNumber.getText().toString()));
                        job.setPayType(payTypeString);
                        if (resButton.isChecked()) {
                            job.setJobType(String.valueOf(resButton.getText()));
                        } else if (commButton.isChecked()) {
                            job.setJobType(String.valueOf(commButton.getText()));
                        }
                    }

                    job.setCcNumber(creditCardNumber);
                    job.setCcExpDate(creditCardExpDate);

                    FirebaseDatabase
                            .getInstance()
                            .getReference(currentJournalRef + "jobs/"
                                    + String.valueOf(intSID))
                            .setValue(job);

                    Toast.makeText(getActivity(), "Job number " + stringSID + " saved", Toast.LENGTH_SHORT).show();

                    dismiss();

                } else {

                    if (!validateEditTextLength(etSID, 5, 8)) {
                        enterSIDWrapper.setErrorEnabled(true);
                        enterSIDWrapper.setError("Must be 5-7 numbers");
                    } else {
                        enterSIDWrapper.setErrorEnabled(false);
                    }
                    if (!validateEditTextLength(etReceiptNumber, 5, 5)) {
                        enterReceiptNumberWrapper.setErrorEnabled(true);
                        enterReceiptNumberWrapper.setError("Must be 5 numbers");
                    } else {
                        enterReceiptNumberWrapper.setErrorEnabled(false);
                    }
                    if (TextUtils.isEmpty(etGrossSale.getText())) {
                        enterGrossSaleWrapper.setErrorEnabled(true);
                        enterGrossSaleWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        enterGrossSaleWrapper.setErrorEnabled(false);
                    }
                    if (TextUtils.isEmpty(etNetSale.getText())) {
                        enterNetSaleWrapper.setErrorEnabled(true);
                        enterNetSaleWrapper.setError(getString(R.string.empty_et_error_message));
                    } else {
                        enterNetSaleWrapper.setErrorEnabled(false);
                    }
                    if (payTypeSpinner.getSelectedItemPosition() == 0) {
                        TextView errorText = (TextView) payTypeSpinner.getSelectedView();
                        errorText.setError("");
                    }
                    if (startTime.getText().equals("") || endTime.getText().equals("")) {
                        Toast.makeText(getActivity(), "Start and end times required", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        cancelJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setCancelable(false);
        return v;

    }

    public void showCreditCardAlertDialog(String title) {

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText creditCard = new EditText(getActivity());
        creditCard.setHint("XXXX-XXXX-XXXX-XXXX");
        creditCard.setGravity(Gravity.CENTER_HORIZONTAL);
        creditCard.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        creditCard.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
        creditCard.addTextChangedListener(new CustomTextWatcher(4, 4, '-'));
        layout.addView(creditCard);

        final EditText expDate = new EditText(getActivity());
        expDate.setHint("XX/XX");
        expDate.setGravity(Gravity.CENTER_HORIZONTAL);
        expDate.setInputType(InputType.TYPE_CLASS_PHONE);
        expDate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        expDate.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));
        expDate.addTextChangedListener(new CustomTextWatcher(2, 2, '/'));
        layout.addView(expDate);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        final AlertDialog AD = alert.setTitle(title)
                .setPositiveButton("OK", null) //Set to null. We override the onclick
                .setNegativeButton("Cancel", null)
                .setView(layout)
                .setCancelable(false)
                .create();

        AD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        AD.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String CC = creditCard.getText().toString();
                        String ED = expDate.getText().toString();

                        if (validateEditTextLength(creditCard, 19, 19)
                                && validateEditTextLength(expDate, 5, 5)) {

                            CC = CC.replaceAll("[-]", "");
                            ED = ED.replaceAll("[/]", "");

                            creditCardNumber = Long.parseLong(CC);
                            creditCardExpDate = Integer.parseInt(ED);

                            AD.dismiss();

                        } else {
                            Toast.makeText(getActivity(), "Fields must be completely filled", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        AD.show();

    }
}