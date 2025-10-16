package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.TextWidget
import com.cleanroommc.modularui.widgets.layout.Row
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.recipe.builder.ClayFabricatorRecipeBuilder
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class SolarClayFabricatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    val registry: RecipeRegistry<ClayFabricatorRecipeBuilder>
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModesLists[1], "solar_clay_fabricator") {

    override val importItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, false)
    override val exportItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, true)
    override val itemInventory: IItemHandler = ItemHandlerProxy(importItems, exportItems)
    val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    private val workable = SolarClayFabricatorRecipeLogic()

    override fun createMetaTileEntity(): MetaTileEntity {
        return SolarClayFabricatorMetaTileEntity(metaTileEntityId, tier, registry)
    }

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return facing == EnumFacing.UP
    }

    override fun onPlacement() {
        this.frontFacing = EnumFacing.UP
        super.onPlacement()
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(Row().widthRel(0.7f).height(26).align(Alignment.Center)
                .child(MuiSlots.itemSlotBuilder(importItems, 0).singletonSlotGroup(2).buildLarge()
                    .align(Alignment.CenterLeft))
                .child(workable.getProgressBar(syncManager).align(Alignment.Center))
                .child(MuiSlots.itemSlotBuilder(exportItems, 0).singletonSlotGroup(0).takeOnly().buildLarge()
                    .align(Alignment.CenterRight))
            )
            .child(workable.createCeTextWidget(syncManager)
                .bottom(12).left(0).widthRel(0.5f))
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/solar"))
    }

    private inner class SolarClayFabricatorRecipeLogic : AbstractRecipeLogic(this@SolarClayFabricatorMetaTileEntity, registry) {
        private var clayEnergy = ClayEnergy.ZERO

        override fun drawEnergy(ce: ClayEnergy, simulate: Boolean): Boolean {
            val world = world ?: return false
            val pos = pos ?: return false
            if (!world.canSeeSky(pos.up())) return false

            if (simulate) return true
            clayEnergy += ce
            return true
        }

        override fun completeWork() {
            clayEnergy = ClayEnergy.ZERO
            super.completeWork()
        }

        fun createCeTextWidget(syncManager: PanelSyncManager): TextWidget<*> {
            syncManager.syncValue("clayEnergy", SyncHandlers.longNumber(
                { clayEnergy.energy },
                { clayEnergy = ClayEnergy(it) }
            ))

            return IKey.dynamic { clayEnergy.format() }.asWidget()
        }

        override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, hitData: IProbeHitData) {
            super.addProbeInfo(mode, probeInfo, player, world, state, hitData)
            if (this.isWorking) {
                val cet = recipeCEt * overclockHandler.accelerationFactor
                probeInfo.text("Generating ${TextFormatting.GREEN}${cet.formatWithoutUnit()}${TextFormatting.WHITE} CE/t")
            }
        }
    }

    companion object {
        private val validInputModes = listOf(MachineIoMode.NONE, MachineIoMode.ALL)
    }
}