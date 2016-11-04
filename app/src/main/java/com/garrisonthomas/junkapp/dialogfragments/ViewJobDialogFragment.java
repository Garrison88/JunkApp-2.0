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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.garrisonthomas.junkapp.DialogFragmentHelper;
import com.garrisonthomas.junkapp.R;
import com.garrisonthomas.junkapp.entryobjects.JobObject;
import com.garrisonthomas.junkapp.interfaces.ViewItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewJobDialogFragment extends DialogFragmentHelper implements ViewItem {

    @Bind(R.id.tv_view_job_gross)
    TextView vjGross;
    @Bind(R.id.tv_view_job_net)
    TextView vjNet;
    @Bind(R.id.tv_view_pay_type)
    TextView vjPayType;
    @Bind(R.id.tv_view_job_type)
    TextView vjJobType;
    @Bind(R.id.tv_job_type_display)
    TextView tvJobTypeDisplay;
    @Bind(R.id.tv_view_job_time)
    TextView vjTime;
    @Bind(R.id.tv_view_job_receipt_number)
    TextView vjReceiptNumber;
    @Bind(R.id.tv_view_job_notes)
    TextView vjNotes;
    @Bind(R.id.tv_view_job_notes_display)
    TextView tvNotesDisplay;
    @Bind(R.id.btn_view_job_ok)
    Button okBtn;
    @Bind(R.id.btn_delete_job)
    ImageButton deleteJobBtn;
    private static int vjSID;
    public static String firebaseJournalRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.view_job_layout, container, false);

        ButterKnife.bind(this, v);

        //Hide these optional fields by default
        tvJobTypeDisplay.setVisibility(View.GONE);
        vjJobType.setVisibility(View.GONE);
        tvNotesDisplay.setVisibility(View.GONE);
        vjNotes.setVisibility(View.GONE);

        populateItemInfo();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(ViewJobDialogFragment.this,
                        firebaseJournalRef + "/jobs/" + String.valueOf(vjSID)).show();
            }
        });

        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Bundle vjBundle = getArguments();
        vjSID = vjBundle.getInt("jobSpinnerSID");
        firebaseJournalRef = vjBundle.getString(getString(R.string.sp_current_journal_ref));
        dialog.setTitle("Job SID: " + String.valueOf(vjSID));
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public void populateItemInfo() {

        Firebase ref = new Firebase(firebaseJournalRef + "jobs");
        Query queryRef = ref.orderByChild("sid").equalTo(vjSID);
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                JobObject jobObject = snapshot.getValue(JobObject.class);

                String gross = currencyFormat.format(jobObject.getGrossSale());
                String net = currencyFormat.format(jobObject.getNetSale());

                vjGross.setText(gross);
                vjNet.setText(net);

                vjPayType.setText(jobObject.getPayType());
                if (jobObject.getJobType() != null) {
                    tvJobTypeDisplay.setVisibility(View.VISIBLE);
                    vjJobType.setVisibility(View.VISIBLE);
                    vjJobType.setText(jobObject.getJobType());
                }
                vjJobType.setText(jobObject.getJobType());
                vjTime.setText(jobObject.getTime());
                vjReceiptNumber.setText(String.valueOf(jobObject.getReceiptNumber()));
                // if there are no notes, do not display them
                if (jobObject.getJobNotes() != null) {
                    tvNotesDisplay.setVisibility(View.VISIBLE);
                    vjNotes.setVisibility(View.VISIBLE);
                    vjNotes.setText(jobObject.getJobNotes());
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
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}