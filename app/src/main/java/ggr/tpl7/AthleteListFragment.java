package ggr.tpl7;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

public class AthleteListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView athleteRecyclerView;
    private AthleteAdapter adapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_athlete_list, container, false);

        athleteRecyclerView = (RecyclerView) view
                .findViewById(R.id.athlete_recycler_view);
        athleteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        try {
            updateUI();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            updateUI();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_athlete_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_athlete:
                Athlete athlete = new Athlete();
                AthleteLab.get(getActivity()).addAthlete(athlete);
                Intent intent = AthletePagerActivity
                        .newIntent(getActivity(), athlete.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() throws ParseException {
        AthleteLab athleteLab = AthleteLab.get(getActivity());
        String athleteCount = "0";
        athleteCount = "" + athleteLab.getAthletes().size();
        String subtitle = getString(R.string.subtitle_format, athleteCount);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() throws ParseException {
        AthleteLab athleteLab = AthleteLab.get(getActivity());
        List<Athlete> athlete = athleteLab.getAthletes();

        if (adapter == null) {
            adapter = new AthleteAdapter(athlete);
            athleteRecyclerView.setAdapter(adapter);
        } else {
            adapter.setAthletes(athlete);
            adapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class AthleteHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView nameTextView;
        private TextView twokTextView;
        private Button imageButtonView;

        private Athlete athlete;

        public AthleteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            nameTextView = (TextView) itemView.findViewById(R.id.list_item_athlete_name_text_view);
            twokTextView = (TextView) itemView.findViewById(R.id.list_item_athlete_twok_text_view);
            imageButtonView = (Button) itemView.findViewById(R.id.list_item_athlete_image_button_view);
        }

        public void bindAthlete(Athlete bAthlete) {
            athlete = bAthlete;
            String full = athlete.getFirstName() + " " + athlete.getLastName();
            nameTextView.setText(full);
            String twok = athlete.getTwokMin() + ":" + athlete.getTwokSec();
            twokTextView.setText(twok);
            //TODO:get image path
        }

        @Override
        public void onClick(View v) {
            Intent intent = AthletePagerActivity.newIntent(getActivity(), athlete.getId());
            startActivity(intent);
        }
    }

    private class AthleteAdapter extends RecyclerView.Adapter<AthleteHolder> {

        private List<Athlete> athletes;

        public AthleteAdapter(List<Athlete> lAthletes) {
            athletes = lAthletes;
        }

        @Override
        public AthleteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_athlete, parent, false);
            return new AthleteHolder(view);
        }

        @Override
        public void onBindViewHolder(AthleteHolder holder, int position) {
            Athlete athlete = athletes.get(position);
            holder.bindAthlete(athlete);
        }

        @Override
        public int getItemCount() {
            return athletes.size();
        }

        public void setAthletes(List<Athlete> athletes) {
            this.athletes = athletes;
        }
    }
}
