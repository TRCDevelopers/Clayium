package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.ItemSlot
import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.api.util.getCapability
import com.github.trc.clayium.api.util.hasCapability
import com.github.trc.clayium.api.util.toItemStack
import com.github.trc.clayium.common.gui.ClayGuiTextures
import com.github.trc.clayium.common.reflect.BlockReflect
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayerFactory

class AdvancedRangedMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : RangedMinerMetaTileEntity(metaTileEntityId, tier, "advanced_ranged_miner") {
    override val faceTexture: ResourceLocation = clayiumId("blocks/adv_miner")

    private val extraFilters = ClayiumItemStackHandler(this, 2)
    private val fortuneFilter get() = extraFilters.getStackInSlot(0).getCapability(ClayiumCapabilities.ITEM_FILTER)
    private val silkTouchFilter get() = extraFilters.getStackInSlot(1).getCapability(ClayiumCapabilities.ITEM_FILTER)

    override fun mine(world: World, pos: BlockPos, state: IBlockState): Boolean {
        val silkFilter = silkTouchFilter
        val fortuneFilter = fortuneFilter
        val drops = NonNullList.create<ItemStack>()
        if (silkFilter != null && silkFilter.test(state.toItemStack())
            && world is WorldServer
            && state.block.canSilkHarvest(world, pos, state, FakePlayerFactory.getMinecraft(world)))
        {
            drops.add(BlockReflect.getSilkTouchDrop(state.block, state))
        } else {
            val fortune = if (fortuneFilter != null && fortuneFilter.test(state.toItemStack())) 3 else 0
            state.block.getDrops(drops, world, pos, state, fortune)
        }
        if (!TransferUtils.insertToHandler(itemInventory, drops, true)) return false
        TransferUtils.insertToHandler(itemInventory, drops, false)
        world.destroyBlock(pos, false)
        return true
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(extraFilters, 0).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) })
                .background(ClayGuiTextures.FILTER_SLOT)
                .top(12 + 18 + 2).right(24)
                .tooltipBuilder { it.addLine(IKey.lang("enchantment.lootBonusDigger")) }
            )
            .child(ItemSlot().slot(SyncHandlers.phantomItemSlot(extraFilters, 1).filter { it.hasCapability(ClayiumCapabilities.ITEM_FILTER) })
                .background(ClayGuiTextures.FILTER_SLOT)
                .top(12 + 18 * 2 + 2 * 2).right(24)
                .tooltipBuilder { it.addLine(IKey.lang("enchantment.untouching")) }
            )
    }

    override fun createMetaTileEntity() = AdvancedRangedMinerMetaTileEntity(metaTileEntityId, tier)
}