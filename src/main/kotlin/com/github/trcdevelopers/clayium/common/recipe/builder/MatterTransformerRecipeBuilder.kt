package com.github.trcdevelopers.clayium.common.recipe.builder

import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry

class MatterTransformerRecipeBuilder : RecipeBuilder<MatterTransformerRecipeBuilder> {

    private var defaultPrefix: OrePrefix? = null

    constructor() : super()
    constructor(another: MatterTransformerRecipeBuilder) : super(another)

    override fun copy() = MatterTransformerRecipeBuilder(this)
        .also { it.defaultPrefix = this.defaultPrefix }

    fun defaultPrefix(orePrefix: OrePrefix): MatterTransformerRecipeBuilder {
        this.defaultPrefix = orePrefix
        return this
    }

    fun input(material: Material) = input(defaultPrefix!!, material)
    fun output(material: Material) = output(defaultPrefix!!, material)

    /**
     * Returns a new builder instance with the current output set as the input,
     * and the output set to the given ore dictionary.
     * If the given oreDict does not exist, this does nothing.
     * Also sets the CEt, duration, and tier to the current values.
     * These values can be reset by calling the respective methods.
     *
     * ```
     * registry.builder()
     *    .CEt(ClayEnergy.of(1)).duration(20).tier(7)
     *    .input("ingotIron")
     *    .output("ingotCopper")
     *    .chain("ingotGold")
     *    .chain("someInvalidOreDict")
     *    .chain("gemDiamond").tier(8).duration(200)
     *    .buildAndRegister()
     * ```
     * This will generate these recipes:
     * - `ingotIron` -> `ingotCopper`
     * - `ingotCopper` -> `ingotGold`
     * - `ingotGold` -> `gemDiamond`
     */
    fun chain(oreDict: String): MatterTransformerRecipeBuilder {
        if (OreDictUnifier.get(oreDict).isEmpty) return this
        this.buildAndRegister()
        val newBuilder = this.recipeRegistry.builder()
            .tier(this.tier).cePerTick(this.cePerTick).duration(this.duration)
            .input(this.outputs[0])
            .output(oreDict)
        if (defaultPrefix != null) newBuilder.defaultPrefix(defaultPrefix!!)
        return newBuilder
    }

    fun chain(orePrefix: OrePrefix, material: Material) = chain(UnificationEntry(orePrefix, material).toString())
    fun chain(material: Material) = chain(defaultPrefix!!, material)
}