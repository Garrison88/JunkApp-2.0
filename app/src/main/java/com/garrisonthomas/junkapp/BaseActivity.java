package com.garrisonthomas.junkapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.auth.AuthUI;
import com.garrisonthomas.junkapp.dialogfragments.ArchiveJournalDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    public String todaysDate;
    private static final int RC_SIGN_IN = 9001;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;

    public Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date date = new Date();
        SimpleDateFormat df2 = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.CANADA);
        todaysDate = df2.format(date);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                startActivity(new Intent(this, TabsActivity.class));
                Toast.makeText(BaseActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
                finish();
            }
//            else {
//                // user is not signed in. Maybe just wait for the user to press
//                // "sign in" again, or show a message
//            }
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//
//
//        this.menu = menu;
//

//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_call_office:
                Uri number = Uri.parse(getString(R.string.office_phone_number_uri));
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
                break;

            case R.id.action_about_developer:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.developer_website)));
                startActivity(browserIntent);
                break;

            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.action_login_logout:
                LoginLogout();
                break;

            case R.id.action_email_office:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "rcrawford@ridofittoronto.com", null));
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Choose an Email client:"));
                }
                break;

            case R.id.action_archive_journal:

                ArchiveJournalDialogFragment djFragment = new ArchiveJournalDialogFragment();
                djFragment.show(getSupportFragmentManager(), "Dialog");
                break;

            case R.id.action_delete_journal:

                confirmJournalDelete();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    protected void showProgress(String message) {
        if (progressDialog != null && progressDialog.isShowing())
            dismissProgress();

        progressDialog = ProgressDialog.show(this, null, message);
    }

    protected void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void LoginLogout() {

        if (auth.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {

                            // set menu item to "Logout"

                            MenuItem loginLogout = menu.findItem(R.id.action_login_logout);

                            loginLogout.setTitle("Logout");

                            // user is now logged out
                            Toast.makeText(BaseActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(BaseActivity.this, TabsActivity.class));

                        }
                    });
        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI
                            .getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.CustomTheme_NoActionBar)
                            .build(),
                    RC_SIGN_IN);

        }

    }

    private AlertDialog confirmJournalDelete() {

        String currentJournalString = preferences.getString((getString(R.string.sp_current_journal_ref)), null);

        final Firebase currentJournalRef = currentJournalString != null
                ? new Firebase(currentJournalString)
                : null;

        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_journal_delete_title))
                .setMessage(getString(R.string.confirm_journal_delete_message))
                .setIcon(R.drawable.ic_warning_white_24dp)
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

//                        currentJournalRef.removeEventListener(jobsListener);
//                        currentJournalRef.removeEventListener(dumpsListener);
//                        dialog.dismiss();
                        deleteJournal(currentJournalRef);

                    }

                })

                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .show();
    }

    private void deleteJournal(Firebase currentJournalRef) {

        showProgress("Deleting journal...");

        currentJournalRef.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.sp_current_journal_ref), null);
                editor.putString("driver", null);
                editor.putString("navigator", null);
                editor.apply();

                dismissProgress();
                Toast.makeText(BaseActivity.this, "Journal successfully deleted",
                        Toast.LENGTH_SHORT).show();
                ((TabsActivity) BaseActivity.this).notifyJournalChanged();

            }
        });
    }



}