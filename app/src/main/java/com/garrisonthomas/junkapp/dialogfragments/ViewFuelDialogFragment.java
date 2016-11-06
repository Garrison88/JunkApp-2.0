package com.garrisonthomas.junkapp.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.entryobjects.FuelObject;
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
public class ViewFuelDialogFragment extends DialogFragmentHelper implements ViewItem {

    @Bind(R.id.tv_view_fuel_vendor)
    TextView vfVendor;
    @Bind(R.id.tv_view_fuel_cost)
    TextView vfCost;
    @Bind(R.id.btn_view_fuel_ok)
    Button okBtn;
    @Bind(R.id.btn_delete_fuel)
    ImageButton deleteFuelBtn;
    public String firebaseJournalRef, fuelReceiptNumber;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.view_fuel_layout, container, false);

        ButterKnife.bind(this, v);

        populateItemInfo();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteFuelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(ViewFuelDialogFragment.this,
                        firebaseJournalRef + "/fuel/" + String.valueOf(fuelReceiptNumber)).show();
            }
        });

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Bundle vfBundle = getArguments();
        fuelReceiptNumber = vfBundle.getString("fuelReceiptNumber");
        firebaseJournalRef = vfBundle.getString(getString(R.string.sp_current_journal_ref));
        dialog.setTitle(fuelReceiptNumber);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public void populateItemInfo() {

        FirebaseDatabase
                .getInstance()
                .getReference(firebaseJournalRef + "/fuel")
                .orderByChild("fuelReceiptNumber")
                .equalTo(fuelReceiptNumber)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                        FuelObject fuelObject = snapshot.getValue(FuelObject.class);

                        vfVendor.setText(String.valueOf(fuelObject.getFuelVendor()));
                        vfCost.setText(currencyFormat.format(fuelObject.getFuelCost()));

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
