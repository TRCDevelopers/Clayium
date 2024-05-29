package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_STRUCTURE_VALIDITY
import com.github.trcdevelopers.clayium.api.capability.impl.MultiblockRecipeLogic
import com.github.trcdevelopers.clayium.api.metatileentity.WorkableMetaTileEntity
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.api.util.RelativeDirection
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import com.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
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
    protected fun getControllerRelativeCoord(pos: BlockPos, right: Int, up: Int, backwards: Int): BlockPos {
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
}