package com.github.trc.clayium.api.gui

import com.cleanroommc.modularui.api.IGuiHolder
import com.cleanroommc.modularui.factory.AbstractUIFactory
import com.cleanroommc.modularui.factory.GuiManager
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.gui.data.WorldPosGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager

object MetaTileEntityGuiFactory : AbstractUIFactory<WorldPosGuiData>("${CValues.MOD_ID}:metatileentity") {

    fun <T> open(player: EntityPlayer, metaTileEntity: T) where T: MetaTileEntity {
        val data = WorldPosGuiData(player, metaTileEntity.pos ?: return, metaTileEntity.world ?: return)
        GuiManager.open(this, data, player as EntityPlayerMP)
    }

    @JvmOverloads
    fun open(player: EntityPlayer, pos: BlockPos, world: World = player.world) {
        val data = WorldPosGuiData(player, pos, world)
        GuiManager.open(this, data, player as EntityPlayerMP)
    }

    override fun getGuiHolder(data: WorldPosGuiData): IGuiHolder<WorldPosGuiData> {
        return (data.tileEntity as? MetaTileEntityHolder)?.metaTileEntity as? IGuiHolder<WorldPosGuiData>
            ?: throw NullPointerException("Found MetaTileEntity is not a gui holder!")
    }

    override fun writeGuiData(guiData: WorldPosGuiData, buffer: PacketBuffer) {
        buffer.writeVarInt(guiData.x)
        buffer.writeVarInt(guiData.y)
        buffer.writeVarInt(guiData.z)
        buffer.writeVarInt(guiData.world.provider.dimension)
    }

    override fun readGuiData(player: EntityPlayer, buffer: PacketBuffer): WorldPosGuiData {
        val x = buffer.readVarInt()
        val y = buffer.readVarInt()
        val z = buffer.readVarInt()
        val world = DimensionManager.getWorld(buffer.readVarInt())
        return WorldPosGuiData(player, BlockPos(x, y, z), world)
    }
}