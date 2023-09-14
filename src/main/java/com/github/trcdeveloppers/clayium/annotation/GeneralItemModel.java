package com.github.trcdeveloppers.clayium.annotation;

public enum GeneralItemModel {
    INGOT,
    DUST,
    PLATE,
    LARGE_PLATE,
    MATTER,
    MATTER2,
    MATTER3,
    MATTER4,
    MATTER5;

    public static GeneralItemModel fromCShape(CShape shape) {
        switch (shape) {
            case PLATE:
                return GeneralItemModel.PLATE;
            case LARGE_PLATE:
                return GeneralItemModel.LARGE_PLATE;
            case DUST:
                return GeneralItemModel.DUST;
            default:
                return null;
        }
    }
}
