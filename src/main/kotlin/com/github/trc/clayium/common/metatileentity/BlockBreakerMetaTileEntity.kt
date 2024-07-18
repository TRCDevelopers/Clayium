package com.github.trc.clayium.common.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IClayLaserAcceptor
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class BlockBreakerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : MetaTileEntity(
    metaTileEntityId,
    tier,
    validInputModes = validInputModesLists[0],
    validOutputModesLists[1],
    translationKey = "machine.${CValues.MOD_ID}.block_breaker"
), IClayLaserAcceptor {
    private var laser: Array<IClayLaser?> = arrayOfNulls(6)
    private var laserPower: Double = 0.0;
    override val faceTexture: ResourceLocation = clayiumId("blocks/block_breaker")
    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val itemInventory: IItemHandler = EmptyItemStackHandler

    override fun createMetaTileEntity(): MetaTileEntity {
        return BlockBreakerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR) {
            return ClayiumTileCapabilities.CAPABILITY_CLAY_LASER_ACCEPTOR.cast(this);
        }
        return super.getCapability(capability, facing)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("clay_multi_track_buffer"), "tier=${tier.numeric}"))

    }

    override fun buildUI(
        data: PosGuiData?,
        syncManager: GuiSyncManager?
    ): ModularPanel? {
        TODO("Not yet implemented")
    }

    override fun laserChanged(
        irradiatedSide: EnumFacing,
        laser: IClayLaser?
    ) {
        this.laser[irradiatedSide.index] = laser
    }

    override fun update() {
        if (!isRemote) {
            (0..5).map {
                if (this.laser[it] != null)
                    this.laserPower += this.laser[it]!!.laserEnergy
            }
            println("$laserPower")
        }
        super.update()
    }

}