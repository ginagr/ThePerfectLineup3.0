package ggr.tpl7;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.util.UUID;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;

public class LineupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_ATHLETE_ARRAY = "ggr.tpl17.athlete_array";
    private static final String EXTRA_BOAT_POSITION = "ggr.tpl17.boat_position";
    private static final String EXTRA_CURRENT_BOAT = "ggr.tpl17.current_boat";


    private String[] boatAthletes = new String[45];

    private boolean fromList = false;
    private Athlete athlete;
    private String athleteName;
    private String pos = "";
    private TextView currNameTextView;
    private TextView currSideTextView;

    private Button[] lineupButtons;
    private TextView[] texts;
    private Button[] boatButtons;

    private int athleteBoatPosition;
    public int currentBoat;

    public int getCurrentBoat(){return currentBoat;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineup);

        athleteBoatPosition = getIntent().getIntExtra(EXTRA_BOAT_POSITION, -1);
        UUID athleteId = (UUID) getIntent().getSerializableExtra(EXTRA_ATHLETE_ID);
        boatAthletes = getIntent().getStringArrayExtra(EXTRA_ATHLETE_ARRAY);
        currentBoat = getIntent().getIntExtra(EXTRA_CURRENT_BOAT, -1);
        if(currentBoat == -1){ currentBoat = 0; }

        listen();

        if (boatAthletes != null) {
            try {
                setUp();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            boatAthletes = new String[45];
            try {
                AthleteLab.get(this).resetAthletesInLineup();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if((athleteId != null) && (athleteBoatPosition != -1)){
            try {
                Athlete currAthlete = AthleteLab.get(this).getAthlete(athleteId);
                String name = currAthlete.getFirstName();
                if(currAthlete.getLastName() != null && !currAthlete.getLastName().isEmpty()) {
                    name = currAthlete.getFirstName() + " " + currAthlete.getLastName().charAt(0);
                }
                int loc = athleteBoatPosition - (currentBoat*9);
                texts[loc].setText(name);
                boatAthletes[athleteBoatPosition] = currAthlete.getId().toString();
                currAthlete.setInLineup(true);
                AthleteLab.get(this).updateAthlete(currAthlete);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {

            if (athleteId != null) {
                try {
                    athlete = AthleteLab.get(this).getAthlete(athleteId);
                    fromList = true;
                    athleteName = athlete.getFirstName();
                    if (athlete.getLastName() != null && !athlete.getLastName().isEmpty()) {
                        athleteName = athlete.getFirstName() + " " + athlete.getLastName().charAt(0);
                    }
                    addAthlete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.e("LineupActivity", "currentBoat in oncreate: " + currentBoat);
        if(currentBoat != 0) {
            boatButtons[currentBoat].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
            boatButtons[0].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
        }
    }

    private void setUp() throws ParseException {
        Log.e("LineupActivity", "currentBoat at setup: " + currentBoat);
        AthleteLab athleteLab = AthleteLab.get(this);
        int start = currentBoat*9;
        int end = start+9;
        int count = 0;
        for(int i = start; i < end; i++){
            if(boatAthletes[i] != null){
                Athlete currAthlete = athleteLab.getAthlete(UUID.fromString(boatAthletes[i]));
                String name = currAthlete.getFirstName();
                if(currAthlete.getLastName() != null && !currAthlete.getLastName().isEmpty()) {
                   name = currAthlete.getFirstName() + " " + currAthlete.getLastName().charAt(0);
                }
                texts[count].setText(name);
            }
            count++;
        }

    }


    private void addAthlete(){
        currNameTextView = (TextView) findViewById(R.id.current_rower_name);
        currNameTextView.setText(athleteName);

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

        lineupButtons = new Button[]{coxButton, eightButton, sevenButton, sixButton, fiveButton,
                fourButton, threeButton, twoButton, oneButton};

        Button boatOneButton = (Button) findViewById(R.id.boat_one);
        Button boatTwoButton = (Button) findViewById(R.id.boat_two);
        Button boatThreeButton = (Button) findViewById(R.id.boat_three);
        Button boatFourButton = (Button) findViewById(R.id.boat_four);
        Button boatFiveButton = (Button) findViewById(R.id.boat_five);

        boatOneButton.setOnClickListener(this);
        boatTwoButton.setOnClickListener(this);
        boatThreeButton.setOnClickListener(this);
        boatFourButton.setOnClickListener(this);
        boatFiveButton.setOnClickListener(this);

        boatButtons = new Button[] {boatOneButton, boatTwoButton, boatThreeButton, boatFourButton, boatFiveButton};

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
    public void onClick(View v){
        switch(v.getId()){
            case R.id.lineup_cox_image_button_view:
                updateText(currentBoat*9, 0);
                break;
            case R.id.lineup_8_image_button_view:
                updateText((currentBoat*9)+1, 1);
                break;
            case R.id.lineup_7_image_button_view:
                updateText((currentBoat*9)+2, 2);
                break;
            case R.id.lineup_6_image_button_view:
                updateText((currentBoat*9)+3, 3);
                break;
            case R.id.lineup_5_image_button_view:
                updateText((currentBoat*9)+4, 4);
                break;
            case R.id.lineup_4_image_button_view:
                updateText((currentBoat*9)+5, 5);
                break;
            case R.id.lineup_3_image_button_view:
                updateText((currentBoat*9)+6, 6);
                break;
            case R.id.lineup_2_image_button_view:
                updateText((currentBoat*9)+7, 7);
                break;
            case R.id.lineup_1_image_button_view:
                updateText((currentBoat*9)+8, 8);
                break;
            case R.id.boat_one:
                boatButtons[currentBoat].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
                boatButtons[0].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
                currentBoat = 0;
                updateBoat(currentBoat);
                break;
            case R.id.boat_two:
                boatButtons[currentBoat].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
                boatButtons[1].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
                currentBoat = 1;
                updateBoat(currentBoat);
                break;
            case R.id.boat_three:
                boatButtons[currentBoat].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
                boatButtons[2].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
                currentBoat = 2;
                updateBoat(currentBoat);
                break;
            case R.id.boat_four:
                boatButtons[currentBoat].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
                boatButtons[3].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
                currentBoat = 3;
                updateBoat(currentBoat);
                break;
            case R.id.boat_five:
                boatButtons[currentBoat].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
                boatButtons[4].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
                currentBoat = 4;
                updateBoat(currentBoat);
                break;

        }
    }

    private void updateBoat(int lastBoat){
        String text;
        for(int i = 1; i < 9; i++){
                text = (9 - i) + " Seat";
                texts[i].setText(text);
        }
        text = "Coxswain";
        texts[0].setText(text);
        try {
            setUp();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateText(int position, int pos){
        if(fromList) {
            if(boatAthletes[position] == null) {
                texts[pos].setText(athleteName);
                boatAthletes[position] = athlete.getId().toString();
                athlete.setInLineup(true);
                AthleteLab.get(this).updateAthlete(athlete);
                fromList = false;
            } else {
                alertChange(position, athlete, pos);
            }
        } else {
            Intent i = new Intent(this, AthleteListActivity.class);
            i.putExtra(EXTRA_BOAT_POSITION, position);
            i.putExtra(EXTRA_ATHLETE_ARRAY, boatAthletes);
            i.putExtra(EXTRA_CURRENT_BOAT, currentBoat);
            startActivity(i);
        }
    }

    private void alertChange(final int position, final Athlete currAthlete, final int pos) {
        try {
            final Athlete pastAthlete = AthleteLab.get(this).getAthlete(UUID.fromString(boatAthletes[position]));
            final AthleteLab athleteLab = AthleteLab.get(this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to switch athletes?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            pastAthlete.setInLineup(false);
                            currAthlete.setInLineup(true);
                            athleteLab.updateAthlete(pastAthlete);
                            athleteLab.updateAthlete(currAthlete);
                            texts[pos].setText(athleteName);
                            boatAthletes[position] = currAthlete.getId().toString();
                            fromList = false;
                        }
                    });
            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
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
                i.putExtra(EXTRA_ATHLETE_ARRAY, boatAthletes);
                startActivity(i);
                return true;
            case R.id.clear_lineup:
                final AthleteLab athleteLab = AthleteLab.get(this);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you want to clear this lineup?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                try {
                                    athleteLab.resetAthletesInLineup();
                                    int start = currentBoat*9;
                                    int end = start+9;
                                    int count = 0;
                                    for(int i = start; i < end; i++){
                                        if(boatAthletes[i] != null){
                                            String text = (9-i) + " Seat";
                                            texts[count].setText(text);
                                        }
                                        count++;
                                    }
                                    if(boatAthletes[0] != null){
                                        String text = "Coxswain";
                                        texts[0].setText(text);
                                    }
                                    boatAthletes = new String[9];
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
