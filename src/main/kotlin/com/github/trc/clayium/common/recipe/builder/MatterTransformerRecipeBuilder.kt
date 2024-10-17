package com.github.trc.clayium.common.recipe.builder

import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class MatterTransformerRecipeBuilder : RecipeBuilder<MatterTransformerRecipeBuilder> {

    private var defaultPrefix: OrePrefix? = null

    constructor() : super()

    constructor(another: MatterTransformerRecipeBuilder) : super(another)

    private val chains = mutableListOf<Any>()

    override fun copy() =
        MatterTransformerRecipeBuilder(this).also { it.defaultPrefix = this.defaultPrefix }

    fun defaultPrefix(orePrefix: OrePrefix): MatterTransformerRecipeBuilder {
        this.defaultPrefix = orePrefix
        return this
    }

    fun input(material: CMaterial) = input(defaultPrefix!!, material)

    fun output(material: CMaterial) = output(defaultPrefix!!, material)

    /**
     * Returns a new builder instance with the current output set as the input, and the output set
     * to the given ore dictionary. If the given oreDict does not exist, this does nothing. Also
     * sets the CEt, duration, and tier to the current values. These values can be reset by calling
     * the respective methods.
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
     *
     * This will generate these recipes:
     * - `ingotIron` -> `ingotCopper`
     * - `ingotCopper` -> `ingotGold`
     * - `ingotGold` -> `gemDiamond`
     */
    fun chain(oreDict: String): MatterTransformerRecipeBuilder {
        if (OreDictUnifier.get(oreDict).isEmpty) return this
        this.buildAndRegister()
        val newBuilder =
            this.recipeRegistry
                .builder()
                .tier(this.tier)
                .CEt(this.cePerTick)
                .duration(this.duration)
                .input(this.outputs[0])
                .output(oreDict)
        if (defaultPrefix != null) newBuilder.defaultPrefix(defaultPrefix!!)
        return newBuilder
    }

    fun chain(orePrefix: OrePrefix, material: CMaterial) =
        chain(UnificationEntry(orePrefix, material).toString())

    fun chain(material: CMaterial) = chain(defaultPrefix!!, material)

    fun chain(block: Block): MatterTransformerRecipeBuilder {
        this.buildAndRegister()
        val newBuilder =
            this.recipeRegistry
                .builder()
                .tier(this.tier)
                .CEt(this.cePerTick)
                .duration(this.duration)
                .input(this.outputs[0])
                .output(block)
        return newBuilder
    }

    fun chain(item: Item): MatterTransformerRecipeBuilder {
        this.buildAndRegister()
        val newBuilder =
            this.recipeRegistry
                .builder()
                .tier(this.tier)
                .CEt(this.cePerTick)
                .duration(this.duration)
                .input(this.outputs[0])
                .output(item)
        return newBuilder
    }

    fun chain(stack: ItemStack): MatterTransformerRecipeBuilder {
        this.buildAndRegister()
        val newBuilder =
            this.recipeRegistry
                .builder()
                .tier(this.tier)
                .CEt(this.cePerTick)
                .duration(this.duration)
                .input(this.outputs[0])
                .output(stack)
        return newBuilder
    }
}
