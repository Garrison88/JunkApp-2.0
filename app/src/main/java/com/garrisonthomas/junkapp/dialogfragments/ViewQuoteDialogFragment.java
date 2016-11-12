package com.garrisonthomas.junkapp.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.entryobjects.QuoteObject;
import com.garrisonthomas.junkapp.interfaces.ViewItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Garrison on 2016-06-16.
 */
public class ViewQuoteDialogFragment extends DialogFragmentHelper implements ViewItem {

    @Bind(R.id.tv_view_quote_low_end)
    TextView vqLowEnd;
    @Bind(R.id.tv_view_quote_high_end)
    TextView vqHighEnd;
    @Bind(R.id.tv_view_quote_time)
    TextView vqTime;
    @Bind(R.id.tv_view_quote_notes)
    TextView vqNotes;
    @Bind(R.id.tv_view_quote_notes_display)
    TextView tvQuoteNotesDisplay;
    @Bind(R.id.vq_high_end_display)
    TextView vqHighEndDisplay;
    @Bind(R.id.btn_view_quote_ok)
    Button okBtn;
    @Bind(R.id.btn_delete_quote)
    ImageButton deleteQuoteBtn;
    //    @Bind(R.id.image_view_quote_photo)
//    ImageView ivQuotePhoto;
    private int vqSIDInt;
    private String firebaseJournalRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.view_quote_layout, container, false);

        ButterKnife.bind(this, v);

        tvQuoteNotesDisplay.setVisibility(View.GONE);
        vqNotes.setVisibility(View.GONE);

//        Typeface custom_font = Typeface.createFromAsset(v.getContext().getApplicationContext().getAssets(), "fonts/WorkSans-Regular.ttf");
//
//        vqNotes.setTypeface(custom_font);

        populateItemInfo();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteQuoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteItemAlertDialog(ViewQuoteDialogFragment.this,
                        firebaseJournalRef + "quotes/" + vqSIDInt);
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Bundle vqBundle = getArguments();
        String vqSIDString = vqBundle.getString("quoteSpinnerSID");
        vqSIDInt = Integer.parseInt(vqSIDString.replaceAll("[-]", ""));
        firebaseJournalRef = vqBundle.getString(getString(R.string.sp_current_journal_ref));
        dialog.setTitle("Quote SID: " + vqSIDString);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public void populateItemInfo() {

        FirebaseDatabase
                .getInstance()
                .getReference(firebaseJournalRef + "quotes")
                .orderByChild("quoteSID")
                .equalTo(vqSIDInt)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                        QuoteObject quoteObject = snapshot.getValue(QuoteObject.class);

                        String highEndString = currencyFormat.format(quoteObject.getHighEnd());
                        String lowEndString = currencyFormat.format(quoteObject.getLowEnd());

//                if(quoteObject.getPhotoDownloadUrl() != null) {
//                    Picasso.with(getContext())
//                            .load(quoteObject.getPhotoDownloadUrl())
//                            .resize(50, 50)
//                            .into(ivQuotePhoto);
//                }

                        if (quoteObject.getHighEnd() != 0) {
                            vqHighEnd.setVisibility(View.VISIBLE);
                            vqHighEndDisplay.setVisibility(View.VISIBLE);
                            vqHighEnd.setText(highEndString);
                        }
                        String startEndTime = quoteObject.getTime();
                        vqLowEnd.setText(lowEndString);
                        vqTime.setText(startEndTime);

                        if (!TextUtils.isEmpty(quoteObject.getQuoteNotes())) {
                            vqNotes.setText(quoteObject.getQuoteNotes());
                            tvQuoteNotesDisplay.setVisibility(View.VISIBLE);
                            vqNotes.setVisibility(View.VISIBLE);

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
