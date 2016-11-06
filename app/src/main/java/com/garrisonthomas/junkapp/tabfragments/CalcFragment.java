package com.garrisonthomas.junkapp.tabfragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.Utils;

import java.text.NumberFormat;
import java.util.ArrayList;

public class CalcFragment extends Fragment {

    private Spinner vSpinner, bSpinner;
    private Button addHST;
    private TextView tvVolumeSize, tvBedloadSize, tvTotal;
    private int[] volumePrice, bedloadPrice;
    private String[] volumeSize, bedloadSize;
    private int vPrice, bPrice;
    private double sum, discount;

    private NumberFormat currencyFormat;

    ArrayList<Integer> priceArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.calculator_layout, container, false);

        addHST = (Button) v.findViewById(R.id.btn_add_hst);
        Button clearCost = (Button) v.findViewById(R.id.btn_clear_cost);

        vSpinner = (Spinner) v.findViewById(R.id.spinner_volume);
        bSpinner = (Spinner) v.findViewById(R.id.spinner_bedload);

        tvVolumeSize = (TextView) v.findViewById(R.id.tv_display_volume);
        tvBedloadSize = (TextView) v.findViewById(R.id.tv_display_bedload);
        tvTotal = (TextView) v.findViewById(R.id.load_total);

        volumePrice = v.getResources().getIntArray(R.array.string_volume_price);
        bedloadPrice = v.getResources().getIntArray(R.array.string_bedload_price);
        volumeSize = v.getResources().getStringArray(R.array.string_volume_name);
        bedloadSize = v.getResources().getStringArray(R.array.string_bedload_name);

        currencyFormat = NumberFormat.getCurrencyInstance();

        vSpinner.setAdapter(new CustomVolumeSpinnerAdapter(getActivity(), R.layout.custom_spinner_layout, volumeSize));
        vSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                vPrice = volumePrice[position];

                switch (position) {
                    case 0:
                        break;
                    // User selected a volume price
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 13:
                    case 14:

                        if (!TextUtils.isEmpty(tvVolumeSize.getText())) {
                            tvVolumeSize.append("\n" + "+" + "\n");
                        }

                        tvVolumeSize.append(volumeSize[position] + " ($" + volumePrice[position] + ")");
                        priceArray.add(vPrice);

                        break;

                    // User selected a custom price
                    case 15:

                        customPriceAlertDialog().show();

                        break;

                    // User selected a percentage discount
                    case 17:
                    case 18:
                    case 19:
                    case 20:

                        if (!TextUtils.isEmpty(tvVolumeSize.getText())) {
                            tvVolumeSize.append("\n" + "+" + "\n");
                        }

                        tvVolumeSize.append(volumeSize[position]);
                        discount = vPrice * .010;

                        break;

                    // User selected a dollar value discount
                    case 21:

                        if (!TextUtils.isEmpty(tvVolumeSize.getText())) {
                            tvVolumeSize.append("\n" + "+" + "\n");
                        }

                        tvVolumeSize.append(volumeSize[position]);
                        priceArray.add(-vPrice);

                        break;

                }

                calcCost();

                vSpinner.setSelection(0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        bSpinner.setAdapter(new CustomBedloadSpinnerAdapter(getActivity(), R.layout.custom_spinner_layout, bedloadSize));
        bSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                bSpinner.setSelection(0);
                bPrice = bedloadPrice[position];
                if (position != 0) {
                    if (!TextUtils.isEmpty(tvBedloadSize.getText())) {
                        tvBedloadSize.append("\n" + "+" + "\n");
                    }

                    tvBedloadSize.append(bedloadSize[position] + " ($" + bedloadPrice[position] + ")");
                    priceArray.add(bPrice);

                    calcCost();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //clears priceArray and all textViews
        clearCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                priceArray.clear();
                tvVolumeSize.setText("");
                tvBedloadSize.setText("");
                tvTotal.setText("");
                addHST.setClickable(true);
                addHST.setText("Add HST");
                discount = 0.0;

            }
        });

        addHST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addHST.getText().equals("Add HST")) {
                    if (!TextUtils.isEmpty(tvTotal.getText())) {

                        tvTotal.setText(currencyFormat.format(Utils.calculateTax(sum)));

                        addHST.setText("Display HST");
                    }
                } else {
                    tvTotal.setText(currencyFormat.format(priceWithHST(sum)));
                    addHST.setClickable(false);
                }
            }
        });

        return v;
    }

    public double priceWithHST(double preTaxAmount) {

        return Utils.calculateTax(preTaxAmount) - preTaxAmount;

    }

    public void calcCost() {

        if (!TextUtils.isEmpty(tvVolumeSize.getText()) || !TextUtils.isEmpty(tvBedloadSize.getText())) {
            sum = 0;
            for (int i : priceArray) {
                sum += i;
            }

            if (discount != 0.0) {
                sum *= discount;
            }
            String sumString = currencyFormat.format(sum);
            tvTotal.setText(sumString);

        }

    }

    public AlertDialog customPriceAlertDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText edittext = new EditText(getActivity());

        edittext.setGravity(Gravity.CENTER_HORIZONTAL);

        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setTitle("Enter a value");

        alert.setView(edittext);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (!TextUtils.isEmpty(tvVolumeSize.getText())) {
                    tvVolumeSize.append("\n" + "+" + "\n");
                }

                tvVolumeSize.append("$" + edittext.getText().toString());
                priceArray.add(Integer.valueOf(edittext.getText().toString()));
                calcCost();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        AlertDialog AD = alert.create();
        AD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return AD;

    }



    public class CustomVolumeSpinnerAdapter extends ArrayAdapter<String> {

        public CustomVolumeSpinnerAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.custom_spinner_layout, parent,
                    false);

            TextView main_text = (TextView) mySpinner.findViewById(R.id.tv_custom_spinner_first);
            main_text.setText(volumeSize[position]);

            TextView subSpinner = (TextView) mySpinner.findViewById(R.id.tv_custom_spinner_second);

            if (position == 0 || position == 12 || position >= 16) {
                subSpinner.setVisibility(View.GONE);
            } else if (position == 15) {
                subSpinner.setText("???");
            } else {
                subSpinner.setText("$" + volumePrice[position]);
            }

//            ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.left_pic);
//            left_icon.setImageResource(R.drawable.dump_truck);

            return mySpinner;
        }

    }

    public class CustomBedloadSpinnerAdapter extends ArrayAdapter<String> {

        public CustomBedloadSpinnerAdapter(Context ctx, int txtViewResourceId, String[] objects) {
            super(ctx, txtViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }

        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View mySpinner = inflater.inflate(R.layout.custom_spinner_layout, parent,
                    false);

            TextView main_text = (TextView) mySpinner.findViewById(R.id.tv_custom_spinner_first);
            main_text.setText(bedloadSize[position]);

            TextView subSpinner = (TextView) mySpinner.findViewById(R.id.tv_custom_spinner_second);

            if (position == 0) {
                subSpinner.setVisibility(View.GONE);
            } else {
                subSpinner.setText("$" + bedloadPrice[position]);
            }

//            ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.left_pic);
//            left_icon.setImageResource(R.drawable.dump_truck);

            return mySpinner;
        }
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//        super.onSaveInstanceState(outState);
//        outState.putString("tab", "CalcFragment"); //save the tab selected
//
//    }

}