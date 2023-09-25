package com.github.trcdeveloppers.clayium.blocks.machines.clayworktable;

public enum ClayWorkTableMethod {
    ROLLING_HAND(1),
    PUNCH(2),
    ROLLING_PIN(3),
    CUT_PLATE(4),
    CUT_DISC(5),
    CUT(6),
    EMPTY(-1);

    public final int id;

    ClayWorkTableMethod(int id) {
        this.id = id;
    }

    public static ClayWorkTableMethod fromId(int id) {
        switch (id) {
            case 1:
                return ROLLING_HAND;
            case 2:
                return PUNCH;
            case 3:
                return ROLLING_PIN;
            case 4:
                return CUT_PLATE;
            case 5:
                return CUT_DISC;
            case 6:
                return CUT;
            default:
                return null;
        }
    }
}
