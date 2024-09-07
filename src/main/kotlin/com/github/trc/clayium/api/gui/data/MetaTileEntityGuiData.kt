package com.github.trc.clayium.api.gui.data

import com.cleanroommc.modularui.factory.GuiData
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.getMetaTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

sealed class MetaTileEntityGuiData(
    player: EntityPlayer,
) : GuiData(player) {
    abstract val metaTileEntity: MetaTileEntity

    class WorldAndPos(
        player: EntityPlayer,
        val world: World,
        val pos: BlockPos,
    ) : MetaTileEntityGuiData(player) {
        override val metaTileEntity: MetaTileEntity by lazy {
            this.world.getMetaTileEntity(pos)
                ?: throw IllegalStateException("Could not find a MetaTileEntity at $pos in ${this.world.provider?.dimensionType}")
        }
    }

    class Id(
        player: EntityPlayer,
        private val metaTileEntityId: ResourceLocation,
    ) : MetaTileEntityGuiData(player) {
        override val metaTileEntity: MetaTileEntity by lazy {
            ClayiumApi.MTE_REGISTRY.getObject(metaTileEntityId)?.createMetaTileEntity()
                ?: throw IllegalStateException("Could not find a MetaTileEntity Id $metaTileEntityId")
        }
    }
}