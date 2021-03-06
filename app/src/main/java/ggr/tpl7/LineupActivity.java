package ggr.tpl7;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import ggr.tpl7.model.Athlete;
import ggr.tpl7.model.AthleteLab;
import ggr.tpl7.model.Boat;
import ggr.tpl7.model.BoatLab;
import ggr.tpl7.model.BoatSize;
import ggr.tpl7.model.Position;

import static ggr.tpl7.R.string.avg_2k;
import static ggr.tpl7.R.string.wt_adj_2k;
import static ggr.tpl7.model.AthleteLab.formatDateToString;

public class LineupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EXTRA_ATHLETE_ID = "ggr.tpl17.athlete_id";
    private static final String EXTRA_BOAT_POSITION = "ggr.tpl17.boat_position";
    private static final String EXTRA_CURRENT_BOAT = "ggr.tpl17.current_boat";

    private boolean seatChosen;

    private TextView[] texts;

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
                currentBoat.setCurrent(true);
                BoatLab.get(this).updateBoat(currentBoat);
                for(int i = 1; i < allBoats.size(); i++){
                    //make sure there are not multiple current boats
                    allBoats.get(i).setCurrent(false);
                    BoatLab.get(this).updateBoat(allBoats.get(i));
                }
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
            } else {
                seatChosen = false;
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
        seatChosen = false;
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
                texts[i].setTypeface(null, Typeface.NORMAL);
            }
            if(currentBoat.isCox()){
                texts[0].setText(Position.COXSWAIN.toString());
                texts[0].setTypeface(null, Typeface.NORMAL);
            }
        } else { //at least one rower in boat
            for(int i = 0; i < athletesInBoat.size(); i++){
                Athlete tempAthlete = athletesInBoat.get(i);
                int seat = tempAthlete.getSeat();
                texts[seat].setText(getAthleteName(tempAthlete));
                texts[seat].setTypeface(null, Typeface.BOLD);
                //boatButtons[seat].setBackground(); TODO: figure out drawable
            }
        }
        checkBoatStats();
    }

    private String getAthleteName(Athlete a){
        String name = a.getFirstName();
        if(a.getLastName() != null && !a.getLastName().isEmpty()) {
            name = a.getFirstName() + " " + a.getLastName().charAt(0);
        }
        return name;
    }

    private void checkBoatStats(){
        double total = 0;
        double totalW = 0;
        int count = 0;
        int countW = 0;
        double weight = 0;
        List<Athlete> athletesInBoat = AthleteLab.get(this).getAthletesByBoat(currentBoat.getId());
        for(int i = 0; i < athletesInBoat.size(); i++){
            if(athletesInBoat.get(i).getTwok() != null) {
                total += (athletesInBoat.get(i).getTwok().getMinutes() * 60) + athletesInBoat.get(i).getTwok().getSeconds();
                count++;
                if(athletesInBoat.get(i).getWeight() > 0){
                    countW++;
                    totalW += (athletesInBoat.get(i).getTwok().getMinutes() * 60) + athletesInBoat.get(i).getTwok().getSeconds();
                    weight += athletesInBoat.get(i).getWeight();
                }
            }
        }

        getAvg2k(total, count);
        getAvgW2k(totalW, countW, weight);
    }

    private void getAvgW2k(double totalW, int countW, double weight){
        if(totalW > 0){
            double wf = Math.pow((weight / (270.0 * countW)), .222);
            double avg = (totalW / countW) * wf;
            double fin = avg * 1000.0;
            Date ret = new Date((long)fin);

            TextView curr2kTextView = (TextView) findViewById(R.id.current_boat_weight_adj_2k);
            curr2kTextView.setText(getString(wt_adj_2k) + " " + formatDateToString(ret));
        } else {
            TextView curr2kTextView = (TextView) findViewById(R.id.current_boat_weight_adj_2k);
            curr2kTextView.setText("" + getString(wt_adj_2k) + " no data");
        }

         /*
         Wf = [body weight in lbs / 270] raised to the power .222
         Corrected time = Wf x actual time (seconds)
         Corrected distance = actual distance / Wf
         */
    }

    private void getAvg2k(double total, int count){
        if(total > 0) {
            double avg = (total / count) * 1000.0;
            Date ret = new Date((long)avg);

            TextView curr2kTextView = (TextView) findViewById(R.id.current_boat_2k);
            curr2kTextView.setText(getString(avg_2k) + " " + formatDateToString(ret));
        } else {
            TextView curr2kTextView = (TextView) findViewById(R.id.current_boat_2k);
            curr2kTextView.setText("" + getString(avg_2k) + " no data");
        }
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

        Button[] lineupButtons = new Button[]{coxButton, oneButton, twoButton, threeButton, fourButton,
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
            texts[i].setTypeface(null, Typeface.NORMAL);
        }
        if(currentBoat.isCox()) {
            texts[0].setTypeface(null, Typeface.NORMAL);
            text = "Coxswain";
            texts[0].setText(text);
        }
    }

    private void updateText() {
        List<Athlete> athletes = AthleteLab.get(this).getAthletesByBoat(currentBoatId);
        boolean taken = false;
        for (int i = 0; i < athletes.size(); i++) { //check if athlete already in seat
            if (athletes.get(i).getSeat() == athleteBoatPosition) {
                //  alertChange(athletes.get(i));
                taken = true;
                break;
            }
        }
        if(taken) {
            Toast.makeText(this,"Spot already taken",Toast.LENGTH_LONG).show();
            //TODO: CREATE OPTIONS OF DELETE/CHANGE ATHLETE/SEE STATS
        } else {
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
                Log.e("LineupActivity", "Going back to roster");
                Intent i = new Intent(this, AthleteListActivity.class);
                i.putExtra(EXTRA_CURRENT_BOAT, currentBoatId);
                startActivity(i);
                return true;

            case R.id.clear_lineup:
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
