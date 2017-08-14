package ggr.tpl7.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Athlete {

    private static final int MAXPHOTOS = 1;
    private UUID id;
    private String firstName = "";
    private String lastName = "";
    private Position position; //1 = Cox, 2 = both, 3 = port, 4 = starboard, 5 = none
    private int feet;
    private int inches;
    private double weight;
    private Date twok;
    private String linkContact;
    private boolean inLineup = false;
    private UUID boatId;
    private int seat;


    public Athlete() {
        id = UUID.randomUUID();
        inLineup = false;

//        SimpleDateFormat ft = new SimpleDateFormat("HH:mm.ss");
//        Date t;
//        try {
//            t = ft.parse("00:00.00");
//            twok = t;
//        } catch (Exception e) {
//            twok = new Date();
//            e.printStackTrace();
//        }
    }

    public Athlete(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        position = Position.NONE;
        inLineup = false;

    }

    public Athlete(String firstName, String lastName, Position position
    , int feet, int inches, int weight, Date twok) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.feet = feet;
        this.inches = inches;
        this.weight = weight;
        this.twok = twok;
        id = UUID.randomUUID();
        inLineup = false;
    }

    String[] getPhotoFilenames() {
        String[] photoFilenames = new String[MAXPHOTOS];

        for (int n = 0; n < MAXPHOTOS; n++) {
            photoFilenames[n] = "IMG_" + getId().toString() + "_" + n + ".jpg";
        }

        return photoFilenames;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBoatId() {
        if(boatId == null){
            return id;
        }
        return boatId;
    }

    public void setBoatId(UUID boatId) {
        this.boatId = boatId;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(firstName);
        boolean found = matcher.find();
        if(found){
            String[] splited = firstName.split("\\s+");
            this.firstName = splited[0];
            this.lastName = splited[1];
        } else {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Position getPosition() {
        if(position == null){
            return Position.NONE;
        }
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    int getFeet() {
        return feet;
    }

    public void setFeet(int feet) {
        this.feet = feet;
    }

    int getInches() {
        return inches;
    }

    public void setInches(int inches) {
        this.inches = inches;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getTwok() {
        if (twok == null){
            return null;
        }
        return twok;
    }

    public void setTwok(Date twok) {
        this.twok = twok;
    }

    String getLinkContact() {
        return linkContact;
    }

    public void setLinkContact(String linkContact) {
        this.linkContact = linkContact;
    }

    public boolean getInLineup() {
        return inLineup;
    }

    public void setInLineup(boolean inLineup) {
        this.inLineup = inLineup;
    }

}
