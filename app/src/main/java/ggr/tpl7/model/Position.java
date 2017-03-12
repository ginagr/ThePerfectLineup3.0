package ggr.tpl7.model;

public enum Position {
   COXSWAIN, PORT, STARBOARD, BOTH, NONE;

    @Override
    public String toString() {
        switch (this){
            case COXSWAIN: //8+
                return "Coxswain";
            case PORT: //4+
                return "Port";
            case STARBOARD: //4x
                return "Starboard";
            case BOTH: //4x
                return "Both";
            case NONE: //4x
                return " ";
            default:
                throw new IllegalArgumentException();
        }

    }

}
