package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.FALLBACK
import io.github.trcdevelopers.clayium.api.FALLBACK_L
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.input.CItemRecipeInputV2
import io.github.trcdevelopers.clayium.api.recipe.input.COreRecipeInputV2
import io.github.trcdevelopers.clayium.api.recipe.input.CRecipeInputV2
import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayium
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

@Suppress("UNCHECKED_CAST")
abstract class RecipeBuilderV2<T: RecipeBuilderV2<T>>(
) {

    private val itemInputs: MutableList<CRecipeInputV2> = mutableListOf()
    private var outputs: IRecipeOutputs? = null
    private var duration: Long = FALLBACK_L
    private var cePerTick: ClayEnergy = ClayEnergy.ZERO
    private var recipeTier: Int = FALLBACK
    private var priority: Int = 0

    fun priority(priority: Int): T {
        this.priority = priority
        return this as T
    }

    fun input(input: CRecipeInputV2): T {
        this.itemInputs.add(input)
        return this as T
    }

    fun input(stack: ItemStack) = this.input(CItemRecipeInputV2(listOf(stack), stack.count))
    fun input(item: Item, amount: Int = 1) = input(ItemStack(item, amount))
    fun input(metaItem: MetaItemClayium.MetaValueItem, amount: Int = 1) = input(metaItem.getStackForm(amount))
    fun input(metaTileEntity: MetaTileEntity, amount: Int = 1) = input(metaTileEntity.asStackForm(amount))
    fun input(block: Block, amount: Int = 1) = input(ItemStack(block, amount))
    fun input(oreDict: String, amount: Int = 1) = input(COreRecipeInputV2(oreDict, amount))
    fun input(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = input(COreRecipeInputV2(orePrefix, material, amount))

    fun build(): RecipeV2 {
        val outputs = this.outputs
            ?: throw IllegalStateException("Outputs must be set to build a recipe")
        if (duration < 0) {
            throw IllegalStateException("Duration must be >= 0 to build a recipe")
        }
        if (recipeTier < 0) {
            throw IllegalStateException("Recipe tier must be >= 0 to build a recipe")
        }

        return RecipeV2(
            this.itemInputs, outputs,
            this.duration, this.cePerTick,
            this.recipeTier, this.priority
            )
    }
}