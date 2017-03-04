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

public class AthletePagerActivity extends AppCompatActivity {
    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_ATHLETE_ARRAY = "ggr.tpl17.athlete_array";

    private ViewPager viewPager;
    private List<Athlete> athletes;

    public static Intent newIntent(Context packageContext, UUID athleteId) {
        Intent intent = new Intent(packageContext, AthletePagerActivity.class);
        intent.putExtra(EXTRA_ATHLETE_ID, athleteId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_athlete_pager);

        UUID athleteId = (UUID) getIntent().getSerializableExtra(EXTRA_ATHLETE_ID);
        String[] boatAthletes = getIntent().getStringArrayExtra(EXTRA_ATHLETE_ARRAY);

        viewPager = (ViewPager) findViewById(R.id.activity_athlete_pager_view_pager);

        try {
            athletes = AthleteLab.get(this).getAthletes();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Athlete athlete = athletes.get(position);
                return AthleteFragment.newInstance(athlete.getId());
            }

            @Override
            public int getCount() {
                return athletes.size();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Athlete athlete = athletes.get(position);
                if (athlete.getFirstName() != null) {
                    setTitle(athlete.getFirstName());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        for (int i = 0; i < athletes.size(); i++) {
            if (athletes.get(i).getId().equals(athleteId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
