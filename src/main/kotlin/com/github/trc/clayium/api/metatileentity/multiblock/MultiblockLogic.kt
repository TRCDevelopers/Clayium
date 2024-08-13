package com.github.trc.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.TextWidget
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_STRUCTURE_VALIDITY
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.RelativeDirection
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.blocks.BlockMachineHull
import net.minecraft.client.resources.I18n
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class MultiblockLogic(
    metaTileEntity: MetaTileEntity,
    private val checkStructure: (MultiblockLogic) -> StructureValidationResult,
) : MTETrait(metaTileEntity, "${CValues.MOD_ID}.structure_validator") {

    var structureFormed = false
        private set

    var recipeLogicTier = 0
        private set

    private val multiblockParts = mutableListOf<IMultiblockPart>()

    override fun update() {
        if (metaTileEntity.isRemote || metaTileEntity.offsetTimer % 20 != 0L) return
        val result = checkStructure(this)
        when (result) {
            StructureValidationResult.Invalid -> {
                if (structureFormed) {
                    structureFormed = false
                    multiblockParts.forEach { it.removeFromMultiblock(metaTileEntity) }
                    multiblockParts.clear()
                    recipeLogicTier = 0
                    writeStructureValidity(false)
                }
            }
            is StructureValidationResult.Valid -> {
                if (!structureFormed) {
                    // Check for MultiblockParts part sharing
                    if (result.parts.any { it.isAttachedToMultiblock && !it.canPartShare() }) {
                        return // one of the parts is already attached and can't part share
                    }
                    structureFormed = true
                    this.multiblockParts.addAll(result.parts)
                    this.multiblockParts.forEach { it.addToMultiblock(metaTileEntity) }
                    val tierNums = listOf(result.tiers.map { it.numeric }, result.parts.map { it.tier.numeric }).flatten()
                    this.recipeLogicTier = if (tierNums.isEmpty()) 0 else tierNums.average().toInt()
                    writeStructureValidity(true)
                }
            }
        }
    }

    override fun onRemoval() {
        this.multiblockParts.forEach { it.removeFromMultiblock(metaTileEntity) }
    }

    fun isPosValidForMutliblock(world: IBlockAccess, pos: BlockPos): BlockValidationResult {
        val metaTileEntity = world.getMetaTileEntity(pos)
        if (metaTileEntity == this.metaTileEntity) return BlockValidationResult.Matched(null)

        if (metaTileEntity is IMultiblockPart) return BlockValidationResult.MultiblockPart(metaTileEntity)
        val block = world.getBlockState(pos).block
        if (block is BlockMachineHull) return BlockValidationResult.Matched(block.getTier(world, pos))

        return BlockValidationResult.Invalid
    }

    fun getControllerRelativeCoord(controllerPos: BlockPos, right: Int, up: Int, backwards: Int): BlockPos {
        val frontFacing = metaTileEntity.frontFacing
        val relRight = RelativeDirection.RIGHT.getActualFacing(frontFacing)
        val relUp = RelativeDirection.UP.getActualFacing(frontFacing)
        val relBackwards = RelativeDirection.BACK.getActualFacing(frontFacing)
        return BlockPos(
            controllerPos.x + relRight.xOffset * right + relUp.xOffset * up + relBackwards.xOffset * backwards,
            controllerPos.y + relRight.yOffset * right + relUp.yOffset * up + relBackwards.yOffset * backwards,
            controllerPos.z + relRight.zOffset * right + relUp.zOffset * up + relBackwards.zOffset * backwards,
        )
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        buf.writeBoolean(structureFormed)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        super.receiveInitialSyncData(buf)
        structureFormed = buf.readBoolean()
        metaTileEntity.scheduleRenderUpdate()
    }

    private fun writeStructureValidity(valid: Boolean) {
        writeCustomData(UPDATE_STRUCTURE_VALIDITY) { writeBoolean(valid) }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_STRUCTURE_VALIDITY -> {
                val structureFormed = buf.readBoolean()
                this.structureFormed = structureFormed
                metaTileEntity.scheduleRenderUpdate()
            }
        }
        super.receiveCustomData(discriminator, buf)
    }

    fun tierTextWidget(syncManager: GuiSyncManager): TextWidget {
        syncManager.syncValue("multiblock_tier", SyncHandlers.intNumber({ recipeLogicTier }, { recipeLogicTier = it }))
        return IKey.dynamic { I18n.format("tooltip.clayium.tier", recipeLogicTier) }.asWidget()
    }

    sealed interface BlockValidationResult {
        object Invalid : BlockValidationResult
        data class Matched(val tier: ITier?) : BlockValidationResult
        data class MultiblockPart(val part: IMultiblockPart) : BlockValidationResult
    }

    sealed interface StructureValidationResult {
        object Invalid : StructureValidationResult
        data class Valid(val parts: Collection<IMultiblockPart>, val tiers: Collection<ITier>) : StructureValidationResult
    }
}