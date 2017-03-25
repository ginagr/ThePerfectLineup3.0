package ggr.tpl7;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import ggr.tpl7.database.AthleteDbSchema;
import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;

public class AthleteListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_ATHLETE_ARRAY = "ggr.tpl17.athlete_array";
    private static final String EXTRA_BOAT_POSITION = "ggr.tpl17.boat_position";
    private static final String EXTRA_CURRENT_BOAT = "ggr.tpl17.current_boat";

    private RecyclerView athleteRecyclerView;
    protected AthleteAdapter adapter;
    private boolean mSubtitleVisible;

    private int athleteBoatPosition;

    private UUID currAthleteId;
    private UUID currBoatId;

    private boolean fromRosterButton;

    private Button alphabetical;
    private Button notBoated;
    private int button; //TODO: change to other identifier

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        athleteBoatPosition = getActivity().getIntent().getIntExtra(EXTRA_BOAT_POSITION, -1);
        currAthleteId = (UUID) getActivity().getIntent().getSerializableExtra(EXTRA_ATHLETE_ID);

        currBoatId = (UUID) getActivity().getIntent().getSerializableExtra(EXTRA_CURRENT_BOAT);

        if(athleteBoatPosition == -1 && currAthleteId == null && currBoatId == null){
            fromRosterButton = true;
        } else {
            fromRosterButton = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_athlete_list, container, false);
        button = 1;

        alphabetical = (Button) view.findViewById(R.id.alphabetical_button);
        notBoated = (Button) view.findViewById(R.id.not_boated_button);
        alphabetical.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("AthleteListActivity", "Sorting alphabetically");
                alphabetical.setPressed(true);
                alphabetical.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorAccent));
                notBoated.setPressed(false);
                notBoated.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimaryDark));
                button = 1;
                try {
                    updateUI();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        notBoated.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("AthleteListActivity", "Sorting boated");
                notBoated.setPressed(true);
                notBoated.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorAccent));
                alphabetical.setPressed(false);
                alphabetical.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimaryDark));
                button = 0;
                try {
                    updateUI();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        athleteRecyclerView = (RecyclerView) view.findViewById(R.id.athlete_recycler_view);
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
                i.putExtra(EXTRA_CURRENT_BOAT, currBoatId);
                startActivity(i);
                return true;
            case R.id.menu_item_update_roster:
                i = new Intent(getActivity(), ReadExcel.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() throws ParseException {
        AthleteLab athleteLab = AthleteLab.get(getActivity());
        List<Athlete> athletes = athleteLab.getAthletes(null);
        int athleteCount  = athletes.size();
        int boated = 0;
        for(int i = 0; i < athleteCount; i++){
            if(athletes.get(i).getInLineup()){
                boated++;
            }
        }
        String subtitle = boated + "/" + athleteCount + " athletes boated";

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    protected void updateUI() throws ParseException {
        AthleteLab athleteLab = AthleteLab.get(getActivity());

        List<Athlete> athlete;
        if(button == 0){
           athlete = athleteLab.getAthletes(AthleteDbSchema.AthleteTable.Cols.INLINEUP +" ASC, " + AthleteDbSchema.AthleteTable.Cols.FIRSTNAME);
        } else {
            athlete = athleteLab.getAthletes(AthleteDbSchema.AthleteTable.Cols.FIRSTNAME +" ASC");
        }

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
        private ImageView editAthlete;
        private Drawable mDrawable;
        private RelativeLayout athleteRelativeLayout;

        private Athlete athlete;

        public AthleteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            nameTextView = (TextView) itemView.findViewById(R.id.list_item_athlete_name_text_view);
            sideTextView = (TextView) itemView.findViewById(R.id.list_item_athlete_subtitle_text_view);
            imageButtonView = (QuickContactBadge) itemView.findViewById(R.id.user_image_icon);
            editAthlete = (ImageView) itemView.findViewById(R.id.list_item_edit_athlete);
            athleteRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.list_item_relative_layout);

        }

        public void bindAthlete(Athlete bAthlete) {
            athlete = bAthlete;
            String full = athlete.getFirstName() + " " + athlete.getLastName();
            nameTextView.setText(full);
            sideTextView.setText(athlete.getPosition().toString());

            if(athlete.getInLineup()) {
                athleteRelativeLayout.setBackgroundColor(Color.parseColor("#d5d5d6"));
            }else {
                athleteRelativeLayout.setBackgroundColor(Color.parseColor("#FFFAFFFF"));
                editAthlete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = AthletePagerActivity.newIntent(getActivity(), athlete.getId());
                        startActivity(intent);
                    }
                });
            }

            //TODO:get image path
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), LineupActivity.class);
            i.putExtra(EXTRA_ATHLETE_ID, athlete.getId());
            i.putExtra(EXTRA_CURRENT_BOAT, currBoatId);
            if(fromRosterButton){
                startActivity(i);
            }
            i.putExtra(EXTRA_BOAT_POSITION, athleteBoatPosition);
            startActivity(i);
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
