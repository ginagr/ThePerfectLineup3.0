package ggr.tpl7;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LineupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_ATHLETE_ARRAY = "ggr.tpl17.athlete_array";

    private String[] athletes = new String[9];

    private boolean fromList = false;
    private Athlete athlete;
    private String pos = "";
    private TextView currNameTextView;
    private TextView currSideTextView;

    private Button[] buttons;
    private TextView[] texts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineup);

        UUID athleteId = (UUID) getIntent().getSerializableExtra(EXTRA_ATHLETE_ID);
        athletes = getIntent().getStringArrayExtra(EXTRA_ATHLETE_ARRAY);

        listen();

        if(athletes != null) {
            try {
                setUp();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            athletes = new String[9];
        }

        if(athletes != null) {
            Log.e("LineupActivity", "Found athlete " + athletes[0]);
        } else {
            Log.e("LineupActivity", "Found athlete after intent 0");
        }

        if(athleteId != null) {
            try {
                athlete = AthleteLab.get(this).getAthlete(athleteId);
                fromList = true;
                addAthlete();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUp() throws ParseException {
        AthleteLab athleteLab = AthleteLab.get(this);
        for(int i = 0; i < 9; i++){
            if(athletes[i] != null){
                Athlete currAthlete = athleteLab.getAthlete(UUID.fromString(athletes[i]));
                String name = currAthlete.getFirstName();
                if(currAthlete.getLastName() != null && !currAthlete.getLastName().isEmpty()) {
                   name = currAthlete.getFirstName() + " " + currAthlete.getLastName().charAt(0);
                }
                texts[i].setText(name);
                try {
                    Log.e("LineupActivity", "Set up:Found athlete " + athleteLab.getAthlete(UUID.fromString(athletes[i])).getFirstName());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void addAthlete(){
        currNameTextView = (TextView) findViewById(R.id.current_rower_name);
        String name = athlete.getFirstName();
        if(athlete.getLastName() != null && !athlete.getLastName().isEmpty()) {
            name = athlete.getFirstName() + " " + athlete.getLastName().charAt(0);
        }
        currNameTextView.setText(name);

        currSideTextView = (TextView) findViewById(R.id.current_rower_side);

        switch (athlete.getPosition()){
            case 4 :
                pos = "Starboard";
                currSideTextView.setText(pos);
                break;
            case 3:
                pos = "Port";
                currSideTextView.setText(pos);
                break;
            case 2:
                pos = "Both";
                currSideTextView.setText(pos);
                break;
            case 1:
                pos = "Coxswain";
                currSideTextView.setText(pos);
                break;
        }
    }

    private void listen(){
        Button coxButton = (Button) findViewById(R.id.lineup_cox_image_button_view);
        Button eightButton = (Button) findViewById(R.id.lineup_8_image_button_view);
        Button sevenButton = (Button) findViewById(R.id.lineup_7_image_button_view);
        Button sixButton = (Button) findViewById(R.id.lineup_6_image_button_view);
        Button fiveButton = (Button) findViewById(R.id.lineup_5_image_button_view);
        Button fourButton = (Button) findViewById(R.id.lineup_4_image_button_view);
        Button threeButton = (Button) findViewById(R.id.lineup_3_image_button_view);
        Button twoButton = (Button) findViewById(R.id.lineup_2_image_button_view);
        Button oneButton = (Button) findViewById(R.id.lineup_1_image_button_view);

        coxButton.setOnClickListener(this);
        eightButton.setOnClickListener(this);
        sevenButton.setOnClickListener(this);
        sixButton.setOnClickListener(this);
        fiveButton.setOnClickListener(this);
        fourButton.setOnClickListener(this);
        threeButton.setOnClickListener(this);
        twoButton.setOnClickListener(this);
        oneButton.setOnClickListener(this);

        buttons = new Button[]{coxButton, eightButton, sevenButton, sixButton, fiveButton,
                fourButton, threeButton, twoButton, oneButton};

        TextView coxText = (TextView) findViewById(R.id.lineup_cox_text_name_view);
        TextView eightText = (TextView) findViewById(R.id.lineup_8_text_name_view);
        TextView sevenText = (TextView) findViewById(R.id.lineup_7_text_name_view);
        TextView sixText = (TextView) findViewById(R.id.lineup_6_text_name_view);
        TextView fiveText = (TextView) findViewById(R.id.lineup_5_text_name_view);
        TextView fourText = (TextView) findViewById(R.id.lineup_4_text_name_view);
        TextView threeText = (TextView) findViewById(R.id.lineup_3_text_name_view);
        TextView twoText = (TextView) findViewById(R.id.lineup_2_text_name_view);
        TextView oneText = (TextView) findViewById(R.id.lineup_1_text_name_view);

        texts = new TextView[]{coxText, eightText, sevenText, sixText, fiveText,
                fourText, threeText, twoText, oneText};

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.lineup_cox_image_button_view:
                if(fromList) {
                    TextView coxTextView = (TextView) findViewById(R.id.lineup_cox_text_name_view);
                    String name = athlete.getFirstName();
                    if (athlete.getLastName() != null && !athlete.getLastName().isEmpty()) {
                        name = athlete.getFirstName() + " " + athlete.getLastName().charAt(0);
                    }
                    coxTextView.setText(name);
                    athletes[0] = athlete.getId().toString();
                    Log.e("LineupActivity", "Found athlete after add " + athletes[0]);
                }
                break;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_lineup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_goto_roster:
                Intent i = new Intent(this, AthleteListActivity.class);
                if(athletes != null) {
                    Log.e("LineupActivity", "Found athlete before intent" + athletes[0]);
                } else {
                    Log.e("LineupActivity", "Found athlete before intent 0");
                }
                i.putExtra(EXTRA_ATHLETE_ARRAY, athletes);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
