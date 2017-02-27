package ggr.tpl7;

import android.support.v4.app.Fragment;

public class AthleteListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AthleteListFragment();
    }
}
