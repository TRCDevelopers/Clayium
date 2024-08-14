package com.github.trc.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.SlotGroupWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.block.BlockLiquid
import net.minecraft.client.resources.I18n
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.pow

class WaterwheelMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : MetaTileEntity(
    metaTileEntityId, tier, onlyNoneList, onlyNoneList,
    "machine.${CValues.MOD_ID}.waterwheel",
) {
    override val faceTexture = clayiumId("blocks/waterwheel")
    override val importItems = EmptyItemStackHandler
    override val exportItems = EmptyItemStackHandler
    override val itemInventory = EmptyItemStackHandler

    val clayEnergyPerWork = ClayEnergy(this.tier.numeric.toDouble().pow(8).toLong())
    private val maxClayEnergy = clayEnergyPerWork * 5
    private val progressPerTick = (1000 * this.tier.numeric.toDouble().pow(3)).toInt()

    private var waterCount = 0
    private var progress = 0

    override fun createMetaTileEntity(): MetaTileEntity {
        return WaterwheelMetaTileEntity(metaTileEntityId, tier)
    }

    override fun update() {
        super.update()
        if (isRemote) return

        waterCount = getWaterFlowsCount()
        val world = world ?: return
        if (world.rand.nextInt(ConfigCore.misc.waterwheelEfficiency) < waterCount) {
            progress += progressPerTick
        }
        if (this.progress >= MAX_PROGRESS) {
            this.progress = 0
            emitEnergy()
        }
    }

    private fun emitEnergy() {
        val pos = pos ?: return
        for (side in EnumFacing.entries) {
            val energyHolder = world?.getTileEntity(pos.offset(side))?.getCapability(ClayiumTileCapabilities.CLAY_ENERGY_HOLDER, side.opposite)
                ?: continue

            val energyStored = energyHolder.getEnergyStored()
            if (energyStored < maxClayEnergy) {
                energyHolder.addEnergy(clayEnergyPerWork)
            }
        }
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        syncManager.syncValue("waterCount", SyncHandlers.intNumber({ waterCount }, { waterCount = it }))
        syncManager.syncValue("progress", SyncHandlers.intNumber({ progress }, { progress = it }))
        return ModularPanel.defaultPanel("waterwheel", 176, 126)
            .child(Column().margin(7)
                .child(ParentWidget().widthRel(1f).expanded().marginBottom(2)
                    .child(IKey.lang(this.translationKey, IKey.lang(tier.prefixTranslationKey)).asWidget()
                        .align(Alignment.TopLeft))
                    .child(IKey.lang("container.inventory").asWidget()
                        .align(Alignment.BottomLeft))
                    .child(IKey.dynamic { I18n.format("gui.clayium.waterwheel.waters", waterCount) }.asWidget()
                        .widthRel(0.3f).align(Alignment.CenterRight))
                    .child(IKey.dynamic { I18n.format("gui.clayium.waterwheel.progress", progress) }.asWidget()
                        .widthRel(0.6f).align(Alignment.CenterLeft)))
                .child(SlotGroupWidget.playerInventory(0)))
    }

    private fun getWaterFlowsCount(): Int {
        val world = world ?: return 0
        val pos = pos ?: return 0
        var waterFlows = 0
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    val state = world.getBlockState(pos.add(dx, dy, dz))
                    val block = state.block
                    if ((block === Blocks.WATER || block === Blocks.FLOWING_WATER) && state.getValue(BlockLiquid.LEVEL) != 0) {
                        waterFlows++
                    }
                }
            }
        }
        return waterFlows
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        registerItemModelDefault(item, meta, "waterwheel")
    }

    companion object {
        private const val MAX_PROGRESS = 20_000
    }
}