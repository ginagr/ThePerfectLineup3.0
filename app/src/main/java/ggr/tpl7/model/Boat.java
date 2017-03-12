package ggr.tpl7.model;

import java.util.UUID;

public class Boat {

    private UUID id;
    private BoatSize boatSize;
    private boolean cox;
    private String name;
    private boolean isCurrent;

    public Boat() {
        id = UUID.randomUUID();
    }

    public Boat(BoatSize boatSize, boolean cox, String name){
        this.boatSize = boatSize;
        this.cox = cox;
        this.name = name;
        this.id = UUID.randomUUID();
    }

    public Boat(UUID id, BoatSize boatSize, boolean cox, String name){
        this.boatSize = boatSize;
        this.cox = cox;
        this.name = name;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public BoatSize getBoatSize() {
        return boatSize;
    }

    public void setBoatSize(BoatSize boatSize) {
        this.boatSize = boatSize;
    }

    public boolean isCox() {
        return cox;
    }

    public void setCox(boolean cox) {
        this.cox = cox;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }


    public String getName() {
        if(name == null){
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
