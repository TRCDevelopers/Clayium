package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.recipe.builder.ClayFabricatorRecipeBuilder
import com.github.trc.clayium.common.recipe.builder.MatterTransformerRecipeBuilder
import com.github.trc.clayium.common.recipe.builder.RecipeBuilder
import com.github.trc.clayium.common.recipe.builder.SimpleRecipeBuilder
import com.github.trc.clayium.common.recipe.builder.WeightedRecipeBuilder
import com.github.trc.clayium.integration.jei.JeiPlugin
import com.github.trc.clayium.integration.jei.basic.SolarClayFabricatorRecipeWrapper

object CRecipes {
    private val REGISTRY = mutableMapOf<String, RecipeRegistry<*>>()
    val ALL_REGISTRIES: Map<String, RecipeRegistry<*>> get() = REGISTRY.toMap()

    // 1 -> 1 recipes
    val BENDING = addRegistry("bending_machine", SimpleRecipeBuilder(), inputSize = 1, outputSize = 1)
    val CONDENSER = addRegistry("condenser", SimpleRecipeBuilder(), 1, 1)
    val CUTTING_MACHINE = addRegistry("cutting_machine", SimpleRecipeBuilder(), 1, 1)
    val DECOMPOSER = addRegistry("decomposer", SimpleRecipeBuilder(), 1, 1)
    val ENERGETIC_CLAY_CONDENSER = addRegistry("energetic_clay_condenser", SimpleRecipeBuilder(), 1, 1)
    val ELECTROLYSIS_REACTOR = addRegistry("electrolysis_reactor", SimpleRecipeBuilder(), 1, 1)
    val GRINDER = addRegistry("grinder", SimpleRecipeBuilder(), 1, 1)
    val LATHE = addRegistry("lathe", SimpleRecipeBuilder(), 1, 1)
    val MATTER_TRANSFORMER = addRegistry("matter_transformer", MatterTransformerRecipeBuilder(), 1, 1)
    val MILLING_MACHINE = addRegistry("milling_machine", SimpleRecipeBuilder(), 1, 1)
    val PIPE_DRAWING_MACHINE = addRegistry("pipe_drawing_machine", SimpleRecipeBuilder(), 1, 1)
    val WIRE_DRAWING_MACHINE = addRegistry("wire_drawing_machine", SimpleRecipeBuilder(), 1, 1)
    val SMELTER = addRegistry("smelter", SimpleRecipeBuilder(), 1, 1)
    val CA_CONDENSER = addRegistry("ca_condenser", SimpleRecipeBuilder(), 1, 1)

    //solar
    val SOLAR_1 = addRegistry("solar_clay_fabricator_1", ClayFabricatorRecipeBuilder(ClayFabricatorRecipeBuilder::solarClayFabricator), 1, 1)
    val SOLAR_2 = addRegistry("solar_clay_fabricator_2", ClayFabricatorRecipeBuilder(ClayFabricatorRecipeBuilder::solarClayFabricator), 1, 1)
    val SOLAR_3 = addRegistry("solar_clay_fabricator_3", ClayFabricatorRecipeBuilder(ClayFabricatorRecipeBuilder::solarClayFabricator), 1, 1)

    // 2 -> 1 recipes
    val ASSEMBLER = addRegistry("assembler", SimpleRecipeBuilder(), 2, 1)
    val INSCRIBER = addRegistry("inscriber", SimpleRecipeBuilder(), 2, 1)
    val ALLOT_SMELTER = addRegistry("allot_smelter", SimpleRecipeBuilder(), 2, 1)
    val CA_INJECTOR = addRegistry("ca_injector", SimpleRecipeBuilder(), 2, 1) //todo special registry or builder?

    // others
    val CHEMICAL_REACTOR = addRegistry("chemical_reactor", SimpleRecipeBuilder(), 2, 2)
    val CLAY_BLAST_FURNACE = addRegistry("clay_blast_furnace", SimpleRecipeBuilder(), 2, 2)
    val CLAY_REACTOR = addRegistry("clay_reactor", SimpleRecipeBuilder(), 2, 2)
    val CHEMICAL_METAL_SEPARATOR = addRegistry("chemical_metal_separator", WeightedRecipeBuilder(), 1, 1)
    val CENTRIFUGE = addRegistry("centrifuge", SimpleRecipeBuilder(), 1, 4)

    fun <R: RecipeBuilder<R>> addRegistry(name: String, buildSample: R, inputSize: Int, outputSize: Int): RecipeRegistry<R> {
        val registry = RecipeRegistry(name, buildSample, inputSize, outputSize)
        REGISTRY[name] = registry
        return registry
    }

    fun findRegistry(name: String): RecipeRegistry<*>? {
        return REGISTRY[name]
    }

    init {
        if (Mods.JustEnoughItems.isModLoaded) {
            JeiPlugin.registerWrapper(SOLAR_1, ::SolarClayFabricatorRecipeWrapper)
            JeiPlugin.registerWrapper(SOLAR_2, ::SolarClayFabricatorRecipeWrapper)
            JeiPlugin.registerWrapper(SOLAR_3, ::SolarClayFabricatorRecipeWrapper)
        }
    }
}