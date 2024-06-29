package com.github.trcdevelopers.clayium.common.recipe.registry

import com.github.trcdevelopers.clayium.common.recipe.builder.ClayFabricatorRecipeBuilder
import com.github.trcdevelopers.clayium.common.recipe.builder.RecipeBuilder
import com.github.trcdevelopers.clayium.common.recipe.builder.SimpleRecipeBuilder

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
    val MATTER_TRANSFORMER = addRegistry("matter_transformer", SimpleRecipeBuilder(), 1, 1)
    val MILLING_MACHINE = addRegistry("milling_machine", SimpleRecipeBuilder(), 1, 1)
    val PIPE_DRAWING_MACHINE = addRegistry("pipe_drawing_machine", SimpleRecipeBuilder(), 1, 1)
    val WIRE_DRAWING_MACHINE = addRegistry("wire_drawing_machine", SimpleRecipeBuilder(), 1, 1)
    val SMELTER = addRegistry("smelter", SimpleRecipeBuilder(), 1, 1)
    val SOLAR_CLAY_FABRICATOR = addRegistry("solar_clay_fabricator", ClayFabricatorRecipeBuilder(ClayFabricatorRecipeBuilder::solarClayFabricator), 1, 1)
    val CA_CONDENSER = addRegistry("ca_condenser", SimpleRecipeBuilder(), 1, 1)
    /**
     * todo: solar clay fabricator is maybe special
     * todo: electrolysis reactor can be dynamic ore-prefixed recipe
     * todo: energetic clay decomposer
     */

    // 2 -> 1 recipes
    val ASSEMBLER = addRegistry("assembler", SimpleRecipeBuilder(), 2, 1)
    val INSCRIBER = addRegistry("inscriber", SimpleRecipeBuilder(), 2, 1)
    val ALLOT_SMELTER = addRegistry("allot_smelter", SimpleRecipeBuilder(), 2, 1)
    val CA_INJECTOR = addRegistry("ca_injector", SimpleRecipeBuilder(), 2, 1) //todo special registry or builder?

    // others
    val CHEMICAL_REACTOR = addRegistry("chemical_reactor", SimpleRecipeBuilder(), 2, 2)
    val CLAY_BLAST_FURNACE = addRegistry("clay_blast_furnace", SimpleRecipeBuilder(), 2, 2)
    val CLAY_REACTOR = addRegistry("clay_reactor", SimpleRecipeBuilder(), 2, 2)

    fun <R: RecipeBuilder<R>> addRegistry(name: String, buildSample: R, inputSize: Int, outputSize: Int): RecipeRegistry<R> {
        val registry = RecipeRegistry(name, buildSample, inputSize, outputSize)
        REGISTRY[name] = registry
        return registry
    }

    fun findRegistry(name: String): RecipeRegistry<*>? {
        return REGISTRY[name]
    }
}