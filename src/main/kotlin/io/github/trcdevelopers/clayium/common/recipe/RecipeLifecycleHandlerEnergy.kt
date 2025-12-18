package io.github.trcdevelopers.clayium.common.recipe

import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.recipe.RecipeLifecycleHandler
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler

class RecipeLifecycleHandlerEnergy(
    private val outputInventory: IItemHandler,
    private val clayEnergyHolder: ClayEnergyHolder,
) : RecipeLifecycleHandler {

    private var outputs = emptyList<ItemStack>()

    private var outputsFull = false

    override fun tryStartCrafting(job: RecipeProcessingJob): Boolean {
        if (!clayEnergyHolder.drawEnergy(job.clayEnergyPerTick, simulate = true)) return false
        val outputs = job.outputs.take(outputInventory.slots)
        if (!TransferUtils.insertToHandler(outputInventory, outputs, true)) {
            this.outputsFull = true
            return false
        }
        this.outputs = outputs
        return true
    }

    override fun completeCrafting() {
        TODO("Not yet implemented")
    }

}