package com.github.trc.clayium.api.gui

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.AbstractUIFactory
import com.cleanroommc.modularui.factory.GuiManager
import com.cleanroommc.modularui.factory.PosGuiData
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.gui.data.WorldPosGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object MetaTileEntityGuiFactory : AbstractUIFactory<PosGuiData>("${CValues.MOD_ID}:metatileentity") {

    fun open(player: EntityPlayer, pos: BlockPos) {
        val data = PosGuiData(player, pos.x, pos.y, pos.z)
        GuiManager.open(this, data, player as EntityPlayerMP)
    }

    fun open(player: EntityPlayer, pos: BlockPos, world: World) {
        val data = WorldPosGuiData(player, pos, world)
        GuiManager.open(this, data, player as EntityPlayerMP)
    }

    override fun getGuiHolder(data: PosGuiData): IGuiHolder<PosGuiData> {
        return (data.tileEntity as? MetaTileEntityHolder)?.metaTileEntity as? IGuiHolder<PosGuiData>
            ?: throw NullPointerException("Found MetaTileEntity is not a gui holder!")
    }

    override fun writeGuiData(guiData: PosGuiData, buffer: PacketBuffer) {
        buffer.writeVarInt(guiData.x)
        buffer.writeVarInt(guiData.y)
        buffer.writeVarInt(guiData.z)
    }

    override fun readGuiData(player: EntityPlayer, buffer: PacketBuffer): PosGuiData {
        return PosGuiData(player, buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt())
    }
}