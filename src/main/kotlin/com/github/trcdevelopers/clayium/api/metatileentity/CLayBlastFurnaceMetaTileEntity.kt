package com.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.github.trcdevelopers.clayium.api.CValues
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_STRUCTURE_VALIDITY
import com.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import com.github.trcdevelopers.clayium.api.metatileentity.multiblock.IMultiblockPart
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.CUtils.clayiumId
import com.github.trcdevelopers.clayium.api.util.RelativeDirection
import com.github.trcdevelopers.clayium.common.blocks.BlockMachineHull
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.items.ItemStackHandler

class CLayBlastFurnaceMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: Int,
) : MultiblockControllerBase(
    metaTileEntityId, tier,
    listOf(MachineIoMode.NONE, MachineIoMode.ALL, MachineIoMode.CE), listOf(MachineIoMode.NONE, MachineIoMode.ALL, MachineIoMode.CE),
    "machine.${CValues.MOD_ID}.clay_blast_furnace"
) {

    private val faceWhenDeconstructed = clayiumId("blocks/blastfurnace")
    private val faceWhenConstructed = clayiumId("blocks/blastfurnace_1")
    override val allFaceTextures = listOf(faceWhenDeconstructed, faceWhenConstructed)
    override var faceTexture = faceWhenDeconstructed
        private set

    override val importItems = ItemStackHandler(1)
    override val exportItems = ItemStackHandler(1)
    override val itemInventory = ItemHandlerProxy(importItems, exportItems)
    override val autoIoHandler = AutoIoHandler.Combined(this)

    override fun createMetaTileEntity(): MetaTileEntity {
        return CLayBlastFurnaceMetaTileEntity(metaTileEntityId, tier)
    }

    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("clay_blast_furnace"), "tier=$tier"))
    }

    override fun isConstructed(): Boolean {
        val world = world ?: return false
        val controllerPos = pos ?: return false
        val mbParts = mutableListOf<IMultiblockPart>()
        for (yy in 0..1) {
            for (xx in -1..1) {
                for (zz in 0..2) {
                    val mbPartPos = getControllerRelativeCoord(controllerPos, xx, yy, zz)
                    val (isValid, mbPart) = isPosValidForMultiblock(world, mbPartPos)
                    if (!isValid) {
                        writeCustomData(UPDATE_STRUCTURE_VALIDITY) { writeBoolean(false) }
                        return false
                    }
                    mbPart?.let { mbParts.add(it) }
                }
            }
        }
        multiblockParts.addAll(mbParts)
        if (!structureFormed) {
            writeCustomData(UPDATE_STRUCTURE_VALIDITY) { writeBoolean(true) }
        }
        return true
    }

    private fun isPosValidForMultiblock(world: IBlockAccess, pos: BlockPos): Pair<Boolean, IMultiblockPart?> {
        if (CUtils.getMetaTileEntity(world, pos) == this) return Pair(true, null)

        world.getTileEntity(pos)?.let { tileEntity ->
            if (tileEntity is IMultiblockPart) {
                multiblockParts.add(tileEntity)
                return Pair(true, tileEntity)
            }
        }

        return Pair((world.getBlockState(pos).block is BlockMachineHull), null)
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("notImplementedBlastFurnace")
            .child(IKey.str("isConstructed: $structureFormed").asWidget()
                .left(6).top(6))
            .bindPlayerInventory()
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

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_STRUCTURE_VALIDITY -> {
                val structureFormed = buf.readBoolean()
                faceTexture = if (structureFormed) faceWhenConstructed else faceWhenDeconstructed
                this.structureFormed = structureFormed
                scheduleRenderUpdate()
            }
        }
        super.receiveCustomData(discriminator, buf)
    }
}