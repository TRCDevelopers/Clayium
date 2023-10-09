package com.github.trcdeveloppers.clayium.common.items;

import com.github.trcdeveloppers.clayium.common.annotation.CItem;
import com.github.trcdeveloppers.clayium.common.blocks.ClayiumBlocks;
import com.google.common.reflect.ClassPath;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.github.trcdeveloppers.clayium.Clayium.MOD_ID;
import static com.github.trcdeveloppers.clayium.common.creativetab.ClayiumCreativeTab.CLAYIUM;

public class ClayiumItems {

    @SideOnly(Side.CLIENT)
    private static Map<IItemColor, Item> itemColors;

    private static final Map<String, Item> items = new HashMap<>();

    public static Item getItem(String registryName) {
        return items.get(registryName);
    }

    public static Map<String, Item> getAllItems() {
        return Collections.unmodifiableMap(items);
    }

    public static void registerItems(RegistryEvent.Register<Item> event, Side side) {
        if (side.isClient()) {
            itemColors = new HashMap<>();
        }
        //参考 https://blog1.mammb.com/entry/2015/03/31/001620
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassPath.from(classLoader)
                .getTopLevelClassesRecursive("com.github.trcdeveloppers.clayium.common.items").stream()
                .map(ClassPath.ClassInfo::load)
                .forEach(clazz -> {
                    CItem cItem = clazz.getAnnotation(CItem.class);
                    if (cItem == null) {
                        return;
                    }
                    Item item;
                    try {
                        item = (Item) clazz.newInstance();
                        String registryName = cItem.registryName();
                        item.setCreativeTab(CLAYIUM)
                            .setTranslationKey(registryName)
                            .setRegistryName(new ResourceLocation(MOD_ID, registryName));
                        event.getRegistry().register(item);
                        items.put(registryName, item);
                        if (side.isClient()) {
                            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(MOD_ID, registryName), "inventory"));
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (ClayiumMaterials material : ClayiumMaterials.values()) {
            Item item = material.hasTier() ? itemWithTierTootip(material.tier) : new Item();
            String registryName = material.name().toLowerCase(Locale.ROOT);
            item.setCreativeTab(CLAYIUM)
                .setTranslationKey(registryName)
                .setRegistryName(new ResourceLocation(MOD_ID, registryName));
            event.getRegistry().register(item);
            if (!material.oreDict.isEmpty()) {
                OreDictionary.registerOre(material.oreDict, item);
            }
            items.put(registryName, item);
            if (side.isClient()) {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(MOD_ID, registryName), "inventory"));
            }
        }

        for (ClayiumColoredMaterials material : ClayiumColoredMaterials.values()) {
            for (CShape shape : material.shapes) {
                Item item = material.hasTier() ? itemWithTierTootip(material.tier) : new Item();
                String registryName = material.name().toLowerCase(Locale.ROOT);
                String capitalizedName = material.materialName.substring(0, 1).toUpperCase(Locale.ROOT) + material.materialName.substring(1);
                String oreDict = "";
                switch (shape) {
                    case LARGE_PLATE:
                        registryName = "large_" + material.materialName + "_plate";
                        oreDict = "largePlate" + capitalizedName;
                        break;
                    case INGOT:
                    case PLATE:
                    case DUST:
                        registryName = material.materialName + "_" + shape.name().toLowerCase(Locale.ROOT);
                        oreDict = shape.name().toLowerCase(Locale.ROOT) + capitalizedName;
                        break;
                }
                item.setCreativeTab(CLAYIUM)
                    .setTranslationKey(registryName)
                    .setRegistryName(new ResourceLocation(MOD_ID, registryName));
                event.getRegistry().register(item);
                if (!oreDict.isEmpty()) {
                    OreDictionary.registerOre(oreDict, item);
                }
                items.put(registryName, item);
                if (side.isClient()) {
                    itemColors.put(material::getColor, item);
                    ModelLoader.setCustomModelResourceLocation(item, 0, shape.MODEL);
                }
            }
        }
        ClayiumBlocks.getAllBlocks().forEach((registryName, block) -> {
            event.getRegistry().register(new ItemBlock(block).setRegistryName(registryName));
            if (side.isClient()) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(new ResourceLocation(MOD_ID, registryName), "inventory"));
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemColors() {
        itemColors.forEach(Minecraft.getMinecraft().getItemColors()::registerItemColorHandler);
    }

    private static Item itemWithTierTootip(int tier) {
        return new Item() {
            @Override
            @SideOnly(Side.CLIENT)
            @ParametersAreNonnullByDefault
            public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
                tooltip.set(0, ClayiumItems.getRarity(tier).getColor().toString() + tooltip.get(0));
                tooltip.add(ChatFormatting.WHITE + "Tier " + tier);
            }
        };
    }

    public static IRarity getRarity(int tier) {
        switch (tier) {
            case 4:
            case 5:
            case 6:
            case 7:
                return EnumRarity.UNCOMMON;
            case 8:
            case 9:
            case 10:
            case 11:
                return EnumRarity.RARE;
            case 12:
            case 13:
            case 14:
            case 15:
                return EnumRarity.EPIC;
            default:
                return EnumRarity.COMMON;
        }
    }

    private enum ClayiumMaterials {
        //region Circuits
        CEE_BOARD(3),
        CEE_CIRCUIT(3),
        CIRCUIT_TIER2(2),
        CIRCUIT_TIER3(3),
        CIRCUIT_TIER4(4),
        CIRCUIT_TIER5(5, "circuitBasic"),
        CIRCUIT_TIER6(6, "circuitAdvanced"),
        CIRCUIT_TIER7(7, "circuitElite"),
        CIRCUIT_TIER8(8, "circuitUltimate"),
        CIRCUIT_TIER9(9),
        CIRCUIT_TIER10(10),
        CIRCUIT_TIER11(11),
        CIRCUIT_TIER12(12),
        CIRCUIT_TIER13(13),
        //endregion
        //region (Dense) Clay Parts
        CLAY_BEARING(1),
        CLAY_BLADE(1),
        CLAY_CUTTING_HEAD(1),
        CLAY_CYLINDER(1),
        CLAY_DISC(1),
        CLAY_DUST(1),
        CLAY_GEAR(1),
        CLAY_GRINDING_HEAD(1),
        CLAY_NEEDLE(1),
        CLAY_PIPE(1),
        CLAY_RING(1),
        CLAY_SHORT_STICK(1),
        CLAY_SMALL_DISC(1),
        CLAY_SMALL_RING(1),
        CLAY_SPINDLE(1),
        CLAY_STICK(1),
        CLAY_WHEEL(1),
        LARGE_CLAY_BALL(2),
        COMPRESSED_CLAY_SHARD(1),
        DENSE_CLAY_BEARING(2),
        DENSE_CLAY_BLADE(2),
        DENSE_CLAY_CUTTING_HEAD(2),
        DENSE_CLAY_CYLINDER(2),
        DENSE_CLAY_DISC(2),
        DENSE_CLAY_DUST(2),
        DENSE_CLAY_GEAR(2),
        DENSE_CLAY_GRINDING_HEAD(2),
        DENSE_CLAY_NEEDLE(2),
        DENSE_CLAY_PIPE(2),
        DENSE_CLAY_RING(2),
        DENSE_CLAY_SHORT_STICK(2),
        DENSE_CLAY_SMALL_DISC(2),
        DENSE_CLAY_SMALL_RING(2),
        DENSE_CLAY_SPINDLE(2),
        DENSE_CLAY_STICK(2),
        DENSE_CLAY_WHEEL(2),
        //endregion
        ADV_INDUSTRIAL_CLAY_DUST(4),
        ADV_INDUSTRIAL_CLAY_SHARD(3),
        INDUSTRIAL_CLAY_DUST(3),
        INDUSTRIAL_CLAY_SHARD(2),

        SALT(4),
        CALCIUM_CHLORIDE_DUST(4),
        SODIUM_CARBONATE_DUST(4),
        QUARTZ_DUST(4),

        CLAY_CIRCUIT_BOARD(2),
        CLAY_ENERGY_EXCITER(3),
        EXCITED_CLAY_DUST(7),

        CLAY_GADGET_PARTS(6),

        MANIPULATOR_TIER1(6),
        MANIPULATOR_TIER2(8),
        MANIPULATOR_TIER3(12),

        LASER_PARTS(7),
        TELEPORTATION_PARTS(11),
        ;

        static final int NO_TIER = -1;

        final int tier;
        final String oreDict;

        ClayiumMaterials(int tier, String oreDict) {
            this.tier = tier;
            this.oreDict = oreDict;
        }

        ClayiumMaterials(int tier) {
            this.tier = tier;
            this.oreDict = "";
        }

        ClayiumMaterials(String oreDict) {
            this.tier = NO_TIER;
            this.oreDict = oreDict;
        }

        ClayiumMaterials() {
            this.tier = NO_TIER;
            this.oreDict = "";
        }

        boolean hasTier() {
            return this.tier != NO_TIER;
        }
    }

    private enum ClayiumColoredMaterials {
        //region Core Materials
        SILICONE(5, CShape.INGOT_BASED, 0xD2D2D2, 0xB4B4B4, 0xF0F0F0),
        SILICON(5, CShape.INGOT_BASED, 0x281C28, 0x191919, 0xFFFFFF),
        ALUMINUM(6, CShape.INGOT_BASED, 0xBEC8CA, 0x191919, 0xFFFFFF),
        CLAY_STEEL(7, CShape.INGOT_BASED, 0x8890AD, 0x191919, 0xFFFFFF),
        CLAYIUM(8, CShape.INGOT_BASED, 0x5AF0D2, 0x3F4855, 0xFFCDC8),
        ULTIMATE_ALLOY(9, CShape.INGOT_BASED, 0x55CD55, 0x191919, 0xF5A0FF),
        ANTIMATTER(10, CShape.MATTER_BASED, 0x0000EB, 0x000000, 0xFFFFFF),
        PURE_ANTIMATTER_TIER0("pure_antimatter", 11, CShape.MATTER_BASED, 0xFF32FF, 0x000000, 0xFFFFFF),
        OCTUPLE_ENERGETIC_CLAY("oec", 12, new CShape[]{CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST}, 0xFFFF00, 0x8C8C8C, 0xFFFFFF),
        PURE_ANTIMATTER_TIER8("opa", 13, new CShape[]{CShape.MATTER5, CShape.PLATE, CShape.LARGE_PLATE, CShape.DUST}, 0x960000, 0xC8C800, 0xFFFFFF),
        //endregion
        PURE_ANTIMATTER_TIER1(11, CShape.MATTER2, 0xC42385, 0x191919, 0xFFFFFF),
        PURE_ANTIMATTER_TIER2(11, CShape.MATTER2, 0x8E1777, 0x323200, 0xFFFFFF),
        PURE_ANTIMATTER_TIER3(11, CShape.MATTER3, 0x5E0D45, 0x4B4B00, 0xFFFFFF),
        PURE_ANTIMATTER_TIER4(12, CShape.MATTER3, 0x32061F, 0x646400, 0xFFFFFF),
        PURE_ANTIMATTER_TIER5(12, CShape.MATTER4, 0x520829, 0x7D7D00, 0xFFFFFF),
        PURE_ANTIMATTER_TIER6(12, CShape.MATTER4, 0x6E0727, 0x969600, 0xFFFFFF),
        PURE_ANTIMATTER_TIER7(12, CShape.MATTER4, 0x840519, 0xAFAF00, 0xFFFFFF),

        AZ91D(CShape.INGOT_BASED, 0x828C87, 0x0A280A, 0xFFFFFF),
        ZK60A(CShape.INGOT_BASED, 0x4B5550, 0x0A280A, 0xFFFFFF),
        //region Ingot & Dust
        BARIUM(CShape.INGOT_DUST, 0x965078, 0x781450, 0xFFFFFF),
        BERYLLIUM(CShape.INGOT_DUST, 0xD2F0D2, 0x191919, 0xFFFFFF),
        BRASS(CShape.INGOT_DUST, 0xBEAA14, 0x000000, 0xFFFFFF),
        BRONZE(CShape.INGOT_DUST, 0xFA9628, 0x000000, 0xFFFFFF),
        CALCIUM(CShape.INGOT_DUST, 0xF0F0F0, 0x191919, 0xFFFFFF),
        CHROME(CShape.INGOT_DUST, 0xF0D2D2, 0x191919, 0xFFFFFF),
        COPPER(CShape.INGOT_DUST, 0xA05A0A, 0x191919, 0xFFFFFF),
        ELECTRUM(CShape.INGOT_DUST, 0xE6E69B, 0x787846, 0xFFFFFF),
        HAFNIUM(CShape.INGOT_DUST, 0xF0D2AA, 0x191919, 0xFFFFFF),
        INVAR(CShape.INGOT_DUST, 0xAAAA50, 0x8C8C46, 0xB4B450),
        LEAD(CShape.INGOT_DUST, 0xBEF0D2, 0x191919, 0xFFFFFF),
        LITHIUM(CShape.INGOT_DUST, 0xD2D296, 0x787878, 0xFFFFFF),
        MANGANESE(CShape.INGOT_DUST, 0xBEF0F0, 0x191919, 0xFFFFFF),
        MAGNESIUM(CShape.INGOT_DUST, 0x96D296, 0x787878, 0xFFFFFF),
        NICKEL(CShape.INGOT_DUST, 0xD2D2F0, 0x191919, 0xFFFFFF),
        POTASSIUM(CShape.INGOT_DUST, 0xF0F0BE, 0x191919, 0xFFFFFF),
        SODIUM(CShape.INGOT_DUST, 0xAAAADE, 0x787878, 0xFFFFFF),
        STEEL(CShape.INGOT_DUST, 0x5A5A6E, 0x000000, 0xFFFFFF),
        STRONTIUM(CShape.INGOT_DUST, 0xD2AAF2, 0x191919, 0xFFFFFF),
        TITANIUM(CShape.INGOT_DUST, 0xD2F0F0, 0x191919, 0xFFFFFF),
        ZINC(CShape.INGOT_DUST, 0xE6AAAA, 0x787878, 0xFFFFFF),
        ZIRCONIUM(CShape.INGOT_DUST, 0xBEAA7A, 0x787878, 0xFFFFFF),
        //endregion
        //region Only Ingot
        ACTINIUM(CShape.INGOT, 0xF5F5F5, 0x0000EB, 0xFFFFFF),
        AMERICIUM(CShape.INGOT, 0xEBEBEB, 0x9B9B9B, 0xEBEBEB),
        ANTIMONY(CShape.INGOT, 0x464646, 0x191919, 0xFFFFFF),
        BISMUTH(CShape.INGOT, 0x467846, 0x191919, 0xFFFFFF),
        CAESIUM(CShape.INGOT, 0xF5F5F5, 0x969600, 0xFFFFFF),
        CERIUM(CShape.INGOT, 0x919191, 0x969600, 0xFFFFFF),
        COBALT(CShape.INGOT, 0x1E1EE6, 0x191919, 0xFFFFFF),
        CURIUM(CShape.INGOT, 0xFFFFFF, 0x9B9B9B, 0xF4F4F4),
        EUROPIUM(CShape.INGOT, 0x919191, 0x373737, 0x919191),
        FRANCIUM(CShape.INGOT, 0xF5F5F5, 0x00EB00, 0xFFFFFF),
        IRIDIUM(CShape.INGOT, 0xF0F0F0, 0xD2D2D2, 0xEBEBEB),
        LANTHANUM(CShape.INGOT, 0x919191, 0xEB0000, 0xFFFFFF),
        MOLYBDENUM(CShape.INGOT, 0x82A082, 0x191919, 0xFFFFFF),
        NEODYMIUM(CShape.INGOT, 0x919191, 0x009696, 0xFFFFFF),
        NEPTUNIUM(CShape.INGOT, 0x3232FF, 0x32329B, 0x3232FF),
        OSMIUM(CShape.INGOT, 0x464696, 0x191919, 0xFFFFFF),
        PALLADIUM(CShape.INGOT, 0x974646, 0x191919, 0xFFFFFF),
        PLATINUM(CShape.INGOT, 0xF5F5E6, 0x8C8C78, 0xFFFFFF),
        PLUTONIUM(CShape.INGOT, 0xFF3232, 0x9B3232, 0xFF3232),
        PRASEODYMIUM(CShape.INGOT, 0x919191, 0x00EB00, 0xFFFFFF),
        PROMETHIUM(CShape.INGOT, 0x919191, 0x0000EB, 0xFFFFFF),
        PROTACTINIUM(CShape.INGOT, 0x323232, 0x191919, 0x323264),
        RADIUM(CShape.INGOT, 0xF5F5F5, 0x009696, 0xFFFFFF),
        RHENIUM(CShape.INGOT, 0x464696, 0x191919, 0x32325A),
        RUBIDIUM(CShape.INGOT, 0xF5F5F5, 0xEB0000, 0xFFFFFF),
        SAMARIUM(CShape.INGOT, 0x919191, 0x960096, 0xFFFFFF),
        SILVER(CShape.INGOT, 0xE6E6F5, 0x78788C, 0xFFFFFF),
        TANTALUM(CShape.INGOT, 0xF0D2AA, 0x191919, 0xF0D296),
        THORIUM(CShape.INGOT, 0x323232, 0x191919, 0xC83232),
        TIN(CShape.INGOT, 0xE6E6F0, 0x000000, 0xFFFFFF),
        TUNGSTEN(CShape.INGOT, 0x1E1E1E, 0x191919, 0xFFFFFF),
        URANIUM(CShape.INGOT, 0x32FF32, 0x329B32, 0x32FF32),
        VANADIUM(CShape.INGOT, 0x3C7878, 0x191919, 0xFFFFFF),
        ZINC_ALUMINUM(CShape.INGOT, 0xF0BEDC, 0xA00000, 0xFFFFFF),
        ZINC_ZIRCONIUM(CShape.INGOT, 0xE6AA8C, 0x780000, 0xFFFFFF),
        //endregion
        //region Impure Dusts
        IMPURE_ALUMINUM(6, CShape.DUST, 0xBEC8CA, 0x78783C, 0xDCDCDC),
        IMPURE_BARIUM(CShape.DUST, 0x965078, 0x78783C, 0xDCDCDC),
        IMPURE_BERYLLIUM(CShape.DUST, 0xD2F0D2, 0x78783C, 0xDCDCDC),
        IMPURE_CALCIUM(CShape.DUST, 0xF0F0F0, 0x78783C, 0xDCDCDC),
        IMPURE_COPPER(CShape.DUST, 0xA05A0A, 0x78783C, 0xDCDCDC),
        IMPURE_HAFNIUM(CShape.DUST, 0xF0D2AA, 0x78783C, 0xDCDCDC),
        IMPURE_IRON(CShape.DUST, 0xD8D8D8, 0x78783C, 0xDCDCDC),
        IMPURE_LEAD(CShape.DUST, 0xBEF0D2, 0x78783C, 0xDCDCDC),
        IMPURE_LITHIUM(CShape.DUST, 0xDCDC96, 0x78783C, 0xDCDCDC),
        IMPURE_MAGNESIUM(CShape.DUST, 0x96DC96, 0x78783C, 0xDCDCDC),
        IMPURE_MANGANESE(CShape.DUST, 0xBEF0F0, 0x78783C, 0xDCDCDC),
        IMPURE_NICKEL(CShape.DUST, 0xD2D2F0, 0x78783C, 0xDCDCDC),
        IMPURE_POTASSIUM(CShape.DUST, 0xF0F0BE, 0x78783C, 0xDCDCDC),
        IMPURE_SILICON(5, CShape.DUST, 0x978F98, 0x533764, 0xA9A5A5),
        IMPURE_SODIUM(CShape.DUST, 0xAAAAE6, 0x78783C, 0xDCDCDC),
        IMPURE_STRONTIUM(CShape.DUST, 0xD2AAF2, 0x78783C, 0xDCDCDC),
        IMPURE_TITANIUM(CShape.DUST, 0xD2F0F0, 0x78783C, 0xDCDCDC),
        IMPURE_ZINC(CShape.DUST, 0xE6AAAA, 0x78783C, 0xDCDCDC),
        IMPURE_ZIRCONIUM(CShape.DUST, 0xBEAA7A, 0x78783C, 0xDCDCDC),

        IMPURE_REDSTONE(CShape.DUST, 0x974646, 0x191919, 0xFFFFFF),
        IMPURE_GLOWSTONE(CShape.DUST, 0x979746, 0x191919, 0xFFFFFF),
        //endregion
        CARBON(CShape.DUST, 0x0A0A0A, 0x191919, 0x1E1E1E),
        COAL(CShape.DUST, 0x141414, 0x191919, 0x323250),
        CHARCOAL(CShape.DUST, 0x141414, 0x191919, 0x503232),
        GOLD(CShape.DUST, 0xFFFF0A, 0x3C3C00, 0xFFFFFF),
        IRON(CShape.DUST, 0xD8D8D8, 0x353535, 0xFFFFFF),
        LAPIS(CShape.DUST, 0x3C64BE, 0x0A2B7A, 0x5A82E2),
        ORGANIC_CLAY(CShape.DUST, 0x8890AD, 0x6A2C2B, 0x92A4B7),
        SALTPETER(CShape.DUST, 0xDEDCDC, 0xBEC8D2, 0xFFF0E6)
        //PHOSPHORUS(CShape.DUST, );
        //SULFUR(CShape.DUST, );
        ;
        static final int NO_TIER = -1;

        final int tier;
        final CShape[] shapes;
        final int[] colors;
        final String materialName;

        ClayiumColoredMaterials(String materialName, int tier, CShape[] shapes, int... colors) {
            this.tier = tier;
            this.colors = colors;
            this.shapes = shapes;
            this.materialName = materialName;
        }

        ClayiumColoredMaterials(int tier, CShape[] shapes, int... colors) {
            this.tier = tier;
            this.colors = colors;
            this.shapes = shapes;
            this.materialName = this.name().toLowerCase(Locale.ROOT);
        }

        ClayiumColoredMaterials(int tier, CShape shape, int... colors) {
            this.tier = tier;
            this.colors = colors;
            this.shapes = new CShape[]{shape};
            this.materialName = this.name().toLowerCase(Locale.ROOT);
        }

        ClayiumColoredMaterials(CShape[] shapes, int... colors) {
            this.tier = NO_TIER;
            this.colors = colors;
            this.shapes = shapes;
            this.materialName = this.name().toLowerCase(Locale.ROOT);
        }

        ClayiumColoredMaterials(CShape shape, int... colors) {
            this.tier = NO_TIER;
            this.shapes = new CShape[]{shape};
            this.colors = colors;
            this.materialName = this.name().toLowerCase(Locale.ROOT);
        }

        boolean hasTier() {
            return this.tier != NO_TIER;
        }

        int getColor(ItemStack stack, int tinIndex) {
            return this.colors.length > tinIndex
                ?
                this.colors[tinIndex]
                :
                    0;
        }
    }

    public interface ClayiumItem {
        default boolean hasMetadata() {
            return false;
        }

        /* keyに設定されたメタデータに対してモデルをvalueに定義します。
         * hasMetadataがfalseの場合使用せず、メタデータ0に対してregistryNameの使用をします。
         */
        default Map<Integer, String> getMetadataModels() {
            return null;
        }

        default List<String> getOreDictionaries() {
            return Collections.emptyList();
        }
    }
}
