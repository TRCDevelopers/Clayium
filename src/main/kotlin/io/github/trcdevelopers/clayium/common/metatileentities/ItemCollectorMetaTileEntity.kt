package io.github.trcdevelopers.clayium.common.metatileentities

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.trait.ClayMarkerHandler
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.api.util.containsEq
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.ItemHandlerHelper

class ItemCollectorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "item_collector", validInputModesLists[1], validOutputModesLists[1],
    renderMinerBack = false) {

    override fun onPlacement() {
        this.setInput(EnumFacing.UP, MachineIoMode.CE)
        super.onPlacement()
    }

    override fun drawEnergy(accelerationRate: Double): Boolean {
        return clayEnergyHolder.drawEnergy(ClayEnergy.milli(1), false)
    }

    override fun getRequiredProgress(state: IBlockState, world: World, pos: BlockPos) = 400.0

    // This method is meaningless for this machine.
    override fun getNextBlockPos(): BlockPos? { return this.pos }

    val clayEnergyHolder = ClayEnergyHolder(this)
    val clayMarkerHandler = ClayMarkerHandler(this)

    override val rangeRelativeClient get() = clayMarkerHandler.renderingRangeRelative
    override val maxBlocksPerTick: Int = ConfigCore.misc.rangedMachineMaxBlocksPerTick

    private val scannedItemEntities = mutableListOf<EntityItem>()

    override fun actionOnBlock(state: IBlockState, world: World, pos: BlockPos): EnumActionResult {
        val aabb = clayMarkerHandler.markedRangeAbsoluteAabb
        if (aabb == null) return EnumActionResult.FAIL
        if (scannedItemEntities.isEmpty()) {
            val entities = world.getEntitiesWithinAABB(EntityItem::class.java, aabb)
            if (entities.isEmpty()) return EnumActionResult.FAIL // skip to next tick
            this.scannedItemEntities.addAll(entities)
        }
        for (entityItem in this.scannedItemEntities) {
            if (entityItem.isDead || !aabb.containsEq(entityItem.positionVector)) continue
            val itemStack = entityItem.item.copy()
            val pickupStack = itemStack.copyWithSize(1)
            val filter = this.filter
            val filterPass = filter == null || filter.test(pickupStack)
            if (!filterPass) continue
            val remain = ItemHandlerHelper.insertItemStacked(itemInventory, pickupStack, true)
            val canInsert = remain.isEmpty
            if (!canInsert) continue

            ItemHandlerHelper.insertItemStacked(itemInventory, pickupStack, false)
            itemStack.shrink(1)
            if (itemStack.isEmpty) {
                entityItem.setDead()
            } else {
                entityItem.item = itemStack
            }
            break
        }
        this.scannedItemEntities.removeAll { it.isDead || !aabb.contains(it.positionVector) }
        return EnumActionResult.SUCCESS
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/item_collector"))
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ItemCollectorMetaTileEntity(metaTileEntityId, tier)
    }
}