package com.garrisonthomas.junkapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public abstract class Utils {

    public static void populateEntrySpinner(final Context context, final String reference,
                                            final ArrayList<String> arrayList, final Spinner spinner,
                                            final boolean isSID) {

        FirebaseDatabase
                .getInstance()
                .getReference(reference)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChild) {

                        if (!isSID) {
                            arrayList.add(snapshot.getKey());
                        } else {
                            arrayList.add((String.valueOf(snapshot.getKey()).length() <= 5)
                                    ? snapshot.getKey()
                                    : String.valueOf(snapshot.getKey()).substring(0, 5)
                                    + "-" + String.valueOf(snapshot.getKey()).substring(5));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_item, arrayList);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        spinner.setAdapter(adapter);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        if (!isSID) {
                            arrayList.remove(dataSnapshot.getKey());
                        } else {
                            arrayList.remove((String.valueOf(dataSnapshot.getKey()).length() <= 5)
                                    ? dataSnapshot.getKey()
                                    : String.valueOf(dataSnapshot.getKey()).substring(0, 5)
                                    + "-" + String.valueOf(dataSnapshot.getKey()).substring(5));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_item, arrayList);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        spinner.setAdapter(adapter);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });

    }

    public static double calculateTax(double grossSale) {

        return Math.round((grossSale * 1.13) * 100.00) / 100.00;

    }


}