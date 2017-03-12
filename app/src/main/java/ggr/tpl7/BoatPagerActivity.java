package ggr.tpl7;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;
import ggr.tpl7.model.Boat;
import ggr.tpl7.model.BoatLab;

public class BoatPagerActivity extends AppCompatActivity {
    private static final String EXTRA_BOAT_ID = "ggr.tpl17.boat_id";

    private ViewPager viewPager;
    private List<Boat> boats;

    public static Intent newIntent(Context packageContext, UUID boatId) {
        Intent intent = new Intent(packageContext, BoatPagerActivity.class);
        intent.putExtra(EXTRA_BOAT_ID, boatId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_pager);

        UUID boatId = (UUID) getIntent().getSerializableExtra(EXTRA_BOAT_ID);

        viewPager = (ViewPager) findViewById(R.id.activity_boat_pager_view_pager);

        boats = BoatLab.get(this).getBoats();

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Boat boat = boats.get(position);
                return BoatFragment.newInstance(boat.getId());
            }

            @Override
            public int getCount() {
                return boats.size();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Boat boat = boats.get(position);
                if (boat.getName() != null) {
                    setTitle(boat.getName());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        for (int i = 0; i < boats.size(); i++) {
            if (boats.get(i).getId().equals(boatId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
