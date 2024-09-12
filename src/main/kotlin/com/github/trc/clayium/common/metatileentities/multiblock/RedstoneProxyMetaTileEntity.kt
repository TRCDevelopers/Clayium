package com.github.trc.clayium.common.metatileentities.multiblock

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.screen.ModularPanel
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.IntSyncValue
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.CycleButtonWidget
import com.github.trc.clayium.api.GUI_DEFAULT_HEIGHT
import com.github.trc.clayium.api.GUI_DEFAULT_WIDTH
import com.github.trc.clayium.api.MOD_ID
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.gui.data.MetaTileEntityGuiData
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.multiblock.ProxyMetaTileEntityBase
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.client.resources.I18n
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

class RedstoneProxyMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
) : ProxyMetaTileEntityBase(metaTileEntityId, tier, "redstone_proxy") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/redstone_proxy")
    override val useFaceForAllSides: Boolean = true

    private var mode = Mode.NONE
        set(value) {
            val notifyFlag = field != value
            field = value
            if (notifyFlag) {
                notifyNeighbors()
                markDirty()
            }
        }
    private var power: Int = 0
        set(value) {
            val notifyFlag = field != value
            field = value
            if (notifyFlag) { notifyNeighbors() }
        }

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
                power = 0
                val pos = pos ?: return
                controllable.isWorkingEnabled = world.isBlockPowered(pos)
            }
            Mode.DO_WORK_IF_NOT_POWERED -> {
                power = 0
                val pos = pos ?: return
                controllable.isWorkingEnabled = !world.isBlockPowered(pos)
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

    override fun buildUI(data: MetaTileEntityGuiData, syncManager: GuiSyncManager): ModularPanel {
        return ModularPanel.defaultPanel("redstone_proxy.$tier", GUI_DEFAULT_WIDTH, GUI_DEFAULT_HEIGHT - 20)
            .columnWithPlayerInv { child(buildMainParentWidget(syncManager)) }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(CycleButtonWidget()
                .align(Alignment.Center).widthRel(0.7f).height(24)
                .length(Mode.entries.size)
                .value(IntSyncValue({ mode.ordinal }, { mode = Mode.entries[it] }))
                .overlay(IKey.dynamic { I18n.format(mode.translationKey) })
            )
    }

    override fun canOpenGui(): Boolean {
        return true
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setInteger("rs_mode", mode.ordinal)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        mode = Mode.entries[data.getInteger("rs_mode")]
    }

    private enum class Mode(
        val translationKey: String,
    ) {
        NONE("gui.$MOD_ID.redstone_proxy.none"),
        EMIT_IF_IDLE("gui.$MOD_ID.redstone_proxy.emit_if_idle"),
        EMIT_IF_WORKING("gui.$MOD_ID.redstone_proxy.emit_if_working"),
        DO_WORK_IF_POWERED("gui.$MOD_ID.redstone_proxy.do_work_if_powered"),
        DO_WORK_IF_NOT_POWERED("gui.$MOD_ID.redstone_proxy.do_work_if_not_powered"),
        ;
    }
}