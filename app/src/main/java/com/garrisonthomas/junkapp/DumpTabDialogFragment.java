package com.garrisonthomas.junkapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.garrisonthomas.junkapp.dialogfragments.GarbageDumpFragment;
import com.garrisonthomas.junkapp.dialogfragments.RebateDumpFragment;

public class DumpTabDialogFragment extends DialogFragmentHelper {

    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.dump_tabbed_dialog_layout, container);

        viewPager = v.findViewById(R.id.viewpager);
        TabLayout tabLayout = v.findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        setCancelable(false);

        return v;
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new GarbageDumpFragment(), "Garbage");
        adapter.addFragment(new RebateDumpFragment(), "Rebate");

        viewPager.setAdapter(adapter);



    }
}
