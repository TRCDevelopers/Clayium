package com.github.trc.clayium.api.gui

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.AbstractUIFactory
import com.cleanroommc.modularui.factory.GuiManager
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object MetaTileEntityGuiFactory :
    AbstractUIFactory<MetaTileEntityGuiData>("$MOD_ID:metatileentity") {

    fun <T> open(player: EntityPlayer, metaTileEntity: T) where T : MetaTileEntity {
        val data =
            MetaTileEntityGuiData.WorldAndPos(
                player,
                metaTileEntity.world ?: return,
                metaTileEntity.pos ?: return
            )
        GuiManager.open(this, data, player as EntityPlayerMP)
    }

    @JvmOverloads
    fun open(player: EntityPlayer, pos: BlockPos, world: World = player.world) {
        val data = MetaTileEntityGuiData.WorldAndPos(player, world, pos)
        GuiManager.open(this, data, player as EntityPlayerMP)
    }

    override fun getGuiHolder(data: MetaTileEntityGuiData): IGuiHolder<MetaTileEntityGuiData> {
        return data.metaTileEntity
    }

    override fun writeGuiData(guiData: MetaTileEntityGuiData, buffer: PacketBuffer) {
        // on the server side, guiData should always be WorldAndPos
        if (guiData !is MetaTileEntityGuiData.WorldAndPos) return
        val playerWorldType = guiData.player.world.provider.dimensionType
        val mteWorldType = guiData.world.provider.dimensionType
        if (playerWorldType == mteWorldType) {
            buffer.writeBoolean(true)
            buffer.writeBlockPos(guiData.pos)
        } else {
            buffer.writeBoolean(false)
            buffer.writeResourceLocation(guiData.metaTileEntity.metaTileEntityId)
        }
    }

    override fun readGuiData(player: EntityPlayer, buffer: PacketBuffer): MetaTileEntityGuiData {
        val isSameDimension = buffer.readBoolean()
        if (isSameDimension) {
            val pos = buffer.readBlockPos()
            return MetaTileEntityGuiData.WorldAndPos(player, player.world, pos)
        } else {
            val mteId = buffer.readResourceLocation()
            return MetaTileEntityGuiData.Id(player, mteId)
        }
    }
}
