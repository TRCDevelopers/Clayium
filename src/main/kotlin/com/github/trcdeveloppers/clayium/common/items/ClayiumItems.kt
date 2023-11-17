package com.github.trcdeveloppers.clayium.common.items

import com.github.trcdeveloppers.clayium.Clayium
import com.github.trcdeveloppers.clayium.Clayium.Companion.MOD_ID
import com.github.trcdeveloppers.clayium.common.annotation.CItem
import com.github.trcdeveloppers.clayium.common.blocks.ClayiumBlocks.allBlocks
import com.github.trcdeveloppers.clayium.common.items.CShape.*
import com.google.common.reflect.ClassPath
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.IRarity
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.OreDictionary
import java.util.Collections
import javax.annotation.ParametersAreNonnullByDefault

object ClayiumItems {

    @SideOnly(Side.CLIENT)
    private lateinit var itemColors: MutableMap<Item, IItemColor>
    private val items: MutableMap<String, Item> = HashMap()
    @JvmStatic
    fun getItem(registryName: String): Item? {
        return items[registryName]
    }

    val allItems: Map<String, Item>
        get() = Collections.unmodifiableMap(items)

    fun registerItems(event: RegistryEvent.Register<Item>, side: Side) {
        if (side.isClient) {
            itemColors = HashMap()
        }
        //参考 https://blog1.mammb.com/entry/2015/03/31/001620
        val classLoader = Thread.currentThread().contextClassLoader
        ClassPath.from(classLoader)
            .getTopLevelClassesRecursive("com.github.trcdeveloppers.clayium.common.items")
            .map(ClassPath.ClassInfo::load)
            .forEach { clazz ->
                val cItem = clazz.getAnnotation(CItem::class.java) ?: return@forEach
                val item = clazz.newInstance() as Item
                val registryName = cItem.registryName

                item.creativeTab = Clayium.CreativeTab
                item.registryName = ResourceLocation(MOD_ID, registryName)
                item.translationKey = "$MOD_ID.$registryName"
                event.registry.register(item)
                items[registryName] = item
                if (side.isClient) {
                    ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(ResourceLocation(MOD_ID, registryName), "inventory"))
                }
            }
        for (material in ClayiumMaterials.entries) {
            val item = if (material.hasTier()) itemWithTierTooltip(material.tier) else Item()
            val registryName = material.name.lowercase()

            item.creativeTab = Clayium.CreativeTab
            item.registryName = ResourceLocation(MOD_ID, registryName)
            item.translationKey = "$MOD_ID.$registryName"
            event.registry.register(item)
            if (material.oreDict.isNotEmpty()) {
                OreDictionary.registerOre(material.oreDict, item)
            }
            items[registryName] = item
            if (side.isClient) {
                ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(ResourceLocation(MOD_ID, registryName), "inventory"))
            }
        }
        for (material in ClayiumColoredMaterials.entries) {
            for (shape in material.shapes) {
                val item = if (material.hasTier()) itemWithTierTooltip(material.tier) else Item()
                var registryName = material.name.lowercase()
                val capitalizedName = material.materialName.substring(0, 1).uppercase() + material.materialName.substring(1)
                val oreDict = when (shape) {
                    LARGE_PLATE -> {
                        registryName = "large_" + material.materialName + "_plate"
                        "largePlate$capitalizedName"
                    }
                    INGOT, PLATE, DUST -> {
                        registryName = material.materialName + "_" + shape.name.lowercase()
                        shape.name.lowercase() + capitalizedName
                    }
                    else -> ""
                }

                item.creativeTab = Clayium.CreativeTab
                item.registryName = ResourceLocation(MOD_ID, registryName)
                item.translationKey = "$MOD_ID.$registryName"
                event.registry.register(item)
                if (oreDict.isNotEmpty()) {
                    OreDictionary.registerOre(oreDict, item)
                }
                items[registryName] = item
                if (side.isClient) {
                    itemColors[item] = IItemColor { stack, tintIndex -> material.getColor(stack, tintIndex) }
                    ModelLoader.setCustomModelResourceLocation(item, 0, shape.model)
                }
            }
        }
        registerItem(Item().setMaxDamage(100).setMaxStackSize(1), "clay_spatula", side, event)
        registerItem(Item().setMaxDamage(100).setMaxStackSize(1), "clay_rolling_pin", side, event)
        registerItem(Item().setMaxDamage(100).setMaxStackSize(1), "clay_slicer", side, event)
        registerItem(Item().setMaxStackSize(1), "clay_piping_tool", side, event)
        registerItem(Item().setMaxStackSize(1), "clay_io_configurator", side, event)
        allBlocks.forEach { (registryName: String, block: Block) ->
            event.registry.register(ItemBlock(block).setRegistryName(registryName))
            if (side.isClient) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, ModelResourceLocation(ResourceLocation(MOD_ID, registryName), "inventory"))
            }
        }
    }

    private fun registerItem(item: Item, registryName: String, side: Side, event: RegistryEvent.Register<Item>) {
        item.creativeTab = Clayium.CreativeTab
        item.registryName = ResourceLocation(MOD_ID, registryName)
        item.translationKey = "$MOD_ID.$registryName"
        event.registry.register(item)
        items[registryName] = item
        if (side.isClient) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(ResourceLocation(MOD_ID, registryName), "inventory"))
        }
    }

    @SideOnly(Side.CLIENT)
    fun registerItemColors() {
        itemColors.forEach { (item, itemColor) -> Minecraft.getMinecraft().itemColors.registerItemColorHandler(itemColor, item) }
    }

    private fun itemWithTierTooltip(tier: Int): Item {
        return object : Item() {
            @SideOnly(Side.CLIENT)
            @ParametersAreNonnullByDefault
            override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
                tooltip[0] = getRarity(tier).color.toString() + tooltip[0]
                tooltip.add(I18n.format("gui.clayium.tier", tier))
            }
        }
    }

    @JvmStatic
    fun getRarity(tier: Int): IRarity {
        return when (tier) {
            4, 5, 6, 7 -> EnumRarity.UNCOMMON
            8, 9, 10, 11 -> EnumRarity.RARE
            12, 13, 14, 15 -> EnumRarity.EPIC
            else -> EnumRarity.COMMON
        }
    }

    private enum class ClayiumMaterials constructor(val tier: Int = -1, val oreDict: String = "") {
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
        CLAY_PLATE(1),
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
        DENSE_CLAY_PLATE(2),
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
        ADV_INDUSTRIAL_CLAY_PLATE(2),
        INDUSTRIAL_CLAY_DUST(3),
        INDUSTRIAL_CLAY_SHARD(2),
        INDUSTRIAL_CLAY_PLATE(2),
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

        constructor(oreDict: String) : this(-1, oreDict)

        fun hasTier(): Boolean {
            return tier != -1
        }
    }

    private enum class ClayiumColoredMaterials {
        //region Core Materials
        SILICONE(5, CShape.INGOT_BASED, 0xD2D2D2, 0xB4B4B4, 0xF0F0F0),
        SILICON(5, CShape.INGOT_BASED, 0x281C28, 0x191919, 0xFFFFFF),
        ALUMINUM(6, CShape.INGOT_BASED, 0xBEC8CA, 0x191919, 0xFFFFFF),
        CLAY_STEEL(7, CShape.INGOT_BASED, 0x8890AD, 0x191919, 0xFFFFFF),
        CLAYIUM(8, CShape.INGOT_BASED, 0x5AF0D2, 0x3F4855, 0xFFCDC8),
        ULTIMATE_ALLOY(9, CShape.INGOT_BASED, 0x55CD55, 0x191919, 0xF5A0FF),
        ANTIMATTER(10, CShape.MATTER_BASED, 0x0000EB, 0x000000, 0xFFFFFF),
        PURE_ANTIMATTER_TIER0("pure_antimatter", 11, CShape.MATTER_BASED, 0xFF32FF, 0x000000, 0xFFFFFF),
        OCTUPLE_ENERGETIC_CLAY("oec", 12, arrayOf(PLATE, LARGE_PLATE, DUST), 0xFFFF00, 0x8C8C8C, 0xFFFFFF),
        PURE_ANTIMATTER_TIER8("opa", 13, arrayOf(MATTER5, PLATE, LARGE_PLATE, DUST), 0x960000, 0xC8C800, 0xFFFFFF),

        //endregion
        PURE_ANTIMATTER_TIER1(11, MATTER2, 0xC42385, 0x191919, 0xFFFFFF),
        PURE_ANTIMATTER_TIER2(11, MATTER2, 0x8E1777, 0x323200, 0xFFFFFF),
        PURE_ANTIMATTER_TIER3(11, MATTER3, 0x5E0D45, 0x4B4B00, 0xFFFFFF),
        PURE_ANTIMATTER_TIER4(12, MATTER3, 0x32061F, 0x646400, 0xFFFFFF),
        PURE_ANTIMATTER_TIER5(12, MATTER4, 0x520829, 0x7D7D00, 0xFFFFFF),
        PURE_ANTIMATTER_TIER6(12, MATTER4, 0x6E0727, 0x969600, 0xFFFFFF),
        PURE_ANTIMATTER_TIER7(12, MATTER4, 0x840519, 0xAFAF00, 0xFFFFFF),
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
        ACTINIUM(INGOT, 0xF5F5F5, 0x0000EB, 0xFFFFFF),
        AMERICIUM(INGOT, 0xEBEBEB, 0x9B9B9B, 0xEBEBEB),
        ANTIMONY(INGOT, 0x464646, 0x191919, 0xFFFFFF),
        BISMUTH(INGOT, 0x467846, 0x191919, 0xFFFFFF),
        CAESIUM(INGOT, 0xF5F5F5, 0x969600, 0xFFFFFF),
        CERIUM(INGOT, 0x919191, 0x969600, 0xFFFFFF),
        COBALT(INGOT, 0x1E1EE6, 0x191919, 0xFFFFFF),
        CURIUM(INGOT, 0xFFFFFF, 0x9B9B9B, 0xF4F4F4),
        EUROPIUM(INGOT, 0x919191, 0x373737, 0x919191),
        FRANCIUM(INGOT, 0xF5F5F5, 0x00EB00, 0xFFFFFF),
        IRIDIUM(INGOT, 0xF0F0F0, 0xD2D2D2, 0xEBEBEB),
        LANTHANUM(INGOT, 0x919191, 0xEB0000, 0xFFFFFF),
        MOLYBDENUM(INGOT, 0x82A082, 0x191919, 0xFFFFFF),
        NEODYMIUM(INGOT, 0x919191, 0x009696, 0xFFFFFF),
        NEPTUNIUM(INGOT, 0x3232FF, 0x32329B, 0x3232FF),
        OSMIUM(INGOT, 0x464696, 0x191919, 0xFFFFFF),
        PALLADIUM(INGOT, 0x974646, 0x191919, 0xFFFFFF),
        PLATINUM(INGOT, 0xF5F5E6, 0x8C8C78, 0xFFFFFF),
        PLUTONIUM(INGOT, 0xFF3232, 0x9B3232, 0xFF3232),
        PRASEODYMIUM(INGOT, 0x919191, 0x00EB00, 0xFFFFFF),
        PROMETHIUM(INGOT, 0x919191, 0x0000EB, 0xFFFFFF),
        PROTACTINIUM(INGOT, 0x323232, 0x191919, 0x323264),
        RADIUM(INGOT, 0xF5F5F5, 0x009696, 0xFFFFFF),
        RHENIUM(INGOT, 0x464696, 0x191919, 0x32325A),
        RUBIDIUM(INGOT, 0xF5F5F5, 0xEB0000, 0xFFFFFF),
        SAMARIUM(INGOT, 0x919191, 0x960096, 0xFFFFFF),
        SILVER(INGOT, 0xE6E6F5, 0x78788C, 0xFFFFFF),
        TANTALUM(INGOT, 0xF0D2AA, 0x191919, 0xF0D296),
        THORIUM(INGOT, 0x323232, 0x191919, 0xC83232),
        TIN(INGOT, 0xE6E6F0, 0x000000, 0xFFFFFF),
        TUNGSTEN(INGOT, 0x1E1E1E, 0x191919, 0xFFFFFF),
        URANIUM(INGOT, 0x32FF32, 0x329B32, 0x32FF32),
        VANADIUM(INGOT, 0x3C7878, 0x191919, 0xFFFFFF),
        ZINC_ALUMINUM(INGOT, 0xF0BEDC, 0xA00000, 0xFFFFFF),
        ZINC_ZIRCONIUM(INGOT, 0xE6AA8C, 0x780000, 0xFFFFFF),

        //endregion
        //region Impure Dusts
        IMPURE_ALUMINUM(6, DUST, 0xBEC8CA, 0x78783C, 0xDCDCDC),
        IMPURE_BARIUM(DUST, 0x965078, 0x78783C, 0xDCDCDC),
        IMPURE_BERYLLIUM(DUST, 0xD2F0D2, 0x78783C, 0xDCDCDC),
        IMPURE_CALCIUM(DUST, 0xF0F0F0, 0x78783C, 0xDCDCDC),
        IMPURE_COPPER(DUST, 0xA05A0A, 0x78783C, 0xDCDCDC),
        IMPURE_HAFNIUM(DUST, 0xF0D2AA, 0x78783C, 0xDCDCDC),
        IMPURE_IRON(DUST, 0xD8D8D8, 0x78783C, 0xDCDCDC),
        IMPURE_LEAD(DUST, 0xBEF0D2, 0x78783C, 0xDCDCDC),
        IMPURE_LITHIUM(DUST, 0xDCDC96, 0x78783C, 0xDCDCDC),
        IMPURE_MAGNESIUM(DUST, 0x96DC96, 0x78783C, 0xDCDCDC),
        IMPURE_MANGANESE(DUST, 0xBEF0F0, 0x78783C, 0xDCDCDC),
        IMPURE_NICKEL(DUST, 0xD2D2F0, 0x78783C, 0xDCDCDC),
        IMPURE_POTASSIUM(DUST, 0xF0F0BE, 0x78783C, 0xDCDCDC),
        IMPURE_SILICON(5, DUST, 0x978F98, 0x533764, 0xA9A5A5),
        IMPURE_SODIUM(DUST, 0xAAAAE6, 0x78783C, 0xDCDCDC),
        IMPURE_STRONTIUM(DUST, 0xD2AAF2, 0x78783C, 0xDCDCDC),
        IMPURE_TITANIUM(DUST, 0xD2F0F0, 0x78783C, 0xDCDCDC),
        IMPURE_ZINC(DUST, 0xE6AAAA, 0x78783C, 0xDCDCDC),
        IMPURE_ZIRCONIUM(DUST, 0xBEAA7A, 0x78783C, 0xDCDCDC),
        IMPURE_REDSTONE(DUST, 0x974646, 0x191919, 0xFFFFFF),
        IMPURE_GLOWSTONE(DUST, 0x979746, 0x191919, 0xFFFFFF),

        //endregion
        CARBON(DUST, 0x0A0A0A, 0x191919, 0x1E1E1E),
        COAL(DUST, 0x141414, 0x191919, 0x323250),
        CHARCOAL(DUST, 0x141414, 0x191919, 0x503232),
        GOLD(DUST, 0xFFFF0A, 0x3C3C00, 0xFFFFFF),
        IRON(DUST, 0xD8D8D8, 0x353535, 0xFFFFFF),
        LAPIS(DUST, 0x3C64BE, 0x0A2B7A, 0x5A82E2),
        ORGANIC_CLAY(DUST, 0x8890AD, 0x6A2C2B, 0x92A4B7),
        SALTPETER(DUST, 0xDEDCDC, 0xBEC8D2, 0xFFF0E6);
        //PHOSPHORUS(CShape.DUST, );
        //SULFUR(CShape.DUST, );

        val tier: Int
        val shapes: Array<CShape>
        val colors: IntArray
        val materialName: String

        constructor(materialName: String, tier: Int, shapes: Array<CShape>, vararg colors: Int) {
            this.tier = tier
            this.colors = colors
            this.shapes = shapes
            this.materialName = materialName
        }

        constructor(tier: Int, shapes: Array<CShape>, vararg colors: Int) {
            this.tier = tier
            this.colors = colors
            this.shapes = shapes
            this.materialName = name.lowercase()
        }

        constructor(tier: Int, shape: CShape, vararg colors: Int) : this(tier, arrayOf<CShape>(shape), *colors)
        constructor(shapes: Array<CShape>, vararg colors: Int) : this(NO_TIER, shapes, *colors)
        constructor(shape: CShape, vararg colors: Int) : this(NO_TIER, arrayOf<CShape>(shape), *colors)

        fun hasTier(): Boolean {
            return tier != NO_TIER
        }

        fun getColor(itemStack: ItemStack, tinIndex: Int): Int {
            return if (colors.size > tinIndex) colors[tinIndex] else 0
        }

        companion object {
            const val NO_TIER = -1
        }
    }

    @JvmStatic
    @ObjectHolder("$MOD_ID:clay_spatula")
    lateinit var CLAY_SPATULA: Item
        private set
    @JvmStatic
    @ObjectHolder("$MOD_ID:clay_rolling_pin")
    lateinit var CLAY_ROLLING_PIN: Item
        private set
    @ObjectHolder("$MOD_ID:clay_slicer")
    lateinit var CLAY_SLICER: Item
        private set
    @ObjectHolder("$MOD_ID:clay_piping_tool")
    lateinit var CLAY_PIPING_TOOL: Item
        private set
    @ObjectHolder("$MOD_ID:clay_io_configurator")
    lateinit var CLAY_IO_CONFIGURATOR: Item
        private set
}
