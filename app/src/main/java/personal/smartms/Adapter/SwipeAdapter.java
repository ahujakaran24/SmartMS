package personal.smartms.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import personal.smartms.Fragments.ContactFragment;
import personal.smartms.Fragments.SMSManager;

/**
 * Created by karan on 30/5/15.
 */
public class SwipeAdapter extends FragmentStatePagerAdapter {
    public SwipeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i)
        {
            case 0 :
                fragment = new SMSManager();
                break;
            case 1 :
                fragment = new ContactFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        SMSManager f = (SMSManager ) object;
        if (f != null) {
            f.update();
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Fragment " + (position + 1);
    }
}