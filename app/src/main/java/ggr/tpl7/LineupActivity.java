package ggr.tpl7;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LineupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_ATHLETE_ARRAY = "ggr.tpl17.athlete_array";

    private String[] boatAthletes = new String[9];

    private boolean fromList = false;
    private Athlete athlete;
    private String athleteName;
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
        boatAthletes = getIntent().getStringArrayExtra(EXTRA_ATHLETE_ARRAY);

        listen();

        if(athleteId != null) {
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

        if(boatAthletes != null) {
            try {
                setUp();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            boatAthletes = new String[9];
            try {
                AthleteLab.get(this).resetAthletesInLineup();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUp() throws ParseException {
        AthleteLab athleteLab = AthleteLab.get(this);
        for(int i = 0; i < 9; i++){
            if(boatAthletes[i] != null){
                Athlete currAthlete = athleteLab.getAthlete(UUID.fromString(boatAthletes[i]));
                String name = currAthlete.getFirstName();
                if(currAthlete.getLastName() != null && !currAthlete.getLastName().isEmpty()) {
                   name = currAthlete.getFirstName() + " " + currAthlete.getLastName().charAt(0);
                }
                texts[i].setText(name);
            }
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
    public void onClick(View v){
        switch(v.getId()){
            case R.id.lineup_cox_image_button_view:
                updateText(0);
                break;
            case R.id.lineup_8_image_button_view:
                updateText(1);
                break;
            case R.id.lineup_7_image_button_view:
                updateText(2);
                break;
            case R.id.lineup_6_image_button_view:
                updateText(3);
                break;
            case R.id.lineup_5_image_button_view:
                updateText(4);
                break;
            case R.id.lineup_4_image_button_view:
                updateText(5);
                break;
            case R.id.lineup_3_image_button_view:
                updateText(6);
                break;
            case R.id.lineup_2_image_button_view:
                updateText(7);
                break;
            case R.id.lineup_1_image_button_view:
                updateText(8);
                break;
        }
    }

    private void updateText(int position){
        if(fromList) {
            if(boatAthletes[position] == null) {
                texts[position].setText(athleteName);
                boatAthletes[position] = athlete.getId().toString();
                athlete.setInLineup(true);
                AthleteLab.get(this).updateAthlete(athlete);
            } else {
                alertChange(position, athlete);
            }
        }
    }

    private void alertChange(final int position, final Athlete currAthlete) {
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
                            texts[position].setText(athleteName);
                            boatAthletes[position] = currAthlete.getId().toString();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
