package com.github.trcdeveloppers.clayium.items;

import com.github.trcdeveloppers.clayium.annotation.CItem;
import com.github.trcdeveloppers.clayium.annotation.CShape;
import com.github.trcdeveloppers.clayium.annotation.GeneralItemModel;
import com.github.trcdeveloppers.clayium.interfaces.ITiered;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.github.trcdeveloppers.clayium.creativetab.ClayiumCreativeTab.CLAYIUM;

@SuppressWarnings("unused")
public class CMaterials {
    //region Ingots
    @CItem(oreDicts = "ingotActinium")
    private static final Item ACTINIUM_INGOT = new CMaterial(0xF5F5F5, 0x0000EB, 0xFFFFFF);

    @CItem(oreDicts = {"ingotAluminum", "ingotAluminium"}, shapes = {CShape.DUST, CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item ALUMINUM_INGOT = new CMaterialTiered(6, 0xBEC8CA, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotAmericium")
    private static final Item AMERICIUM_INGOT = new CMaterial(0xEBEBEB, 0x9B9B9B, 0xEBEBEB);

    @CItem(oreDicts = "ingotAntimony")
    private static final Item ANTIMONY_INGOT = new CMaterial(0x464646, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotAz91d", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item AZ91D_INGOT = new CMaterial(0x828C87, 0x0A280A, 0xFFFFFF);

    @CItem(oreDicts = "ingotBarium")
    private static final Item BARIUM_INGOT = new CMaterial(0x965078, 0x781450, 0xFFFFFF);

    @CItem(oreDicts = "ingotBeryllium")
    private static final Item BERYLLIUM_INGOT = new CMaterial(0xD2F0D2, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotBismuth")
    private static final Item BISMUTH_INGOT = new CMaterial(0x467846, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotBrass")
    private static final Item BRASS_INGOT = new CMaterial(0xBEAA14, 0x000000, 0xFFFFFF);

    @CItem(oreDicts = "ingotBronze")
    private static final Item BRONZE_INGOT = new CMaterial(0xFA9628, 0x000000, 0xFFFFFF);

    @CItem(oreDicts = "ingotCaesium")
    private static final Item CAESIUM_INGOT = new CMaterial(0xF5F5F5, 0x969600, 0xFFFFFF);

    @CItem(oreDicts = "ingotCalcium")
    private static final Item CALCIUM_INGOT = new CMaterial(0xF0F0F0, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotCerium")
    private static final Item CERIUM_INGOT = new CMaterial(0x919191, 0x969600, 0xFFFFFF);

    @CItem(oreDicts = "ingotChrome")
    private static final Item CHROME_INGOT = new CMaterial(0xF0D2D2, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotCobalt")
    private static final Item COBALT_INGOT = new CMaterial(0x1E1EE6, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotCopper")
    private static final Item COPPER_INGOT = new CMaterial(0xA05A0A, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotCurium")
    private static final Item CURIUM_INGOT = new CMaterial(0xFFFFFF, 0x9B9B9B, 0xF4F4F4);

    @CItem(oreDicts = "ingotElectrum")
    private static final Item ELECTRUM_INGOT = new CMaterial(0xE6E69B, 0x787846, 0xFFFFFF);

    @CItem(oreDicts = "ingotEuropium")
    private static final Item EUROPIUM_INGOT = new CMaterial(0x919191, 0x373737, 0x919191);

    @CItem(oreDicts = "ingotFrancium")
    private static final Item FRANCIUM_INGOT = new CMaterial(0xF5F5F5, 0x00EB00, 0xFFFFFF);

    @CItem(oreDicts = "ingotGold")
    private static final Item GOLD_INGOT = new CMaterial(0xFFFF0A, 0x3C3C00, 0xFFFFFF);

    @CItem(oreDicts = "ingotHafnium")
    private static final Item HAFNIUM_INGOT = new CMaterial(0xF0D2AA, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotInvar")
    private static final Item INVAR_INGOT = new CMaterial(0xAAAA50, 0x8C8C46, 0xB4B450);

    @CItem(oreDicts = "ingotIridium")
    private static final Item IRIDIUM_INGOT = new CMaterial(0xF0F0F0, 0xD2D2D2, 0xEBEBEB);

    @CItem(oreDicts = "ingotIron")
    private static final Item IRON_INGOT = new CMaterial(0xD8D8D8, 0x353535, 0xFFFFFF);

    @CItem(oreDicts = "ingotLanthanum")
    private static final Item LANTHANUM_INGOT = new CMaterial(0x919191, 0xEB0000, 0xFFFFFF);

    @CItem(oreDicts = "ingotLead")
    private static final Item LEAD_INGOT = new CMaterial(0xBEF0D2, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotLithium")
    private static final Item LITHIUM_INGOT = new CMaterial(0xD2D296, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotMagnesium")
    private static final Item MAGNESIUM_INGOT = new CMaterial(0x96D296, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotManganese")
    private static final Item MANGANESE_INGOT = new CMaterial(0xBEF0F0, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotMolybdenum")
    private static final Item MOLYBDENUM_INGOT = new CMaterial(0x82A082, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotNeodymium")
    private static final Item NEODYMIUM_INGOT = new CMaterial(0x919191, 0x009696, 0xFFFFFF);

    @CItem(oreDicts = "ingotNeptunium")
    private static final Item NEPTUNIUM_INGOT = new CMaterial(0x3232FF, 0x32329B, 0x3232FF);

    @CItem(oreDicts = "ingotNickel")
    private static final Item NICKEL_INGOT = new CMaterial(0xD2D2F0, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotOsmium")
    private static final Item OSMIUM_INGOT = new CMaterial(0x464696, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotPalladium")
    private static final Item PALLADIUM_INGOT = new CMaterial(0x974646, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotPlatinum")
    private static final Item PLATINUM_INGOT = new CMaterial(0xF5F5E6, 0x8C8C78, 0xFFFFFF);

    @CItem(oreDicts = "ingotPlutonium")
    private static final Item PLUTONIUM_INGOT = new CMaterial(0xFF3232, 0x9B3232, 0xFF3232);

    @CItem(oreDicts = "ingotPotassium")
    private static final Item POTASSIUM_INGOT = new CMaterial(0xF0F0BE, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotPraseodymium")
    private static final Item PRASEODYMIUM_INGOT = new CMaterial(0x919191, 0x00EB00, 0xFFFFFF);

    @CItem(oreDicts = "ingotPromethium")
    private static final Item PROMETHIUM_INGOT = new CMaterial(0x919191, 0x0000EB, 0xFFFFFF);

    @CItem(oreDicts = "ingotProtactinium")
    private static final Item PROTACTINIUM_INGOT = new CMaterial(0x323232, 0x191919, 0x323264);

    @CItem(oreDicts = "ingotRadium")
    private static final Item RADIUM_INGOT = new CMaterial(0xF5F5F5, 0x009696, 0xFFFFFF);

    @CItem(oreDicts = "ingotRhenium")
    private static final Item RHENIUM_INGOT = new CMaterial(0x464696, 0x191919, 0x32325A);

    @CItem(oreDicts = "ingotRubidium")
    private static final Item RUBIDIUM_INGOT = new CMaterial(0xF5F5F5, 0xEB0000, 0xFFFFFF);

    @CItem(oreDicts = "ingotSamarium")
    private static final Item SAMARIUM_INGOT = new CMaterial(0x919191, 0x960096, 0xFFFFFF);

    @CItem(oreDicts = "ingotSilicon", shapes = {CShape.DUST, CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item SILICON_INGOT = new CMaterialTiered(5, 0x281C28, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotSilicone", shapes = {CShape.DUST, CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item SILICONE_INGOT = new CMaterialTiered(5, 0xD2D2D2, 0xB4B4B4, 0xF0F0F0);

    @CItem(oreDicts = "ingotSilver")
    private static final Item SILVER_INGOT = new CMaterial(0xE6E6F5, 0x78788C, 0xFFFFFF);

    @CItem(oreDicts = "ingotSodium")
    private static final Item SODIUM_INGOT = new CMaterial(0xAAAADE, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotSteel")
    private static final Item STEEL_INGOT = new CMaterial(0x5A5A6E, 0x000000, 0xFFFFFF);

    @CItem(oreDicts = "ingotStrontium")
    private static final Item STRONTIUM_INGOT = new CMaterial(0xD2AAF2, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotTantalum")
    private static final Item TANTALUM_INGOT = new CMaterial(0xF0D2AA, 0x191919, 0xF0D296);

    @CItem(oreDicts = "ingotThorium")
    private static final Item THORIUM_INGOT = new CMaterial(0x323232, 0x191919, 0xC83232);

    @CItem(oreDicts = "ingotTin")
    private static final Item TIN_INGOT = new CMaterial(0xE6E6F0, 0x000000, 0xFFFFFF);

    @CItem(oreDicts = "ingotTitanium")
    private static final Item TITANIUM_INGOT = new CMaterial(0xD2F0F0, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotTungsten")
    private static final Item TUNGSTEN_INGOT = new CMaterial(0x1E1E1E, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotUranium")
    private static final Item URANIUM_INGOT = new CMaterial(0x32FF32, 0x329B32, 0x32FF32);

    @CItem(oreDicts = "ingotVanadium")
    private static final Item VANADIUM_INGOT = new CMaterial(0x3C7878, 0x191919, 0xFFFFFF);

    @CItem(oreDicts = "ingotZinc")
    private static final Item ZINC_INGOT = new CMaterial(0xE6AAAA, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotZincAluminum", shapes = CShape.DUST)
    private static final Item ZINC_ALUMINUM_INGOT = new CMaterial(0xF0BEDC, 0xA00000, 0xFFFFFF);

    @CItem(oreDicts = "ingotZincZirconium", shapes = CShape.DUST)
    private static final Item ZINC_ZIRCONIUM_INGOT = new CMaterial(0xE6AA8C, 0x780000, 0xFFFFFF);

    @CItem(oreDicts = "ingotZirconium", shapes = CShape.DUST)
    private static final Item ZIRCONIUM_INGOT = new CMaterial(0xBEAA7A, 0x787878, 0xFFFFFF);

    @CItem(oreDicts = "ingotZk60a", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item ZK60A_INGOT = new CMaterial(0x4B5550, 0x0A280A, 0xFFFFFF);
    //endregion

    //region Other Materials
    @CItem(shapes = {CShape.DUST, CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item ANTIMATTER = new CMaterialTiered(GeneralItemModel.MATTER, 10, 0x0000EB, 0x000000, 0xFFFFFF);

    @CItem(materialName = "pure_antimatter", shapes = {CShape.DUST, CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item PURE_ANTIMATTER_TIER0 = new CMaterialTiered(GeneralItemModel.MATTER, 11, 0xFF32FF, 0x000000, 0xFFFFFF);
    @CItem private static final Item PURE_ANTIMATTER_TIER1 = new CMaterialTiered(GeneralItemModel.MATTER2, 11, 0xC42385, 0x191919, 0xFFFFFF);
    @CItem private static final Item PURE_ANTIMATTER_TIER2 = new CMaterialTiered(GeneralItemModel.MATTER2, 11, 0x8E1777, 0x323200, 0xFFFFFF);
    @CItem private static final Item PURE_ANTIMATTER_TIER3 = new CMaterialTiered(GeneralItemModel.MATTER3, 11, 0x5E0D45, 0x4B4B00, 0xFFFFFF);
    @CItem private static final Item PURE_ANTIMATTER_TIER4 = new CMaterialTiered(GeneralItemModel.MATTER3, 12, 0x32061F, 0x646400, 0xFFFFFF);
    @CItem private static final Item PURE_ANTIMATTER_TIER5 = new CMaterialTiered(GeneralItemModel.MATTER4, 12, 0x520829, 0x7D7D00, 0xFFFFFF);
    @CItem private static final Item PURE_ANTIMATTER_TIER6 = new CMaterialTiered(GeneralItemModel.MATTER4, 12, 0x6E0727, 0x969600, 0xFFFFFF);
    @CItem private static final Item PURE_ANTIMATTER_TIER7 = new CMaterialTiered(GeneralItemModel.MATTER4, 12, 0x840519, 0xAFAF00, 0xFFFFFF);
    @CItem(materialName = "opa", shapes = {CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST})
    private static final Item PURE_ANTIMATTER_TIER8 = new CMaterialTiered(GeneralItemModel.MATTER5, 13, 0x960000, 0xC8C800, 0xFFFFFF);
    //endregion

    //region Dusts
    @CItem private static final Item CARBON_DUST = new CMaterial(GeneralItemModel.DUST, 0x0A0A0A, 0x191919, 0x1E1E1E);
    @CItem private static final Item CHARCOAL_DUST = new CMaterial(GeneralItemModel.DUST, 0x141414, 0x191919, 0x503232);
    @CItem private static final Item COAL_DUST = new CMaterial(GeneralItemModel.DUST, 0x141414, 0x191919, 0x323250);
    @CItem private static final Item IMPURE_ALUMINUM_DUST = new CMaterialTiered(GeneralItemModel.DUST, 6, 0xBEC8CA, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_BARIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0x965078, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_BERYLLIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xD2F0D2, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_CALCIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xF0F0F0, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_COPPER_DUST = new CMaterial(GeneralItemModel.DUST, 0xA05A0A, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_GLOWSTONE_DUST = new CMaterial(GeneralItemModel.DUST, 0x979746, 0x191919, 0xFFFFFF);
    @CItem private static final Item IMPURE_HAFNIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xF0D2AA, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_IRON_DUST = new CMaterial(GeneralItemModel.DUST, 0xD8D8D8, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_LEAD_DUST = new CMaterial(GeneralItemModel.DUST, 0xBEF0D2, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_LITHIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xDCDC96, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_MAGNESIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0x96DC96, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_MANGANESE_DUST = new CMaterial(GeneralItemModel.DUST, 0xBEF0F0, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_NICKEL_DUST = new CMaterial(GeneralItemModel.DUST, 0xD2D2F0, 0x78783C, 0xDCDCDC);
    // hardcore osmium is not implemented yet
    // @CItem private static final Item IMPURE_OSMIUM_DUST = new ClayiumItems.CMaterial(0x464696, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_POTASSIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xF0F0BE, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_REDSTONE_DUST = new CMaterial(GeneralItemModel.DUST, 0x974646, 0x191919, 0xFFFFFF);
    @CItem private static final Item IMPURE_SILICON_DUST = new CMaterialTiered(GeneralItemModel.DUST, 5, 0x978F98, 0x533764, 0xA9A5A5);
    @CItem private static final Item IMPURE_SODIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xAAAAE6, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_STRONTIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xD2AAF2, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_TITANIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xD2F0F0, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_ZINC_DUST = new CMaterial(GeneralItemModel.DUST, 0xE6AAAA, 0x78783C, 0xDCDCDC);
    @CItem private static final Item IMPURE_ZIRCONIUM_DUST = new CMaterial(GeneralItemModel.DUST, 0xBEAA7A, 0x78783C, 0xDCDCDC);
    @CItem private static final Item LAPIS_DUST = new CMaterial(GeneralItemModel.DUST, 0x3C64BE, 0x0A2B7A, 0x5A82E2);
    @CItem private static final Item ORGANIC_CLAY_DUST = new CMaterial(GeneralItemModel.DUST, 0x8890AD, 0x6A2C2B, 0x92A4B7);

    @CItem(shapes = {CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item CLAY_DUST = new CItemTiered(1);

    @CItem(shapes = {CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item DENSE_CLAY_DUST = new CItemTiered(2);

    @CItem(shapes = {CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item INDUSTRIAL_CLAY_DUST = new CItemTiered(3);

    @CItem(shapes = {CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item ADV_INDUSTRIAL_CLAY_DUST = new CItemTiered(4);

    @CItem(registryName = "oec_dust", shapes = {CShape.PLATE, CShape.LARGE_PLATE})
    private static final Item OCTUPLE_ENERGETIC_CLAY_DUST = new CMaterialTiered(GeneralItemModel.DUST, 12, 0xFFFF00, 0x8C8C8C, 0xFFFFFF);
    //endregion

    //region Circuits
    @CItem private static final Item CEE_BOARD = new CItemTiered(3);
    @CItem private static final Item CEE_CIRCUIT = new CItemTiered(3);
    @CItem private static final Item CIRCUIT_TIER2 = new CItemTiered(2);
    @CItem private static final Item CIRCUIT_TIER3 = new CItemTiered(3);
    @CItem private static final Item CIRCUIT_TIER4 = new CItemTiered(4);
    @CItem private static final Item CIRCUIT_TIER5 = new CItemTiered(5);
    @CItem private static final Item CIRCUIT_TIER6 = new CItemTiered(6);
    @CItem private static final Item CIRCUIT_TIER7 = new CItemTiered(7);
    @CItem private static final Item CIRCUIT_TIER8 = new CItemTiered(8) {
        @Override
        @ParametersAreNonnullByDefault
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
            tooltip.add(I18n.format("item.circuit_tier8.tooltip"));
        }
    };
    @CItem private static final Item CIRCUIT_TIER9 = new CItemTiered(9);
    @CItem private static final Item CIRCUIT_TIER10 = new CItemTiered(10);
    @CItem private static final Item CIRCUIT_TIER11 = new CItemTiered(11);
    @CItem private static final Item CIRCUIT_TIER12 = new CItemTiered(12);
    @CItem private static final Item CIRCUIT_TIER13 = new CItemTiered(13);
    @CItem private static final Item CLAY_CIRCUIT_BOARD = new CItemTiered(2);
    //endregion

    //region (DENSE) Clay Parts
    @CItem private static final Item CLAY_BEARING = new CItemTiered(1);
    @CItem private static final Item CLAY_BLADE = new CItemTiered(1);
    @CItem private static final Item CLAY_CUTTING_HEAD = new CItemTiered(1);
    @CItem private static final Item CLAY_CYLINDER = new CItemTiered(1);
    @CItem private static final Item CLAY_DISC = new CItemTiered(1);
    @CItem private static final Item CLAY_GEAR = new CItemTiered(1);
    @CItem private static final Item CLAY_GRINDING_HEAD = new CItemTiered(1);
    @CItem private static final Item CLAY_NEEDLE = new CItemTiered(1);
    @CItem private static final Item CLAY_PIPE = new CItemTiered(1);
    @CItem private static final Item CLAY_RING = new CItemTiered(1);
    @CItem private static final Item CLAY_SHORT_STICK = new CItemTiered(1);
    @CItem private static final Item CLAY_SMALL_DISC = new CItemTiered(1);
    @CItem private static final Item CLAY_SMALL_RING = new CItemTiered(1);
    @CItem private static final Item CLAY_SPINDLE = new CItemTiered(1);
    @CItem private static final Item CLAY_STICK = new CItemTiered(1);
    @CItem private static final Item CLAY_WHEEL = new CItemTiered(1);

    @CItem private static final Item DENSE_CLAY_BEARING = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_BLADE = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_CUTTING_HEAD = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_CYLINDER = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_DISC = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_GEAR = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_GRINDING_HEAD = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_NEEDLE = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_PIPE = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_RING = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_SHORT_STICK = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_SMALL_DISC = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_SMALL_RING = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_SPINDLE = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_STICK = new CItemTiered(2);
    @CItem private static final Item DENSE_CLAY_WHEEL = new CItemTiered(2);
    //endregion

    //region Other Parts
    @CItem private static final Item MANIPULATOR_TIER1 = new CItemTiered(6);
    @CItem private static final Item MANIPULATOR_TIER2 = new CItemTiered(8);
    @CItem private static final Item MANIPULATOR_TIER3 = new CItemTiered(12);

    @CItem private static final Item COMPRESSED_CLAY_SHARD = new CItemTiered(1);
    @CItem private static final Item INDUSTRIAL_CLAY_SHARD = new CItemTiered(2);
    @CItem private static final Item ADV_INDUSTRIAL_CLAY_SHARD = new CItemTiered(3);

    @CItem private static final Item CLAY_ENERGY_EXCITER = new CItemTiered(3);
    @CItem private static final Item CLAY_GADGET_PARTS = new CItemTiered(6);
    @CItem private static final Item LARGE_CLAY_BALL = new CItemTiered(2);
    @CItem private static final Item LASER_PARTS = new CItemTiered(7);
    @CItem private static final Item TELEPORTATION_PARTS = new CItemTiered(11);
    //endregion

    public static class CItemTiered extends Item implements ITiered {
        private final int tier;

        public CItemTiered(int tier) {
            super.setCreativeTab(CLAYIUM);
            this.tier = tier;
        }

        @Override
        public int getTier() {
            return this.tier;
        }
    }

    public static class CMaterial extends Item implements IItemColor {
        private final int[] colors;
        public final GeneralItemModel MODEL;

        public CMaterial(GeneralItemModel model, int... colors) {
            super();
            super.setCreativeTab(CLAYIUM);
            this.MODEL = model;
            this.colors = colors;
        }

        /**
         * Create new instance with GeneralItemModel.Ingot
         */
        public CMaterial(int... colors) {
            this(GeneralItemModel.INGOT, colors);
        }

        @Override
        @ParametersAreNonnullByDefault
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            return tintIndex >= 0 && tintIndex < this.colors.length ? this.colors[tintIndex] : 0;
        }

        public int[] getColors() {
            return this.colors;
        }
    }

    public static class CMaterialTiered extends CMaterial implements ITiered, IItemColor {
        private final int tier;

        public CMaterialTiered(GeneralItemModel model, int tier, int... colors) {
            super(model, colors);
            this.tier = tier;
        }
        public CMaterialTiered(int tier, int... colors) {
            super(colors);
            this.tier = tier;
        }

        @Override
        public int getTier() {
            return this.tier;
        }
    }
}
