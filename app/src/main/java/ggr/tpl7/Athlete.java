package ggr.tpl7;

import java.util.UUID;

public class Athlete {

    private static final int MAXPHOTOS = 1;
    private UUID id;
    private String firstName;
    private String lastName;
    private int position; //1 = Cox, 2 = Port, 3 = Starboard  TODO: turn to enum
    private int feet;
    private int inches;
    private int weight;
    private int twokMin;
    private double twokSec;
    private String linkContact; //TODO: add to database

    public Athlete() {
        id = UUID.randomUUID();
        firstName = "";
        lastName = "";
        twokMin = 0;
        twokSec = 0;
    }

    public Athlete(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        twokMin = 0;
        twokSec = 0;
    }

    public Athlete(String firstName, String lastName, int position
    , int feet, int inches, int weight, int twokMin, double twokSec) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.feet = feet;
        this.inches = inches;
        this.weight = weight;
        this.twokMin = twokMin;
        this.twokSec = twokSec;
        this.id = UUID.randomUUID();
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
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

}
