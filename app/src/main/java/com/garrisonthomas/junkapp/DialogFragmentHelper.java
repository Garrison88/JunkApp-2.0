package com.garrisonthomas.junkapp;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Garrison on 2016-06-11.
 */
public abstract class DialogFragmentHelper extends DialogFragment {

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public Date date = new Date();
    public SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.CANADA);
    public SimpleDateFormat month = new SimpleDateFormat("MMM", Locale.CANADA);
    public SimpleDateFormat day = new SimpleDateFormat("dd", Locale.CANADA);
    public SimpleDateFormat fullDate = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.CANADA);
    public String currentYear = year.format(date);
    public String currentMonth = month.format(date);
    public String currentDay = day.format(date);
    public String todaysDate = fullDate.format(date);
    public NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog() != null) {

            Window window = getDialog().getWindow();

            // This helps to always show cancel and save button when keyboard is open
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            // This normalizes the width of the dialogFragments to 90% of screen width
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.90);

            window.setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        }
    }

    public static AlertDialog deleteItem(final DialogFragment df, final String firebaseRef) {

        AlertDialog.Builder builder = new AlertDialog.Builder(df.getActivity());
        builder.setTitle("Delete this entry?")
                .setCancelable(false)
                .setIcon(R.drawable.ic_warning_white_24dp)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {

                        Firebase ref = new Firebase(firebaseRef);
                        ref.removeValue();
                        Toast.makeText(df.getActivity(), "Entry deleted", Toast.LENGTH_SHORT).show();
                        df.dismiss();

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();

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
