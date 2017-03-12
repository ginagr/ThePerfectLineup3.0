package ggr.tpl7.model;

public enum BoatSize {
    EIGHT, FOUR, QUAD, PAIR, DOUBLE, SINGLE;

    @Override
    public String toString() {
        switch (this){
            case EIGHT: //8+
                return "8+";
            case FOUR: //4+
                return "4+";
            case QUAD: //4x
                return "4x";
            case PAIR: //2+
                return "2+";
            case DOUBLE: //2x
                return "2x";
            case SINGLE: //1x
                return "1x";
            default:
                return " ";
        }
    }

    public int toInt() {
        switch (this){
            case EIGHT: //8+
                return 8;
            case FOUR: //4+
                return 4;
            case QUAD: //4x
                return 4;
            case PAIR: //2+
                return 2;
            case DOUBLE: //2x
                return 2;
            case SINGLE: //1x
                return 1;
            default:
                return 0;
        }
    }
}

