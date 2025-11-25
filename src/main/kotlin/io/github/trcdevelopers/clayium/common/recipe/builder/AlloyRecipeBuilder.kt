package io.github.trcdevelopers.clayium.common.recipe.builder

import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.util.Mods
import net.minecraftforge.fml.common.Optional

class AlloyRecipeBuilder : RecipeBuilder<AlloyRecipeBuilder> {

    constructor() : super()
    constructor(another: AlloyRecipeBuilder) : super(another)

    override fun copy(): AlloyRecipeBuilder {
        return AlloyRecipeBuilder(this)
    }

    /**
     * Inputs ingot or dust.
     */
    fun input(material: IMaterial, amount: Int = 1): AlloyRecipeBuilder {
        return this.input(listOf(OrePrefix.ingot, OrePrefix.dust), material, amount)
    }

    /**
     * Inputs ingot or dust.
     */
    @Optional.Method(modid = Mods.Names.GREGTECH)
    fun input(material: GtMaterial, amount: Int = 1): AlloyRecipeBuilder {
        return this.input(listOf(OrePrefix.ingot, OrePrefix.dust), material, amount)
    }
}