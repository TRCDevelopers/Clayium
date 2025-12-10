package io.github.trcdevelopers.clayium.api.capability

import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.sync.ClayiumSyncManager
import io.github.trcdevelopers.clayium.common.recipe.RecipeProgressTracker
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

abstract class AbstractWorkableV2(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, "TODO_REPLACE_ME") {
    private val syncManager = ClayiumSyncManager()

    private val progressTracker = RecipeProgressTracker(syncManager, metaTileEntity.overclockHandler)

    override fun update() {
        super.update()
        if (metaTileEntity.isRemote) return

        progressTracker.updateServer()
        if (progressTracker.isCompleted()) {

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