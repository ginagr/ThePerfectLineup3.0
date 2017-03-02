package ggr.tpl7;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

public class AthleteListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_ATHLETE_ARRAY = "ggr.tpl17.athlete_array";
    private static final String EXTRA_BOAT_POSITION = "ggr.tpl17.boat_position";
    private static final String EXTRA_BOAT_ATHLETE_ID = "ggr.tpl17.boat_athlete_id";

    private RecyclerView athleteRecyclerView;
    private AthleteAdapter adapter;
    private boolean mSubtitleVisible;

    private String[] boatAthletes = new String[45];

    private int boatPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        boatPosition = getActivity().getIntent().getIntExtra(EXTRA_BOAT_POSITION, -1);

        boatAthletes = getActivity().getIntent().getStringArrayExtra(EXTRA_ATHLETE_ARRAY);
        if(boatAthletes == null){
            try {
                AthleteLab.get(getContext()).resetAthletesInLineup();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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
            case R.id.menu_item_goto_lineup:
                Intent i = new Intent(getActivity(), LineupActivity.class);
                i.putExtra(EXTRA_ATHLETE_ARRAY, boatAthletes);
                startActivity(i);
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
        private TextView sideTextView;
        private QuickContactBadge imageButtonView;
        private ImageView inLineupImageView;
        private Drawable mDrawable;
        private RelativeLayout athleteRelativeLayout;

        private Athlete athlete;

        public AthleteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            nameTextView = (TextView) itemView.findViewById(R.id.list_item_athlete_name_text_view);
            sideTextView = (TextView) itemView.findViewById(R.id.list_item_athlete_subtitle_text_view);
            imageButtonView = (QuickContactBadge) itemView.findViewById(R.id.user_image_icon);
            inLineupImageView = (ImageView) itemView.findViewById(R.id.athlete_to_lineup);
            athleteRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.list_item_relative_layout);

        }

        public void bindAthlete(Athlete bAthlete) {
            athlete = bAthlete;
            String full = athlete.getFirstName() + " " + athlete.getLastName();
            nameTextView.setText(full);

            switch (athlete.getPosition()){
                case 4 :
                    String pos = "Starboard";
                    sideTextView.setText(pos);
                    break;
                case 3:
                    pos = "Port";
                    sideTextView.setText(pos);
                    break;
                case 2:
                    pos = "Both";
                    sideTextView.setText(pos);
                    break;
                case 1:
                    pos = "Coxswain";
                    sideTextView.setText(pos);
                    break;
            }

            if(athlete.getInLineup()) {
                inLineupImageView.setColorFilter(ContextCompat.getColor(getContext(),R.color.colorPrimaryDark));
                athleteRelativeLayout.setBackgroundColor(Color.parseColor("#d5d5d6"));
            }else {
                inLineupImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), LineupActivity.class);
                        intent.putExtra(EXTRA_ATHLETE_ID, athlete.getId());
                        intent.putExtra(EXTRA_ATHLETE_ARRAY, boatAthletes);
                        if(boatPosition!= -1){
                            intent.putExtra(EXTRA_BOAT_POSITION, boatPosition);
                        }
                        startActivity(intent);

                    }
                });
            }


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
