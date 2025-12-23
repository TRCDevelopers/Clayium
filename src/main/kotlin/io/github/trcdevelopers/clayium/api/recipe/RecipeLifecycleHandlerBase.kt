package io.github.trcdevelopers.clayium.api.recipe

import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.CUtils
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * provides an startCrafting/completeCrafting method for recipe-driven workable.
 * also handles NBT (de)serialization of [itemOutputs].
 *
 * startCrafting: [trySearchNewRecipe] method, which is called only when new recipe is possible to match.
 * i.e. this method is not called when (OR):
 * - Inputs were invalid in the last recipe search, and not changed since then.
 * - Outputs were full in the last recipe search, and not changed since then.
 *
 * completeCrafting: set [itemOutputs]. These items are inserted into output inventory by [completeCrafting].
 */
abstract class RecipeLifecycleHandlerBase(
    protected val metaTileEntity: MetaTileEntity,
) : RecipeLifecycleHandler {

    protected var wasInputsInvalid = false
    protected var wasOutputsFull = false

    protected var itemOutputs = emptyList<ItemStack>()

    override fun tryStartCrafting(machineTier: Int, inputs: List<ItemStack>): RecipeProcessingJob? {
        if (this.shouldSearchForRecipe()) {
            return this.trySearchNewRecipe(machineTier, inputs)
        }
        return null
    }

    override fun completeCrafting() {
        TransferUtils.insertToHandler(metaTileEntity.exportItems, this.itemOutputs)
    }

    protected abstract fun trySearchNewRecipe(machineTier: Int, inputs: List<ItemStack>): RecipeProcessingJob?

    override fun serializeNBT(): NBTTagCompound {
        val tag = NBTTagCompound()
        CUtils.writeItems(this.itemOutputs, "Outputs", tag)
        return tag
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        this.itemOutputs = CUtils.readItems("Outputs", nbt)
    }

    private fun shouldSearchForRecipe(): Boolean {
        return this.canWorkWithInputs() && this.canFitNewOutputs()
    }

    private fun canWorkWithInputs(): Boolean {
        if (this.wasInputsInvalid && !this.metaTileEntity.hasNotifiedInputs) return false

        this.wasInputsInvalid = false
        this.metaTileEntity.hasNotifiedInputs = false
        return true
    }

    private fun canFitNewOutputs(): Boolean {
        this.wasOutputsFull = false
        return true

        // currently, NotifiableItemStackHandler.onContentsChanged isn't called
        // if the item is extracted without pressing a shift key in GUI.
        // therefore, metaTileEntity.hasNotifiedOutputs is remains false in that case.
        // so output full check is disabled.

//        if (outputsFull && !metaTileEntity.hasNotifiedOutputs) return false
//
//        outputsFull = false
//        metaTileEntity.hasNotifiedOutputs = false
//        return true
    }
}