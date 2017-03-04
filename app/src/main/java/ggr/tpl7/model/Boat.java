package ggr.tpl7.model;

import java.util.UUID;

public class Boat {

    private UUID id;
    private int boatSize;
    private boolean cox;
    private String name;

    public Boat() {
        id = UUID.randomUUID();
    }

    public Boat(int boatSize, boolean cox, String name){
        this.boatSize = boatSize;
        this.cox = cox;
        this.name = name;
        this.id = UUID.randomUUID();
    }

    public Boat(UUID id, int boatSize, boolean cox, String name){
        this.boatSize = boatSize;
        this.cox = cox;
        this.name = name;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public int getBoatSize() {
        return boatSize;
    }

    public void setBoatSize(int boatSize) {
        this.boatSize = boatSize;
    }

    public boolean isCox() {
        return cox;
    }

    public void setCox(boolean cox) {
        this.cox = cox;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
