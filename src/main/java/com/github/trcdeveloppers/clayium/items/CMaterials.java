package com.github.trcdeveloppers.clayium.items;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.annotation.CShape;
import net.minecraft.item.Item;

public class CMaterials {
    /* Ingots */

    //region Ingots
    @CItem(oreDicts = "ingotAluminum", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item ALUMINUM_INGOT = new ClayiumItems.CMaterialTiered(6, 0xBEC8CA, 0x191919, 0xFFFFFF);

    @CItem(shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item AZ91D_INGOT = new ClayiumItems.CMaterialTiered(6, 0x828C87, 0x0A280A, 0xFFFFFF);

    @CItem(oreDicts = "ingotAntimony")
    private static final Item ANTIMONY_INGOT = new ClayiumItems.CMaterialTiered(8, 0x464646, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotImpureSilicon", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item IMPURE_SILICON_INGOT = new ClayiumItems.CMaterialTiered(5, 0x978F98, 0x533764, 0xA9A5A5);

    @CItem(oreDicts = "ingotSilicon", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item SILICON_INGOT = new ClayiumItems.CMaterialTiered(5, 0x281C28, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotSilicone", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item SILICONE_INGOT = new ClayiumItems.CMaterialTiered(5, 0xB4B4B4, 0xF0F0F0, 0xFFFFFF);

    @CItem(oreDicts = "ingotClaySteel", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item CLAY_STEEL_INGOT = new ClayiumItems.CMaterialTiered(7, 0x8890AD, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotClayium", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item CLAYIUM_INGOT = new ClayiumItems.CMaterialTiered(8, 0x5AF0D2, 0x3F4855, 0xFFCDC8);

    @CItem(oreDicts = "ingotUltimateAlloy", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item ULTIMATE_ALLOY_INGOT = new ClayiumItems.CMaterialTiered(9, 0x55CD55, 0x191919, 0xF5A0FF);

    @CItem(oreDicts = "ingotBeryllium", shapes = CShape.DUST)
    private static final Item BERYLLIUM_INGOT = new ClayiumItems.CMaterialTiered(9, 0xD2F0D2, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotCalcium", shapes = CShape.DUST)
    private static final Item CALCIUM_INGOT = new ClayiumItems.CMaterialTiered(7, 0xF0F0F0, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotLithium", shapes = CShape.DUST)
    private static final Item LITHIUM_INGOT = new ClayiumItems.CMaterialTiered(7, 0xD2D296, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotMagnesium", shapes = CShape.DUST)
    private static final Item MAGNESIUM_INGOT = new ClayiumItems.CMaterialTiered(6, 0x96D296, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotPotassium", shapes = CShape.DUST)
    private static final Item POTASSIUM_INGOT = new ClayiumItems.CMaterialTiered(7, 0xF0F0BE, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotSodium", shapes = CShape.DUST)
    private static final Item SODIUM_INGOT = new ClayiumItems.CMaterialTiered(6, 0xAAAADE, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotTitanium", shapes = CShape.DUST)
    private static final Item TITANIUM_INGOT = new ClayiumItems.CMaterialTiered(9, 0xD2F0F0, 0x191919, 0xFFFFFF);

    @CItem(shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item ZK60A_INGOT = new ClayiumItems.CMaterialTiered(6, 0x4B5550, 0x0A280A, 0xFFFFFF);

    @CItem
    private static final Item VANADIUM_INGOT = new ClayiumItems.CMaterialTiered(9, 0x3C7878, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotChrome", shapes = CShape.DUST)
    private static final Item CHROME_INGOT = new ClayiumItems.CMaterialTiered(9, 0xF0D2D2, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotManganese", shapes = CShape.DUST)
    private static final Item MANGANESE_INGOT = new ClayiumItems.CMaterialTiered(7, 0xBEF0F0, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotCobalt")
    private static final Item COBALT_INGOT = new ClayiumItems.CMaterialTiered(8, 0x1E1EE6, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotNickel", shapes = CShape.DUST)
    private static final Item NICKEL_INGOT = new ClayiumItems.CMaterialTiered(8, 0xD2D2F0, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotCopper")
    private static final Item COPPER_INGOT = new ClayiumItems.CMaterial(0xA05A0A, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotZinc")
    private static final Item ZINC_INGOT = new ClayiumItems.CMaterialTiered(6, 0xE6AAAA, 0x787878, 0xFFFFFF);

    @CItem
    private static final Item RUBIDIUM_INGOT = new ClayiumItems.CMaterialTiered(8, 0xF5F5F5, 0xEB0000, 0xFFFFFF);

    @CItem(shapes = CShape.DUST)
    private static final Item STRONTIUM_INGOT = new ClayiumItems.CMaterialTiered(7, 0xD2AAF2, 0x191919, 0xFFFFFF);

    @CItem(shapes = CShape.DUST)
    private static final Item ZIRCONIUM_INGOT = new ClayiumItems.CMaterialTiered(6, 0xBEAA7A, 0x787878, 0xFFFFFF);

    @CItem
    private static final Item MOLYBDENUM_INGOT = new ClayiumItems.CMaterialTiered(10, 0x82A082, 0x191919, 0xFFFFFF);
    //endregion
}
