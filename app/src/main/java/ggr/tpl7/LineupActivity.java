package ggr.tpl7;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;
import ggr.tpl7.model.Boat;
import ggr.tpl7.model.BoatLab;
import ggr.tpl7.model.BoatSize;
import ggr.tpl7.model.Position;

public class LineupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_BOAT_POSITION = "ggr.tpl17.boat_position";
    private static final String EXTRA_CURRENT_BOAT = "ggr.tpl17.current_boat";

    private boolean seatChosen;

    private Button[] lineupButtons;
    private TextView[] texts;

    private Button[] boatButtons;

    private int athleteBoatPosition;
    private UUID currAthleteId;
    private Athlete currAthlete;
    public UUID currentBoatId;
    public Boat currentBoat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lineup);

        List<Boat> boats = BoatLab.get(this).getBoats();
        if(boats.isEmpty()){
            Boat defaultBoat = new Boat();
            defaultBoat.setName("Boat 1");
            defaultBoat.setBoatSize(BoatSize.EIGHT);
            defaultBoat.setCox(true);
            defaultBoat.setCurrent(true);
            BoatLab.get(this).addBoat(defaultBoat);
        }

//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = new BoatListFragment();
//
//        if (fragment == null) {
//            fragment = createFragment();
//            fm.beginTransaction()
//                    .add(R.id.fragment_container_boat1, fragment)
//                    .commit();
//        }

        athleteBoatPosition = getIntent().getIntExtra(EXTRA_BOAT_POSITION, -1);
        currAthleteId = (UUID) getIntent().getSerializableExtra(EXTRA_ATHLETE_ID);

        currentBoatId = (UUID) getIntent().getSerializableExtra(EXTRA_CURRENT_BOAT);

        List<Boat> allBoats = BoatLab.get(this).getBoats();

        //no boat button is pressed
        if (currentBoatId == null) {
            //make sure there is at least one boat created
            if(allBoats != null && !allBoats.isEmpty()){
                currentBoat = allBoats.get(0);
                currentBoatId = currentBoat.getId();
                setUp();
            } else {
                //temp boat made that is an eight
                Boat temp = new Boat();
                temp.setBoatSize(BoatSize.EIGHT);
                currentBoat = temp;
                currentBoatId = temp.getId();
                setUp();
            }
        } else {
            currentBoat = BoatLab.get(this).getBoat(currentBoatId);
            setUp();
        }


        if(currAthleteId != null) {
            if (athleteBoatPosition != -1) { //coming back from already chosen seat
                currAthlete = AthleteLab.get(this).getAthlete(currAthleteId);
                Log.e("LineupActivity", "Found athlete " + currAthlete.getFirstName());
                seatChosen = true;
                addAthleteToSeat();
            } else { //put athlete info in box and wait for seat click
                currAthlete = AthleteLab.get(this).getAthlete(currAthleteId);
                Log.e("LineupActivity", "Found athlete " + currAthlete.getFirstName());
                seatChosen = false;
                addAthleteToBox();
            }
        } else {
            seatChosen = false;
        }

        Log.e("LineupActivity", "currentBoat in oncreate: " + currentBoat.getName());
    }

    private void addAthleteToSeat() {
        Log.e("LINEUP", currAthlete.getFirstName() + " in boat: " + currAthlete.getBoatId());
        currAthlete.setBoatId(currentBoat.getId());
        currAthlete.setSeat(athleteBoatPosition);
        currAthlete.setInLineup(true);
        AthleteLab.get(this).updateAthlete(currAthlete);
        Athlete temp = AthleteLab.get(this).getAthlete(currAthlete.getId());
        Log.e("LINEUP", temp.getFirstName() + " in boat: " + temp.getBoatId());
        populateBoat();
    }

    private void setUp() {
        Log.e("LineupActivity", "currentBoat at setup: " + currentBoat.getName());
        int num = currentBoat.getBoatSize().toInt();

        listen(num, currentBoat.isCox());

        populateBoat();
    }

    private void populateBoat(){

        clearBoat();

        List<Athlete> athletesInBoat = AthleteLab.get(this).getAthletesByBoat(currentBoat.getId());
        Log.e("LineupActivity", "Number of athletes in boat: " + athletesInBoat.size());
        //empty boat
        if(athletesInBoat.size() == 0) {
            for(int i = 1; i < currentBoat.getBoatSize().toInt(); i++) {
                String text = i + " Seat";
                texts[i].setText(text);
            }
            if(currentBoat.isCox()){
                texts[0].setText(Position.COXSWAIN.toString());
            }
        } else { //at least one rower in boat
            for(int i = 0; i < athletesInBoat.size(); i++){
                Athlete tempAthlete = athletesInBoat.get(i);
                int seat = tempAthlete.getSeat();
                texts[seat].setText(getAthleteName(tempAthlete));
                //boatButtons[seat].setBackground(); TODO: figure out drawable
            }
        }
    }

    private String getAthleteName(Athlete a){
        String name = a.getFirstName();
        if(a.getLastName() != null && !a.getLastName().isEmpty()) {
            name = a.getFirstName() + " " + a.getLastName().charAt(0);
        }
        return name;
    }


    private void addAthleteToBox(){
        String name = getAthleteName(currAthlete);
        TextView currNameTextView = (TextView) findViewById(R.id.current_rower_name);
        currNameTextView.setText(name);

        TextView currSideTextView = (TextView) findViewById(R.id.current_rower_side);
        currSideTextView.setText(currAthlete.getPosition().toString());
    }

    private void listen(int num, boolean cox){
        Button coxButton = (Button) findViewById(R.id.lineup_cox_image_button_view);
        Button eightButton = (Button) findViewById(R.id.lineup_8_image_button_view);
        Button sevenButton = (Button) findViewById(R.id.lineup_7_image_button_view);
        Button sixButton = (Button) findViewById(R.id.lineup_6_image_button_view);
        Button fiveButton = (Button) findViewById(R.id.lineup_5_image_button_view);
        Button fourButton = (Button) findViewById(R.id.lineup_4_image_button_view);
        Button threeButton = (Button) findViewById(R.id.lineup_3_image_button_view);
        Button twoButton = (Button) findViewById(R.id.lineup_2_image_button_view);
        Button oneButton = (Button) findViewById(R.id.lineup_1_image_button_view);

        lineupButtons = new Button[]{ coxButton, oneButton, twoButton, threeButton, fourButton,
                fiveButton, sixButton, sevenButton, eightButton};

        TextView coxText = (TextView) findViewById(R.id.lineup_cox_text_name_view);
        TextView eightText = (TextView) findViewById(R.id.lineup_8_text_name_view);
        TextView sevenText = (TextView) findViewById(R.id.lineup_7_text_name_view);
        TextView sixText = (TextView) findViewById(R.id.lineup_6_text_name_view);
        TextView fiveText = (TextView) findViewById(R.id.lineup_5_text_name_view);
        TextView fourText = (TextView) findViewById(R.id.lineup_4_text_name_view);
        TextView threeText = (TextView) findViewById(R.id.lineup_3_text_name_view);
        TextView twoText = (TextView) findViewById(R.id.lineup_2_text_name_view);
        TextView oneText = (TextView) findViewById(R.id.lineup_1_text_name_view);

        texts = new TextView[]{ coxText, oneText, twoText, threeText, fourText, fiveText, sixText,
        sevenText, eightText };

        for(int i = num; i > 0 ; i--){
            lineupButtons[i].setOnClickListener(this);
        }
        for(int i = 8; i > num; i--){
            lineupButtons[i].setVisibility(View.GONE);
            texts[i].setVisibility(View.GONE);
        }

        if(cox){
            lineupButtons[0].setOnClickListener(this);
        } else {
            lineupButtons[0].setVisibility(View.GONE);
            texts[0].setVisibility(View.GONE);
        }

        ImageView addBoatImageButton = (ImageView) findViewById(R.id.add_boat_button);
        addBoatImageButton.setOnClickListener(this);
    }

    public void changeBoat(Boat newBoat){
        currentBoatId = newBoat.getId();
        currentBoat = newBoat;
        setUp();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.lineup_cox_image_button_view:
                athleteBoatPosition = 0;
                updateText();
                Log.e("LineupActivity", "Clicked Cox");
                break;
            case R.id.lineup_8_image_button_view:
                athleteBoatPosition = 8;
                updateText();
                Log.e("LineupActivity", "Clicked 8");
                break;
            case R.id.lineup_7_image_button_view:
                athleteBoatPosition = 7;
                updateText();
                Log.e("LineupActivity", "Clicked 7");
                break;
            case R.id.lineup_6_image_button_view:
                athleteBoatPosition = 6;
                updateText();
                Log.e("LineupActivity", "Clicked 6");
                break;
            case R.id.lineup_5_image_button_view:
                athleteBoatPosition = 5;
                updateText();
                Log.e("LineupActivity", "Clicked 5");
                break;
            case R.id.lineup_4_image_button_view:
                athleteBoatPosition = 4;
                updateText();
                Log.e("LineupActivity", "Clicked 4");
                break;
            case R.id.lineup_3_image_button_view:
                athleteBoatPosition = 3;
                updateText();
                Log.e("LineupActivity", "Clicked 3");
                break;
            case R.id.lineup_2_image_button_view:
                athleteBoatPosition = 2;
                updateText();
                Log.e("LineupActivity", "Clicked 2");
                break;
            case R.id.lineup_1_image_button_view:
                athleteBoatPosition = 1;
                updateText();
                Log.e("LineupActivity", "Clicked 1");
                break;
            case R.id.add_boat_button:
                Boat newBoat = new Boat();
                newBoat.setBoatSize(BoatSize.EIGHT);
                BoatLab.get(this).addBoat(newBoat);
                Intent intent = BoatPagerActivity
                        .newIntent(this, newBoat.getId());
                startActivity(intent);
                break;
//            case R.id.boat_one:
//                boatButtons[currentBoat].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));
//                boatButtons[0].setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
//                currentBoat = 0;
//                clearBoat(currentBoat);
//                break;
        }
    }

    private void clearBoat(){
        String text;
        for(int i = 1; i < 9; i++){
                text = i + " Seat";
                texts[i].setText(text);
        }
        if(currentBoat.isCox()) {
            text = "Coxswain";
            texts[0].setText(text);
        }
    }

    private void updateText(){
        if(!seatChosen) { //not coming from seat click previously
            if (currAthleteId != null) { //put already chosen athlete into seat
                List<Athlete> athletes = AthleteLab.get(this).getAthletesByBoat(currentBoatId);
                boolean taken = false;
                for (int i = 0; i < athletes.size(); i++) { //check if athlete already in seat
                    if (athletes.get(i).getSeat() == athleteBoatPosition) {
                        alertChange(athletes.get(i));
                        taken = true;
                        i = athletes.size();
                    }
                }
                if (!taken) { //No athlete in spot, put curr one there
                    addAthleteToSeat();
                }
            } else { //no athlete in queue - push intent to list
                Intent i = new Intent(this, AthleteListActivity.class);
                i.putExtra(EXTRA_CURRENT_BOAT, currentBoatId);
                i.putExtra(EXTRA_BOAT_POSITION, athleteBoatPosition);
                startActivity(i);
            }
        } else { //already set seat, send to list for athlete
            Intent i = new Intent(this, AthleteListActivity.class);
            i.putExtra(EXTRA_CURRENT_BOAT, currentBoatId);
            i.putExtra(EXTRA_BOAT_POSITION, athleteBoatPosition);
            startActivity(i);
        }
    }

    private void alertChange(final Athlete oldAthlete) {
        final AthleteLab athleteLab = AthleteLab.get(this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to switch athletes?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            oldAthlete.setInLineup(false);
                            oldAthlete.setBoatId(null);
                            oldAthlete.setSeat(-1);
                            athleteLab.updateAthlete(oldAthlete);

                            addAthleteToSeat();
                            populateBoat();
                            seatChosen = false;
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
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_lineup, menu);
        return true;
    }

    public void clearLineup(){
        List<Athlete> athletes = AthleteLab.get(this).getAthletesByBoat(currentBoat.getId());
        if(athletes.size() < 1) { return; }
        else {
            for(int i = 0; i < athletes.size(); i++){
                athletes.get(i).setInLineup(false);
                athletes.get(i).setBoatId(null);
                athletes.get(i).setSeat(-1);
                AthleteLab.get(this).updateAthlete(athletes.get(i));
            }
            clearBoat();
            Log.e("LineupActivity", "Cleared lineup from boat " + currentBoat.getName());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_goto_roster:
                Intent i = new Intent(this, AthleteListActivity.class);
                i.putExtra(EXTRA_CURRENT_BOAT, currentBoatId);
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
                                clearLineup();
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


    protected Fragment createFragment() {
        return new BoatListFragment();
    }

}
