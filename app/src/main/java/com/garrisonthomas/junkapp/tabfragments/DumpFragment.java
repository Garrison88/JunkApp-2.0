package com.garrisonthomas.junkapp.tabfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.TabsActivity;
import com.garrisonthomas.junkapp.entryobjects.TransferStationObject;

import java.text.NumberFormat;
import java.util.ArrayList;

import static com.garrisonthomas.junkapp.DialogFragmentHelper.calculateDump;

public class DumpFragment extends Fragment {

    private Spinner dumpsSpinner;
    private Button infoBtn, dirBtn, calcBtn, dumpsClearBtn;
    private ArrayList<TransferStationObject> transferStationObjectArrayList;
    private ArrayList<String> dumpNameArray = new ArrayList<>();
    private double selectedDumpRate;
    private EditText etDumpCost;
    private TextView tvDumpCost;
    private String selectedDumpInfo, selectedDumpName;
    private NumberFormat currencyFormat;
    private NumberFormat wholeNumberCurrencyFormat;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.dumps_layout, container, false);

        currencyFormat = NumberFormat.getCurrencyInstance();
        wholeNumberCurrencyFormat = NumberFormat.getCurrencyInstance();
        wholeNumberCurrencyFormat.setMinimumFractionDigits(0);

        infoBtn = (Button) v.findViewById(R.id.btn_dump_info);
        dirBtn = (Button) v.findViewById(R.id.btn_dump_directions);
        calcBtn = (Button) v.findViewById(R.id.btn_calculate_dump);
        dumpsClearBtn = (Button) v.findViewById(R.id.dumps_clear);

        dumpsSpinner = (Spinner) v.findViewById(R.id.spinner_dumps);

        etDumpCost = (EditText) v.findViewById(R.id.et_dump_cost);
        tvDumpCost = (TextView) v.findViewById(R.id.tv_dump_cost);

        transferStationObjectArrayList = TabsActivity.getTransferStationArrayList();

        for (TransferStationObject x : transferStationObjectArrayList) {

            dumpNameArray.add(x.getName());

        }

        dumpsSpinner.setAdapter(new DumpsAdapter(getActivity(), R.layout.custom_spinner_layout, dumpNameArray));

        dumpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                final String dir = getString(R.string.google_maps_url) + transferStationObjectArrayList.get(position).getAddress();
                selectedDumpInfo = transferStationObjectArrayList.get(position).getInfo();
                selectedDumpRate = transferStationObjectArrayList.get(position).getRate();
                selectedDumpName = transferStationObjectArrayList.get(position).getName();

                dirBtn.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse(dir));
                        startActivity(intent);
                    }
                });

                infoBtn.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(selectedDumpName)
                                .setMessage(selectedDumpInfo)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNeutralButton("Call", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Uri number = Uri.parse("tel:" + transferStationObjectArrayList.get(position).getPhoneNumber());
                                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                                        startActivity(callIntent);

                                    }
                                })

                                .show();

                    }
                });

                calcBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!String.valueOf(etDumpCost.getText()).equals("")) {
                            // close keyboard
                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            float weightInTonnes = Float.valueOf(etDumpCost.getText().toString());

                            int minimum = transferStationObjectArrayList.get(dumpsSpinner.getSelectedItemPosition()).getMinimum();

                            double result = calculateDump(selectedDumpRate, weightInTonnes, minimum);

                            String resultString = currencyFormat.format(result);

                            etDumpCost.setVisibility(View.GONE);
                            tvDumpCost.setVisibility(View.VISIBLE);
                            tvDumpCost.setText(resultString);

                            calcBtn.setClickable(false);
                        }
                    }


                });

                dumpsClearBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tvDumpCost.setVisibility(View.GONE);
                        etDumpCost.setVisibility(View.VISIBLE);
                        etDumpCost.setText("");
                        calcBtn.setClickable(true);


                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;

    }

    public class DumpsAdapter extends ArrayAdapter<String> {

        DumpsAdapter(Context ctx, int txtViewResourceId, ArrayList<String> objects) {
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

        View getCustomView(int position, View convertView,
                           ViewGroup parent) {

            LayoutInflater inflater = getActivity().getLayoutInflater();

            View mySpinner = inflater.inflate(R.layout.custom_dumps_spinner, parent, false);

            TextView mainText = (TextView) mySpinner.findViewById(R.id.spinner_text_dump_name);

            mainText.setText(dumpNameArray.get(position));

            TextView subText = (TextView) mySpinner.findViewById(R.id.spinner_text_dump_rate);

            Double rate = transferStationObjectArrayList.get(position).getRate();

            subText.setText(rate % 1 == 0
                    ? wholeNumberCurrencyFormat.format(rate)
                    : currencyFormat.format(rate));


            return mySpinner;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("tab", "DumpFragment"); //save the tab selected

    }

}