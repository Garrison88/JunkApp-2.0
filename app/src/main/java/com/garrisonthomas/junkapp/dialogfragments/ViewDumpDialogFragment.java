package com.garrisonthomas.junkapp.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.entryobjects.DumpObject;
import com.garrisonthomas.junkapp.entryobjects.RebateObject;
import com.garrisonthomas.junkapp.interfaces.ViewItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by GarrisonThomas on 2015-10-22.
 */
public class ViewDumpDialogFragment extends DialogFragmentHelper implements ViewItem {

    @Bind(R.id.tv_view_dump_gross_cost)
    TextView vdGross;
    @Bind(R.id.tv_view_dump_tonnage)
    TextView vdTonnage;
    @Bind(R.id.tv_view_dump_percent_previous)
    TextView vdPercentPrevious;
    @Bind(R.id.v_d_percent_previous_text)
    TextView vdPercentPreviousText;
    @Bind(R.id.cost_amount_tv)
    TextView tvCostOrAmount;
    @Bind(R.id.btn_view_dump_ok)
    Button okBtn;
    @Bind(R.id.btn_delete_dump)
    ImageButton deleteDumpBtn;
    private String dumpName, currentJournalRef;
    private int dumpReceiptNumber;
    private boolean isRebate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.view_dump_layout, container, false);

        ButterKnife.bind(this, v);

        tvCostOrAmount.setText(!isRebate ? "Gross Cost" : "Amount");

        vdPercentPreviousText.setVisibility(View.GONE);
        vdPercentPrevious.setVisibility(View.GONE);

        populateItemInfo();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteDumpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRebate) {
                    showDeleteItemAlertDialog(ViewDumpDialogFragment.this, currentJournalRef + "dumps/" +
                            dumpName);
                } else {
                    showDeleteItemAlertDialog(ViewDumpDialogFragment.this, currentJournalRef + "rebate/" +
                            dumpName);
                }
            }
        });

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Bundle vdBundle = getArguments();
        dumpName = vdBundle.getString("dumpName");
        isRebate = (dumpName != null && dumpName.substring(0, 2).equals("R+"));
        dumpReceiptNumber = vdBundle.getInt("dumpReceiptNumber");
        currentJournalRef = vdBundle.getString(getString(R.string.sp_current_journal_ref));
        dialog.setTitle(dumpName);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public void populateItemInfo() {

        if (!isRebate) {

            FirebaseDatabase
                    .getInstance()
                    .getReference(currentJournalRef + "dumps")
                    .orderByChild("dumpReceiptNumber")
                    .equalTo(dumpReceiptNumber)
                    .addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                            DumpObject dumpObject = snapshot.getValue(DumpObject.class);

                            double previousAmount;
                            String grossCost = currencyFormat.format(dumpObject.getGrossCost());

                            vdGross.setText(grossCost);
                            vdTonnage.setText(String.valueOf(dumpObject.getTonnage()));
                            if (dumpObject.getPercentPrevious() != 0) {
                                previousAmount = Math.round(dumpObject.getGrossCost()
                                        * (dumpObject.getPercentPrevious() * .01) * 100.00) / 100.00;
                                vdPercentPrevious.setText(String.valueOf(dumpObject.getPercentPrevious()) + "%"
                                        + "\n" + "(" + currencyFormat.format(previousAmount) + ")");
                                vdPercentPreviousText.setVisibility(View.VISIBLE);
                                vdPercentPrevious.setVisibility(View.VISIBLE);
                            }
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
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });
        } else {

            FirebaseDatabase
                    .getInstance()
                    .getReference(currentJournalRef + "rebate")
                    .orderByChild("receiptNumber")
                    .equalTo(dumpReceiptNumber)
                    .addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                            RebateObject rebateObject = snapshot.getValue(RebateObject.class);

                            double previousAmount;
                            String rebateAmount = currencyFormat.format(rebateObject.getRebateAmount());

                            vdGross.setText(rebateAmount);
                            vdTonnage.setText(String.valueOf(rebateObject.getRebateTonnage()));
                            if (rebateObject.getPercentPrevious() != 0) {
                                previousAmount = Math.round(rebateObject.getRebateAmount()
                                        * (rebateObject.getPercentPrevious() * .01) * 100.00) / 100.00;
                                vdPercentPrevious.setText(String.valueOf(rebateObject.getPercentPrevious()) + "%"
                                        + "\n" + "(" + currencyFormat.format(previousAmount) + ")");
                                vdPercentPreviousText.setVisibility(View.VISIBLE);
                                vdPercentPrevious.setVisibility(View.VISIBLE);
                            }
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
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });

        }

    }

}
