package ggr.tpl7;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class BoatListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new BoatListFragment();
    }
}
