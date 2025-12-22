package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.trait.OverclockHandler
import io.github.trcdevelopers.clayium.api.recipe.RecipeLifecycleHandler
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class RecipeLifecycleHandlerEnergy(
    private val metaTileEntity: MetaTileEntity,
    private val recipeRegistry: RecipeRegistry<*>,
    private val clayEnergyHolder: ClayEnergyHolder,
) : RecipeLifecycleHandler {

    private val inputInventory: IItemHandlerModifiable get() = metaTileEntity.importItems
    private val outputInventory: IItemHandler get() = metaTileEntity.exportItems
    private val overclockHandler: OverclockHandler get() = metaTileEntity.overclockHandler

    private var previousRecipe: Recipe? = null
    private var outputs = emptyList<ItemStack>()

    private var wasInputsInvalid = false
    private var wasOutputsFull = false

    override fun tryStartCrafting(machineTier: Int, inputs: List<ItemStack>): RecipeProcessingJob? {
        val recipe = this.previousRecipe?.takeIf { it.matches(inputs, machineTier) }
            ?: this.recipeRegistry.searchRecipe(machineTier, inputs)

        if (recipe == null) {
            this.wasInputsInvalid = true
            return null
        }

        return this.startCrafting(recipe, machineTier)
    }

    private fun startCrafting(recipe: Recipe, tier: Int): RecipeProcessingJob? {
        if (!this.clayEnergyHolder.drawEnergy(recipe.cePerTick, simulate = true)) return null
        val outputs = recipe.copyOutputs().take(outputInventory.slots)
        if (!TransferUtils.insertToHandler(outputInventory, outputs, true)) {
            this.wasOutputsFull = true
            return null
        }

        if (!recipe.matches(true, inputInventory, tier)) return null
        val (cePerTick, duration) = overclockHandler.applyOverclock(recipe.cePerTick, recipe.duration)
        this.outputs = outputs
        return RecipeProcessingJob(
            requiredWork = duration,
            clayEnergyPerTick = ClayEnergy(cePerTick),
        )
    }

    override fun completeCrafting() {
        TransferUtils.insertToHandler(this.outputInventory, this.outputs)
    }
}