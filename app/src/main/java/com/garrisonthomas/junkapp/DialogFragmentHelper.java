package com.garrisonthomas.junkapp;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Created by Garrison on 2016-06-11.
 */
public class DialogFragmentHelper extends DialogFragment {

    public static NumberFormat currencyFormat;
    public static FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog() != null) {

            // This helps to always show cancel and save button when keyboard is open
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            // This normalizes the width of the dialogFragments to 90% of screen width
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.90);

            getDialog().getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

            currencyFormat = NumberFormat.getCurrencyInstance();

        }
    }

    public static AlertDialog showDeleteItemAlertDialog(final DialogFragment df, final String databaseRef) {

        AlertDialog.Builder builder = new AlertDialog.Builder(df.getActivity());
        builder.setTitle("Delete this entry?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning_white_24dp)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

                        FirebaseDatabase
                                .getInstance()
                                .getReference(databaseRef)
                                .removeValue();

                        Toast.makeText(df.getActivity(), "Entry deleted", Toast.LENGTH_SHORT).show();

                        df.dismiss();

                    }

                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });
        return builder.show();

    }

    public static boolean validateEditTextLength(EditText et, int minLength, int maxLength) {

        String inputString = String.valueOf(et.getText());

        return (!inputString.equals("")
                && inputString.length() <= maxLength
                && inputString.length() >= minLength);
    }

    public static TimePickerDialog createTimePickerDialog(Context context, final Button button, String title) {

        final Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, android.app.AlertDialog.BUTTON_POSITIVE, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                if (selectedMinute == 00) {
                    button.setText(selectedHour + ":" + selectedMinute + "0");
                } else if (selectedMinute < 10) {
                    button.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                } else {
                    button.setText(selectedHour + ":" + selectedMinute);
                }

//                SimpleDateFormat fmt = new SimpleDateFormat("kk:mm");
//                Date date = new Date();
//                String dateString = fmt.format(date);
//
//                button.setText(dateString);

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(title);

        return mTimePicker;

    }

    public static double calculateDump(double pricePerTonne, double weightInTonnes, int minimumCharge) {

        return Math.round((weightInTonnes * pricePerTonne) * 100.00) / 100.00 < minimumCharge
                ? minimumCharge
                : Math.round((weightInTonnes * pricePerTonne) * 100.00) / 100.00;

    }

}
