package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.recipe.RecipeJobProvider
import io.github.trcdevelopers.clayium.common.recipe.RecipeProgressTracker
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

abstract class AbstractWorkableV2(
    metaTileEntity: MetaTileEntity,
    private val progressTracker: RecipeProgressTracker,
    private val jobProvider: RecipeJobProvider,
) : MTETrait(metaTileEntity, "TODO_REPLACE_ME") {
    private val syncManager = metaTileEntity.clayiumSyncManager

    private var itemOutputs: List<ItemStack> = emptyList()

    override fun update() {
        super.update()
        if (metaTileEntity.isRemote) return

        if (!progressTracker.isProcessingRecipe) {
            val newJob = jobProvider.provide()
            if (newJob != null) {
                progressTracker.startProcessing(newJob.requiredWork)
                this.itemOutputs = newJob.outputs
            }
        }

        progressTracker.updateServer()
        if (progressTracker.isCompleted()) {
            progressTracker.reset()
            itemOutputs = emptyList()
            TransferUtils.insertToHandler(metaTileEntity.exportItems, itemOutputs)
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return when {
            capability === ClayiumTileCapabilities.CONTROLLABLE -> capability.cast(this.progressTracker)
            else -> super.getCapability(capability, facing)
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = super.serializeNBT()
        nbt.setTag("progressTracker", progressTracker.serializeNBT())
        return nbt
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        progressTracker.deserializeNBT(data.getCompoundTag("progressTracker"))
    }
}