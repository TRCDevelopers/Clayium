package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trcdevelopers.clayium.api.util.RelativeDirection
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineHull
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.items.ItemStackHandler

class CLayBlastFurnaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: Int,
) : MultiblockControllerBase(
    metaTileEntityId, tier,
    listOf(MachineIoMode.NONE, MachineIoMode.ALL, MachineIoMode.CE), listOf(MachineIoMode.NONE, MachineIoMode.ALL, MachineIoMode.CE),
    "machine.${CValues.MOD_ID}.clay_blast_furnace"
) {

    override val importItems = ItemStackHandler(1)
    override val exportItems = ItemStackHandler(1)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    override val autoIoHandler = AutoIoHandler.Combined(this)

    override fun createMetaTileEntity(): MetaTileEntity {
        return CLayBlastFurnaceMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        TODO("Not yet implemented")
    }

    override fun isConstructed(): Boolean {
        val world = world ?: return false
        val pos = pos ?: return false
        val mbParts = mutableListOf<IMultiblockPart>()
        for (yy in 0..1) {
            for (xx in -1..1) {
                for (zz in 0..2) {
                    val blockPos = getControllerRelativeCoord(pos, xx, yy, zz)
                    val (isValid, mbPart) = isPosValidForMultiblock(world, blockPos)
                    if (!isValid) return false
                    mbPart?.let { mbParts.add(it) }
                }
            }
        }
        multiblockParts.addAll(mbParts)
        return true
    }

    private fun isPosValidForMultiblock(world: IBlockAccess, pos: BlockPos): Pair<Boolean, IMultiblockPart?> {
        world.getTileEntity(pos)?.let { tileEntity ->
            if (tileEntity is IMultiblockPart) {
                multiblockParts.add(tileEntity)
                return Pair(true, tileEntity)
            }
        }

        return Pair((world.getBlockState(pos).block is BlockMachineHull), null)
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        TODO("Not yet implemented")
    }

    // y
    // ^  z
    // | /
    // |/
    // - - - > x
    private fun getControllerRelativeCoord(pos: BlockPos, right: Int, up: Int, backwards: Int): BlockPos {
        val frontFacing = this.frontFacing
        val relRight = RelativeDirection.RIGHT.getActualFacing(frontFacing)
        val relUp = RelativeDirection.UP.getActualFacing(frontFacing)
        val relBackwards = RelativeDirection.BACK.getActualFacing(frontFacing)
        return BlockPos(
            pos.x + relRight.xOffset * right + relUp.xOffset * up + relBackwards.xOffset * backwards,
            pos.y + relRight.yOffset * right + relUp.yOffset * up + relBackwards.yOffset * backwards,
            pos.z + relRight.zOffset * right + relUp.zOffset * up + relBackwards.zOffset * backwards,
        )
    }
}