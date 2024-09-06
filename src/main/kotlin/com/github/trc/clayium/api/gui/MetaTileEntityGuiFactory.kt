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
import net.minecraftforge.common.DimensionManager

object MetaTileEntityGuiFactory : AbstractUIFactory<PosGuiData>("${CValues.MOD_ID}:metatileentity") {

    @JvmOverloads
    fun open(player: EntityPlayer, pos: BlockPos, world: World = player.world) {
        val data = WorldPosGuiData(player, pos, world)
        GuiManager.open(this, data, player as EntityPlayerMP)
    }

    override fun getGuiHolder(data: PosGuiData): IGuiHolder<PosGuiData> {
        return (data.tileEntity as? MetaTileEntityHolder)?.metaTileEntity as? IGuiHolder<PosGuiData>
            ?: throw NullPointerException("Found MetaTileEntity is not a gui holder!")
    }

    override fun writeGuiData(guiData: PosGuiData, buffer: PacketBuffer) {
        val data = guiData as WorldPosGuiData
        buffer.writeVarInt(data.x)
        buffer.writeVarInt(data.y)
        buffer.writeVarInt(data.z)
        buffer.writeVarInt(data.world.provider.dimension)
    }

    override fun readGuiData(player: EntityPlayer, buffer: PacketBuffer): PosGuiData {
        val x = buffer.readVarInt()
        val y = buffer.readVarInt()
        val z = buffer.readVarInt()
        val world = DimensionManager.getWorld(buffer.readVarInt())
        return WorldPosGuiData(player, BlockPos(x, y, z), world)
    }
}