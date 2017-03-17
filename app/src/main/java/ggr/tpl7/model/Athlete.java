package ggr.tpl7.model;

import java.util.UUID;

public class Athlete {

    private static final int MAXPHOTOS = 1;
    private UUID id;
    private String firstName = "";
    private String lastName = "";
    private Position position; //1 = Cox, 2 = both, 3 = port, 4 = starboard, 5 = none
    private int feet;
    private int inches;
    private int weight;
    private int twokMin;
    private double twokSec; //TODO: change database
    private String linkContact;
    private boolean inLineup = false;
    private UUID boatId;
    private int seat;


    public Athlete() {
        id = UUID.randomUUID();
        inLineup = false;
    }

    public Athlete(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        position = Position.NONE;
        inLineup = false;
    }

    public Athlete(String firstName, String lastName, Position position
    , int feet, int inches, int weight, int twokMin, double twokSec) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.feet = feet;
        this.inches = inches;
        this.weight = weight;
        this.twokMin = twokMin;
        this.twokSec = twokSec;
        id = UUID.randomUUID();
        inLineup = false;
    }

    public String[] getPhotoFilenames() {
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
        this.firstName = firstName;
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

    public int getFeet() {
        return feet;
    }

    public void setFeet(int feet) {
        this.feet = feet;
    }

    public int getInches() {
        return inches;
    }

    public void setInches(int inches) {
        this.inches = inches;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getTwokMin() {
        return twokMin;
    }

    public void setTwokMin(int twokMin) {
        this.twokMin = twokMin;
    }

    public double getTwokSec() {
        return twokSec;
    }

    public void setTwokSec(double twokSec) {
        this.twokSec = twokSec;
    }

    public String getLinkContact() {
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
