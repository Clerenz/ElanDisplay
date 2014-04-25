package de.clemensloos.elan.alert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.ViewGroup;

public class ElanAlert extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elan_alert);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_elan_alert, menu);
//        return true;
    	return false;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	
    	SenderFragment senderFragment;
    	ReceiverFragment receiverFragment;
    	SettingsFragment settingsFragment;
    	
    	
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            
            senderFragment = new SenderFragment();
            receiverFragment = new ReceiverFragment();
            settingsFragment = new SettingsFragment();
            
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
            case 0:
            	return senderFragment;
            case 1:
            	return receiverFragment;
            case 2:
            	return settingsFragment;
            }
        	
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_section1).toUpperCase();
                case 1: return getString(R.string.title_section2).toUpperCase();
                case 2: return getString(R.string.title_section3).toUpperCase();
            }
            return null;
        }
        
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
        	super.setPrimaryItem(container, position, object);
        	switch(position) {
        	case(0):
        		senderFragment.refreshParameter();
        		receiverFragment.pause();
        		break;
        	case(1):
        		receiverFragment.refreshParameter();
        		receiverFragment.restart();
        		break;
        	case(2):
        		receiverFragment.pause();
        		break;
        	}
        }
    }
}
