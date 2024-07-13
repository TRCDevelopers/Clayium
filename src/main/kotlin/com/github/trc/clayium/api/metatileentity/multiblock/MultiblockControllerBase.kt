package com.github.trc.clayium.api.metatileentity.multiblock

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_STRUCTURE_VALIDITY
import com.github.trc.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trc.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.RelativeDirection
import com.github.trc.clayium.common.blocks.machine.MachineIoMode
import com.github.trc.clayium.common.recipe.registry.RecipeRegistry
import net.minecraft.client.resources.I18n
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import org.jetbrains.annotations.MustBeInvokedByOverriders
import kotlin.collections.average
import kotlin.collections.forEach
import kotlin.math.floor


abstract class MultiblockControllerBase(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>,
    validOutputModes: List<MachineIoMode>,
    translationKey: String,
    recipeRegistry: RecipeRegistry<*>,
) : WorkableMetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, translationKey, recipeRegistry) {

    protected abstract val faceWhenDeconstructed: ResourceLocation
    protected abstract val faceWhenConstructed: ResourceLocation
    abstract override var faceTexture: ResourceLocation?

    protected val multiblockParts = mutableListOf<IMultiblockPart>()
    var structureFormed = false
        protected set

    abstract fun isConstructed(): Boolean
    abstract override val workable: MultiblockRecipeLogic

    /**
     * only used for recipeLogics, so int is fine
     */
    protected var recipeLogicTier: Int = 0

    open fun onConstructed() {
        writeStructureValidity(true)
    }

    @MustBeInvokedByOverriders
    open fun onDeconstructed() {
        multiblockParts.forEach { it.removeFromMultiblock(this) }
        multiblockParts.clear()
        recipeLogicTier = 0
        writeStructureValidity(false)
    }

    override fun update() {
        super.update()
        if (world?.isRemote == true) return
        if (offsetTimer % 20 == 0L) {
            val constructed = isConstructed()
            if (constructed != structureFormed) {
                if (constructed) {
                    onConstructed()
                } else {
                    onDeconstructed()
                }
                structureFormed = constructed
            }
        }
    }

    protected fun calcTier(tiers: Collection<Int>): Int {
        return floor(tiers.average()).toInt()
    }

    // y
    // ^  z
    // | /
    // |/
    // - - - > x
    protected fun getControllerRelativeCoord(controllerPos: BlockPos, right: Int, up: Int, backwards: Int): BlockPos {
        val frontFacing = this.frontFacing
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
        faceTexture = if (structureFormed) faceWhenConstructed else faceWhenDeconstructed
        scheduleRenderUpdate()
    }

    protected fun writeStructureValidity(valid: Boolean) {
        writeCustomData(UPDATE_STRUCTURE_VALIDITY) { writeBoolean(valid) }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            UPDATE_STRUCTURE_VALIDITY -> {
                val structureFormed = buf.readBoolean()
                this.structureFormed = structureFormed
                faceTexture = if (structureFormed) faceWhenConstructed else faceWhenDeconstructed
                scheduleRenderUpdate()
            }
        }
        super.receiveCustomData(discriminator, buf)
    }

    override fun createBaseUi(syncManager: GuiSyncManager): Column {
        syncManager.syncValue("multiblock_tier", SyncHandlers.intNumber({ recipeLogicTier }, { recipeLogicTier = it }))
        return super.createBaseUi(syncManager)
            .child(IKey.dynamic { I18n.format("tooltip.clayium.tier", recipeLogicTier) }.asWidget()
                    .align(Alignment.BottomCenter))
    }
}