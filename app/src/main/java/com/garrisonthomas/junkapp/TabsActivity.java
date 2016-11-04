package com.garrisonthomas.junkapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.garrisonthomas.junkapp.entryobjects.TransferStationObject;
import com.garrisonthomas.junkapp.tabfragments.CalcFragment;
import com.garrisonthomas.junkapp.tabfragments.DumpFragment;
import com.garrisonthomas.junkapp.tabfragments.JournalFragment;

import java.util.ArrayList;

public class TabsActivity extends BaseActivity {

    private TabLayout tabLayout;
    private SharedPreferences preferences;
    private ViewPager viewPager;

    private static ArrayList<TransferStationObject> transferStationArrayList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs_activity_layout);

        Firebase dumpInfoRef = new Firebase(App.FIREBASE_URL + "dumpInfo");
        dumpInfoRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    transferStationArrayList.add(shot.getValue(TransferStationObject.class));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(auth.getCurrentUser() != null
                ? auth.getCurrentUser().getEmail()
                : null);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public static ArrayList<TransferStationObject> getTransferStationArrayList() {
        return transferStationArrayList;
    }

    public void notifyJournalChanged() {

        viewPager.getAdapter().notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        invalidateOptionsMenu();

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_assignment_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_keyboard_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_local_shipping_white_24dp);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new JournalFragment(), "Journal");
        adapter.addFragment(new CalcFragment(), "Calculator");
        adapter.addFragment(new DumpFragment(), "Dumps");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        String currentJournal = preferences.getString(getString(R.string.sp_current_journal_ref), null);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem loginLogout = menu.findItem(R.id.action_login_logout);
        MenuItem delete = menu.findItem(R.id.action_delete_journal);
        MenuItem archive = menu.findItem(R.id.action_archive_journal);

        loginLogout.setTitle((auth.getCurrentUser() != null)
                ? "Logout"
                : "Login");

        delete.setVisible(tabLayout.getSelectedTabPosition() == 0 && currentJournal != null);
        archive.setVisible(tabLayout.getSelectedTabPosition() == 0 && currentJournal != null);

        return true;
    }
}