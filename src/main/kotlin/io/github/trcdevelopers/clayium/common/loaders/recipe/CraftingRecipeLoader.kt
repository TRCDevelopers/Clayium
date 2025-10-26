package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayiumApi
import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMaterial
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials.clay
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials.denseClay
import io.github.trcdevelopers.clayium.api.unification.material.MaterialAmount
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import io.github.trcdevelopers.clayium.common.blocks.metalchest.BlockMetalChest
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import io.github.trcdevelopers.clayium.common.items.ClayiumItems
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import io.github.trcdevelopers.clayium.common.recipe.RecipeUtils
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack

object CraftingRecipeLoader {

    private val OSMIUM = clayiumId("osmium")

    fun registerRecipes() {
        clayToolRecipes()
        registerClayPartsRecipes()
        registerCapsuleRecipes()

        RecipeUtils.addShapedRecipe("clay_work_table",
            ItemStack(ClayiumBlocks.CLAY_WORK_TABLE),
            "CC", "CC",
            'C', UnificationEntry(OrePrefix.block, denseClay))
        RecipeUtils.addShapedRecipe("clay_crafting_board",
            ItemStack(ClayiumBlocks.CLAY_CRAFTING_BOARD),
            "CCC",
            'C', UnificationEntry(OrePrefix.block, denseClay))

        RecipeUtils.addSmeltingRecipe(UnificationEntry(OrePrefix.ingot, CMaterials.impureSilicon),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.silicone), 0.1f)

        RecipeUtils.addShapelessRecipe("ultimate_compound_ingot", OreDictUnifier.get(OrePrefix.ingot, CMaterials.ultimateCompound, 9),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.strontium),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.barium),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.calcium),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.clayium),
            *Array(5) { OreDictUnifier.get(OrePrefix.ingot, CMaterials.aluminum) })

        for (i in 1..<CMaterials.PURE_ANTIMATTERS.size) {
            RecipeUtils.addShapelessRecipe("pure_antimatter_decompose_$i",
                OreDictUnifier.get(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i - 1], 9),
                OreDictUnifier.get(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i]))
        }

        RecipeUtils.addShapedRecipe("silicone_compress", ItemStack(ClayiumBlocks.COLORED_SILICONE),
            "III", "III", "III",
            'I', OreDictUnifier.get(OrePrefix.ingot, CMaterials.silicone)
        )
        RecipeUtils.addShapelessRecipe("silicone_decompress", OreDictUnifier.get(OrePrefix.ingot, CMaterials.silicone, 9),
            OreDictUnifier.get(OrePrefix.block, CMaterials.silicone)
        )

        for (color in EnumDyeColor.entries) {
            val dye = ItemStack(Items.DYE, 1, color.dyeDamage)
            RecipeUtils.addShapedRecipe("silicone_coloring_${color.name}", ClayiumBlocks.COLORED_SILICONE.getItem(color, 8),
                "III", "IDI", "III",
                'I', UnificationEntry(OrePrefix.block, CMaterials.silicone),
                'D', dye
            )
        }

        for (material: CMaterial in ClayiumApi.materialRegistry) {
            registerChestRecipeIfExists(material)

            if (!OrePrefix.block.isIgnored(material)
                && OreDictUnifier.exists(OrePrefix.block, material)
                && OrePrefix.block.getMaterialAmount(material) == MaterialAmount.of(9)
            ) {
                val orePrefix = if (OreDictUnifier.exists(OrePrefix.ingot, material))
                    OrePrefix.ingot
                else if (OreDictUnifier.exists(OrePrefix.gem, material))
                    OrePrefix.gem
                else
                    continue
                RecipeUtils.addShapedRecipe("${material.materialId}_compress",
                    OreDictUnifier.get(OrePrefix.block, material), "III", "III", "III",
                    'I', UnificationEntry(orePrefix, material))

                RecipeUtils.addShapelessRecipe("${material.materialId}_decompress",
                    OreDictUnifier.get(orePrefix, material, 9), UnificationEntry(OrePrefix.block, material))
            }
        }
    }

    private fun clayToolRecipes() {
        RecipeUtils.addShapedRecipe("raw_rolling_pin",
            MetaItemClayParts.RawClayRollingPin.getStackForm(),
            "sCs",
            's', UnificationEntry(OrePrefix.shortStick, clay),
            'C', UnificationEntry(OrePrefix.cylinder, clay))
        RecipeUtils.addShapedRecipe("raw_spatula",
            MetaItemClayParts.RawClaySpatula.getStackForm(),
            "sBs",
            's', UnificationEntry(OrePrefix.shortStick, clay),
            'B', UnificationEntry(OrePrefix.blade, clay))

        RecipeUtils.addSmeltingRecipe(MetaItemClayParts.RawClayRollingPin.getStackForm(), ItemStack(ClayiumItems.CLAY_ROLLING_PIN))
        RecipeUtils.addSmeltingRecipe(MetaItemClayParts.RawClaySlicer.getStackForm(), ItemStack(ClayiumItems.CLAY_SLICER))
        RecipeUtils.addSmeltingRecipe(MetaItemClayParts.RawClaySpatula.getStackForm(), ItemStack(ClayiumItems.CLAY_SPATULA))

        RecipeUtils.addShapedRecipe("clay_wrench", ItemStack(ClayiumItems.CLAY_WRENCH),
            "B B", " C ", " S ",
            'B', UnificationEntry(OrePrefix.blade, denseClay),
            'C', UnificationEntry(OrePrefix.spindle, denseClay),
            'S', UnificationEntry(OrePrefix.stick, denseClay))

        RecipeUtils.addShapedRecipe("clay_shovel", ItemStack(ClayiumItems.CLAY_SHOVEL),
            "H", "I", "I",
            'H', UnificationEntry(OrePrefix.plate, clay),
            'I', UnificationEntry(OrePrefix.stick, clay))
        RecipeUtils.addShapedRecipe("clay_pickaxe", ItemStack(ClayiumItems.CLAY_PICKAXE),
            "HHH", " I ", " I ",
            'H', UnificationEntry(OrePrefix.plate, denseClay),
            'I', UnificationEntry(OrePrefix.stick, denseClay))
        RecipeUtils.addShapedRecipe("clay_steel_pickaxe", ItemStack(ClayiumItems.CLAY_STEEL_PICKAXE),
            "HHH", " I ", " I ",
            'H', UnificationEntry(OrePrefix.ingot, CMaterials.claySteel),
            'I', UnificationEntry(OrePrefix.stick, denseClay))
        RecipeUtils.addShapedRecipe("clay_steel_shovel", ItemStack(ClayiumItems.CLAY_STEEL_SHOVEL),
            "H", "I", "I",
            'H', UnificationEntry(OrePrefix.ingot, CMaterials.claySteel),
            'I', UnificationEntry(OrePrefix.stick, denseClay))
    }

    private fun registerClayPartsRecipes() {

        RecipeUtils.addShapelessRecipe("large_clay_ball", MetaItemClayParts.LargeClayBall.getStackForm(),
            *Array(8) { Items.CLAY_BALL })
        RecipeUtils.addShapelessRecipe("clay_short_stick",
            OreDictUnifier.get(OrePrefix.shortStick, clay, 2), UnificationEntry(OrePrefix.stick, clay))
        RecipeUtils.addShapelessRecipe("clay_small_ring_loop",
            OreDictUnifier.get(OrePrefix.smallRing, clay), UnificationEntry(OrePrefix.shortStick, clay))
        RecipeUtils.addShapelessRecipe("clay_short_stick_loop",
            OreDictUnifier.get(OrePrefix.shortStick, clay), UnificationEntry(OrePrefix.smallRing, clay))
        RecipeUtils.addShapelessRecipe("clay_ring",
            OreDictUnifier.get(OrePrefix.ring, clay), UnificationEntry(OrePrefix.cylinder, clay))
        RecipeUtils.addShapelessRecipe("clay_pipe",
            OreDictUnifier.get(OrePrefix.pipe, clay), UnificationEntry(OrePrefix.plate, clay))

        RecipeUtils.addShapedRecipe("large_clay_plate",
            OreDictUnifier.get(OrePrefix.largePlate, clay),
            "CCC", "CCC", "CCC",
            'C', UnificationEntry(OrePrefix.plate, clay))
        RecipeUtils.addShapedRecipe("clay_circuit",
            MetaItemClayParts.ClayCircuit.getStackForm(),
            "SGS", "RBR", "SGS",
            'S', UnificationEntry(OrePrefix.stick, denseClay),
            'G', UnificationEntry(OrePrefix.gear, denseClay),
            'R', UnificationEntry(OrePrefix.ring, denseClay),
            'B', MetaItemClayParts.ClayCircuitBoard)
        RecipeUtils.addShapedRecipe("simple_circuit",
            MetaItemClayParts.SimpleCircuit.getStackForm(),
            "DDD", "DBD", "DDD",
            'D', MetaItemClayParts.EnergizedClayDust,
            'B', MetaItemClayParts.ClayCircuitBoard)

        for (m in listOf(clay, denseClay)) {
            RecipeUtils.addShapedRecipe("${m.materialId.path}_gear",
                OreDictUnifier.get(OrePrefix.gear, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.shortStick, m),
                'C', UnificationEntry(OrePrefix.smallRing, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_cutting_head",
                OreDictUnifier.get(OrePrefix.cuttingHead, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.blade, m),
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_bearing",
                OreDictUnifier.get(OrePrefix.bearing, m),
                "III", "ICI", "III",
                'I', Items.CLAY_BALL,
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_grinding_head",
                OreDictUnifier.get(OrePrefix.grindingHead, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.needle, m),
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_water_wheel",
                OreDictUnifier.get(OrePrefix.wheel, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.plate, m),
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_spindle",
                OreDictUnifier.get(OrePrefix.spindle, m),
                "rPr", "SBR", "rPr",
                'r', UnificationEntry(OrePrefix.smallRing, m),
                'R', UnificationEntry(OrePrefix.ring, m),
                'P', UnificationEntry(OrePrefix.plate, m),
                'S', UnificationEntry(OrePrefix.stick, m),
                'B', UnificationEntry(OrePrefix.bearing, m))
        }

        RecipeUtils.addShapelessRecipe("item_filter_translation_key", ItemStack(ClayiumItems.UNLOCALIZED_NAME_ITEM_FILTER),
            ClayiumItems.DISPLAY_NAME_ITEM_FILTER)
        RecipeUtils.addShapelessRecipe("item_filter_ore_dict", ItemStack(ClayiumItems.ORE_DICT_ITEM_FILTER),
            ClayiumItems.UNLOCALIZED_NAME_ITEM_FILTER)
        RecipeUtils.addShapelessRecipe("item_filter_registry_name", ItemStack(ClayiumItems.REGISTRY_NAME_ITEM_FILTER),
            ClayiumItems.ORE_DICT_ITEM_FILTER)
        RecipeUtils.addShapelessRecipe("item_filter_mod_id", ItemStack(ClayiumItems.MOD_ID_ITEM_FILTER),
            ClayiumItems.REGISTRY_NAME_ITEM_FILTER)
        RecipeUtils.addShapelessRecipe("item_filter_damage_value", ItemStack(ClayiumItems.DAMAGE_VALUE_ITEM_FILTER),
            ClayiumItems.MOD_ID_ITEM_FILTER)
        RecipeUtils.addShapelessRecipe("item_filter_cycle", ItemStack(ClayiumItems.DISPLAY_NAME_ITEM_FILTER),
            ClayiumItems.DAMAGE_VALUE_ITEM_FILTER)
        RecipeUtils.addShapelessRecipe("item_filter_block_metadata", ItemStack(ClayiumItems.BLOCK_METADATA_ITEM_FILTER),
            ClayiumItems.DAMAGE_VALUE_ITEM_FILTER, Blocks.CLAY)

        RecipeUtils.addShapelessRecipe("item_filter_duplicator", ItemStack(ClayiumItems.ITEM_FILTER_DUPLICATOR),
            ClayiumItems.SIMPLE_ITEM_FILTER, ClayiumItems.DISPLAY_NAME_ITEM_FILTER, ClayiumItems.FUZZY_ITEM_FILTER)
    }

    private fun registerCapsuleRecipes() {
        RecipeUtils.addShapedRecipe("fluid_capsule",
            ItemStack(ClayiumItems.FLUID_CAPSULE_1000MB),
            " C ", "C C", " C ",
            'C', UnificationEntry(OrePrefix.block, denseClay))

        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_125",
            ItemStack(ClayiumItems.FLUID_CAPSULE_125MB, 8),
            ClayiumItems.FLUID_CAPSULE_1000MB)
        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_25",
            ItemStack(ClayiumItems.FLUID_CAPSULE_25MB, 5),
            ClayiumItems.FLUID_CAPSULE_125MB)
        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_5",
            ItemStack(ClayiumItems.FLUID_CAPSULE_5MB, 5),
            ClayiumItems.FLUID_CAPSULE_25MB)
        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_1",
            ItemStack(ClayiumItems.FLUID_CAPSULE_1MB, 5),
            ClayiumItems.FLUID_CAPSULE_5MB)

        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_1000_2",
            ItemStack(ClayiumItems.FLUID_CAPSULE_1000MB),
            ClayiumItems.FLUID_CAPSULE_125MB, ClayiumItems.FLUID_CAPSULE_125MB,
            ClayiumItems.FLUID_CAPSULE_125MB, ClayiumItems.FLUID_CAPSULE_125MB,
            ClayiumItems.FLUID_CAPSULE_125MB, ClayiumItems.FLUID_CAPSULE_125MB,
            ClayiumItems.FLUID_CAPSULE_125MB, ClayiumItems.FLUID_CAPSULE_125MB,
        )
        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_125_2",
            ItemStack(ClayiumItems.FLUID_CAPSULE_125MB),
            ClayiumItems.FLUID_CAPSULE_25MB, ClayiumItems.FLUID_CAPSULE_25MB,
            ClayiumItems.FLUID_CAPSULE_25MB, ClayiumItems.FLUID_CAPSULE_25MB,
            ClayiumItems.FLUID_CAPSULE_25MB,
        )
        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_25_2",
            ItemStack(ClayiumItems.FLUID_CAPSULE_25MB),
            ClayiumItems.FLUID_CAPSULE_5MB, ClayiumItems.FLUID_CAPSULE_5MB,
            ClayiumItems.FLUID_CAPSULE_5MB, ClayiumItems.FLUID_CAPSULE_5MB,
            ClayiumItems.FLUID_CAPSULE_5MB,
        )
        RecipeUtils.addShapelessRecipeNbt("fluid_capsule_5_2",
            ItemStack(ClayiumItems.FLUID_CAPSULE_5MB),
            ClayiumItems.FLUID_CAPSULE_1MB, ClayiumItems.FLUID_CAPSULE_1MB,
            ClayiumItems.FLUID_CAPSULE_1MB, ClayiumItems.FLUID_CAPSULE_1MB,
            ClayiumItems.FLUID_CAPSULE_1MB,
        )
    }

    private fun registerChestRecipeIfExists(material: CMaterial) {
        if (BlockMetalChest.metalChestConfig[material.materialId] == null) {
            return
        }
        val prefix = when {
            OreDictUnifier.exists(OrePrefix.ingot, material) -> OrePrefix.ingot
            OreDictUnifier.exists(OrePrefix.gem, material) -> OrePrefix.gem
            else -> return
        }

        if (ConfigCore.gameMode.hardcoreOsmium && material.materialId == OSMIUM) {
            // handle hardcore osmium
            RecipeUtils.addShapedRecipe("metal_chest_${material.materialId.namespace}_${material.materialId.path}",
                ItemStack(ClayiumBlocks.METAL_CHEST, 1, CMaterials.osmium.metaItemSubId),
                "MMM", "MCM", "MMM",
                'M', UnificationEntry(prefix, CMaterials.impureOsmium),
                'C', Blocks.CHEST)
            RecipeUtils.addShapedRecipe("metal_chest_${material.materialId.namespace}_${material.materialId.path}_recraft",
                ItemStack(ClayiumBlocks.METAL_CHEST, 1, CMaterials.osmium.metaItemSubId),
                "MMM", "MCM", "MMM",
                'M', UnificationEntry(prefix, CMaterials.impureOsmium),
                'C', ClayiumBlocks.METAL_CHEST)
        } else {
            RecipeUtils.addShapedRecipe("metal_chest_${material.materialId.namespace}_${material.materialId.path}",
                ItemStack(ClayiumBlocks.METAL_CHEST, 1, material.metaItemSubId),
                "MMM", "MCM", "MMM",
                'M', UnificationEntry(prefix, material),
                'C', Blocks.CHEST)
            RecipeUtils.addShapedRecipe("metal_chest_${material.materialId.namespace}_${material.materialId.path}_recraft",
                ItemStack(ClayiumBlocks.METAL_CHEST, 1, material.metaItemSubId),
                "MMM", "MCM", "MMM",
                'M', UnificationEntry(prefix, material),
                'C', ClayiumBlocks.METAL_CHEST)
        }

    }
}