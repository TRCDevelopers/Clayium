package com.github.trc.clayium.api.metatileentity

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.MachineIoMode
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.client.model.ModelTextures
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer
import com.github.trc.clayium.client.renderer.AreaMarkerRenderer.RangeRenderMode
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.FaceBakery
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.function.Function

abstract class AbstractBuilderMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    name: String,
    validInputModes: List<MachineIoMode> = validInputModesLists[0],
    validOutputModes: List<MachineIoMode> = validOutputModesLists[1],
    renderMinerBack: Boolean = true,
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModes, name) {
    override val itemInventory = ClayiumItemStackHandler(this, 3 * 3)
    override val importItems = EmptyItemStackHandler
    override val exportItems = itemInventory

    protected var workingEnabled = true
    protected var repeatEnabled = true

    protected var progress = 0.0

    var rangeRenderMode = RangeRenderMode.DISABLED

    protected var currentPos: BlockPos? = null

    /**
     * Called every tick. [accelerationRate] is obtained from [getAccelerationRate] method.
     */
    abstract fun drawEnergy(accelerationRate: Double): Boolean

    /**
     * Returns the next block position to work on.
     * Maybe called multiple times per tick.
     */
    abstract fun getNextBlockPos(): BlockPos?

    /**
     * used for rendering.
     * null for disable range rendering.
     */
    abstract val rangeRelative: Cuboid6?

    abstract val maxBlocksPerTick: Int
    open val maxSearchBlockPerTick = ConfigCore.misc.builderMaxSearchBlocksPerTick

    /**
     * [progress] is multiplied by this per tick.
     * Default: always 1.0
     */
    protected open fun getAccelerationRate(): Double = 1.0

    /**
     * [progress] is required to work on given block.
     * Default: always 400.0
     */
    protected open fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double = 400.0

    override fun update() {
        super.update()

        if (isRemote || !workingEnabled) return
        val world = world ?: return
        val r = getAccelerationRate()
        if (!drawEnergy(r)) return
        progress += AbstractMinerMetaTileEntity.Companion.PROGRESS_PER_TICK_BASE * getAccelerationRate()

        var remainingBlocks = maxBlocksPerTick
        for (i in 0..<maxSearchBlockPerTick) {
            if (remainingBlocks <= 0) break
            val pos = getNextBlockPos()
            if (pos == null) return

            val state = world.getBlockState(pos)
            val requiredProgress = getRequiredProgress(state, world, pos)
            if (progress < requiredProgress) return
            val result = this.actionOnBlock(state, world, pos)
            when (result) {
                EnumActionResult.SUCCESS -> {
                    progress -= requiredProgress
                    remainingBlocks--
                }
                EnumActionResult.PASS -> {}
                EnumActionResult.FAIL -> break
            }
        }
    }

    /**
     * NOTE: if all [maxBlocksPerTick] blocks are mined, [progress] will be reset.
     *
     * @return EnumActionResult.
     * - [EnumActionResult.SUCCESS] if the block is successfully processed.
     * [maxBlocksPerTick] is consumed (subtracted by 1). Then it will search for the next block.
     * - [EnumActionResult.PASS] if the block is air or something like that.
     * [maxBlocksPerTick] is not consumed, but then it will search for the next block.
     * - [EnumActionResult.FAIL] if the work should be stopped. It will skip further working on this tick.
     */
    protected abstract fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult

    override fun onPlacement() {
        if (this.frontFacing.axis.isHorizontal) {
            this.setOutput(this.frontFacing.rotateY(), MachineIoMode.ALL)
            this.setOutput(this.frontFacing.rotateYCCW(), MachineIoMode.ALL)
        } else {
            this.setOutput(EnumFacing.NORTH, MachineIoMode.ALL)
            this.setOutput(EnumFacing.SOUTH, MachineIoMode.ALL)
        }
        super.onPlacement()
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setBoolean("workingEnabled", workingEnabled)
        data.setBoolean("repeatEnabled", repeatEnabled)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        workingEnabled = data.getBoolean("workingEnabled")
        repeatEnabled = data.getBoolean("repeatEnabled")
    }

    @SideOnly(Side.CLIENT)
    override fun bakeQuads(getter: Function<ResourceLocation, TextureAtlasSprite>, faceBakery: FaceBakery) {
        val atlas = getter.apply(clayiumId("blocks/miner_back"))
        MINER_BACK = EnumFacing.entries.map { ModelTextures.createQuad(it, atlas) }
    }

    @SideOnly(Side.CLIENT)
    override fun renderMetaTileEntity(x: Double, y: Double, z: Double, partialTicks: Float) {
        AreaMarkerRenderer.render(Cuboid6.full, rangeRelative, x, y, z, rangeRenderMode)
    }

    @SideOnly(Side.CLIENT)
    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        super.overlayQuads(quads, state, side, rand)
        if (state == null || side == null || state !is IExtendedBlockState) return
        if (side == this.frontFacing.opposite) {
            quads.add(MINER_BACK[side.index])
        }
    }

    companion object {
        const val PROGRESS_PER_TICK_BASE = 100

        @JvmStatic // for protected visibility
        protected lateinit var MINER_BACK: List<BakedQuad>
    }
}