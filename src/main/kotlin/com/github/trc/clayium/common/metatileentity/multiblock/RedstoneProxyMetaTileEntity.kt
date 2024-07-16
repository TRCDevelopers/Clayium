package com.github.trc.clayium.common.metatileentity.multiblock

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.factory.PosGuiData
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.IntSyncValue
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.cleanroommc.modularui.widgets.layout.Column
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.ProxyMetaTileEntityBase
import com.github.trc.clayium.api.util.CUtils.clayiumId
import com.github.trc.clayium.api.util.ITier
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class RedstoneProxyMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, "machine.${CValues.MOD_ID}.redstone_proxy") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/redstoneinterface")
    override val useFaceForAllSides: Boolean = true

    private var mode = Mode.NONE
    private var power: Int = 0

    override fun createMetaTileEntity(): MetaTileEntity {
        return RedstoneProxyMetaTileEntity(metaTileEntityId, tier)
    }

    override fun update() {
        super.update()
        val world = world ?: return
        if (world.isRemote) return
        val controllable = target?.getCapability(ClayiumTileCapabilities.CONTROLLABLE, null) ?: return
        when (this.mode) {
            Mode.NONE -> {}
            Mode.EMIT_IF_IDLE -> power = if (controllable.isWorking) 0 else 15
            Mode.EMIT_IF_WORKING -> power = if (controllable.isWorking) 15 else 0
            Mode.DO_WORK_IF_POWERED -> {
                val pos = pos
                controllable.isWorkingEnabled = pos != null && world.isBlockPowered(pos)
            }
            Mode.DO_WORK_IF_NOT_POWERED -> {
                val pos = pos
                controllable.isWorkingEnabled = !(pos != null && world.isBlockPowered(pos))
            }
        }
    }

    override fun canConnectRedstone(side: EnumFacing?): Boolean {
        return true
    }

    override fun getWeakPower(side: EnumFacing?): Int {
        return this.power
    }

    override fun canLink(target: MetaTileEntity): Boolean {
        return super.canLink(target) && target.getCapability(ClayiumTileCapabilities.CONTROLLABLE, null) != null
    }

    override fun buildUI(data: PosGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("clay_laser_tier$tier", 176, 32 + 94)
            .child(Column().size(172, 32)
                .align(Alignment.TopCenter)
                .child(CycleButtonWidget()
                    .align(Alignment.Center).widthRel(0.5f).height(20)
                    .length(5)
                    .value(IntSyncValue({ mode.ordinal }, { mode = Mode.entries[it] }))
                    .overlay(IKey.dynamic { I18n.format(mode.translationKey) })
                    .addTooltip(0, "None")
                    .addTooltip(1, "Emit if idle")
                    .addTooltip(2, "Emit if working")
                    .addTooltip(3, "Do work if powered")
                    .addTooltip(4, "Do work if not powered")
                )
            )
            .bindPlayerInventory()
    }

    @SideOnly(Side.CLIENT)
    override fun registerItemModel(item: Item, meta: Int) {
        ModelLoader.setCustomModelResourceLocation(item, meta, ModelResourceLocation(clayiumId("redstone_proxy"), "tier=${tier.lowerName}"))
    }

    override fun canOpenGui(): Boolean {
        return true
    }

    private enum class Mode(
        val translationKey: String,
    ) {
        NONE("gui.${CValues.MOD_ID}.redstone_proxy.none"),
        EMIT_IF_IDLE("gui.${CValues.MOD_ID}.redstone_proxy.emit_if_idle"),
        EMIT_IF_WORKING("gui.${CValues.MOD_ID}.redstone_proxy.emit_if_working"),
        DO_WORK_IF_POWERED("gui.${CValues.MOD_ID}.redstone_proxy.do_work_if_powered"),
        DO_WORK_IF_NOT_POWERED("gui.${CValues.MOD_ID}.redstone_proxy.do_work_if_not_powered"),
        ;
    }
}