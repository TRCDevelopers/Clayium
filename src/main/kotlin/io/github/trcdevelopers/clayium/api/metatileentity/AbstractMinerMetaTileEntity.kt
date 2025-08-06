package io.github.trcdevelopers.clayium.api.metatileentity

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import io.github.trcdevelopers.clayium.api.HARDNESS_UNBREAKABLE
import io.github.trcdevelopers.clayium.api.LaserEnergy
import io.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.IClayLaserAcceptor
import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.gui.sync.ClayLaserSyncValue
import io.github.trcdevelopers.clayium.api.laser.ClayLaser
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.asWidgetResizing
import io.github.trcdevelopers.clayium.api.util.getCapability
import io.github.trcdevelopers.clayium.api.util.hasCapability
import io.github.trcdevelopers.clayium.common.gui.ClayGuiTextures
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import kotlin.math.log10

abstract class AbstractMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    name: String,
    validInputModes: List<MachineIoMode> = validInputModesLists[0],
    validOutputModes: List<MachineIoMode> = validOutputModesLists[1],
    renderMinerBack: Boolean,
) : AbstractBuilderMetaTileEntity(metaTileEntityId, tier, name, validInputModes, validOutputModes, renderMinerBack), IClayLaserAcceptor {

    protected val filterSlot = ClayiumItemStackHandler(this, 1)
    protected val filter: IItemFilter?
        get() = filterSlot.getStackInSlot(0).getCapability(ClayiumCapabilities.ITEM_FILTER)

    protected var laser: ClayLaser? = null

    /**
     * return true if you want to continue mining within the tick.
     * if false, further blocks will not be mined in this tick.
     * also, if all [maxBlocksPerTick] blocks are mined, [progress] will be reset.
     */
    override fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        val filter = this.filter
        val filterMatches = filter == null || filter.testBlock(world, pos)

        if (!filterMatches) return EnumActionResult.PASS
        val blockHardness = state.getBlockHardness(world, pos)
        if (blockHardness == HARDNESS_UNBREAKABLE) return EnumActionResult.PASS
        return this.mine(state, world, pos)
    }

    protected open fun mine(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        val drops = NonNullList.create<ItemStack>()
        state.block.getDrops(drops, world, pos, state, 0)
        if (!TransferUtils.insertToHandler(itemInventory, drops, true)) return EnumActionResult.FAIL
        TransferUtils.insertToHandler(itemInventory, drops, false)
        world.destroyBlock(pos, false)
        return EnumActionResult.SUCCESS
    }

    override fun getAccelerationRate(): Double {
        // actual $$r = 1 + 4 * log10(energy / 1000 + 1)$$
        val energy = laser?.energy ?: return 1.0
        return 1 + 4 * log10(energy / 1000 + 1)
    }

    override fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos): Double {
        val hardness = if (state.material.isLiquid) 1f else state.getBlockHardness(world, pos)
        return REQUIRED_PROGRESS_BASE * (0.1 + hardness)
    }

    override fun acceptLaser(irradiatedSide: EnumFacing, laser: ClayLaser?) {
        this.laser = laser
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        syncManager.syncValue("clay_laser", ClayLaserSyncValue(::laser, ::laser::set))

        return super.buildMainParentWidget(syncManager)
            .child(IKey.dynamic { "Laser : ${laser?.let { LaserEnergy(it.energy).format() } ?: 0}" }.asWidgetResizing()
                .alignX(Alignment.Center.x).bottom(12)
            )
            .child(MuiSlots.phantomSlotBuilder(filterSlot, 0).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) }.build()
                .background(ClayGuiTextures.FILTER_SLOT)
                .top(12).right(24)
                .tooltipBuilder { it.addLine(IKey.lang("gui.clayium.miner.filter")) }
            )
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setTag("filterSlot", filterSlot.serializeNBT())
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        filterSlot.deserializeNBT(data.getCompoundTag("filterSlot"))
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR) {
            return capability.cast(this)
        }
        return super.getCapability(capability, facing)
    }

    companion object {
        const val REQUIRED_PROGRESS_BASE = 400
    }
}