package com.github.trcdeveloppers.clayium.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.Locale;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;

public enum CShape {
    INGOT,
    PLATE,
    LARGE_PLATE,
    DUST,
    MATTER,
    MATTER2,
    MATTER3,
    MATTER4,
    MATTER5;

    public static final CShape[] INGOT_BASED = {INGOT, PLATE, LARGE_PLATE, DUST};
    public static final CShape[] MATTER_BASED = {MATTER, PLATE, LARGE_PLATE, DUST};
    public static final CShape[] INGOT_DUST = {INGOT, DUST};

    public final ModelResourceLocation MODEL;

    CShape() {
        this.MODEL = new ModelResourceLocation(MOD_ID + ":colored/" + this.name().toLowerCase(Locale.ROOT), "inventory");
    }
}
