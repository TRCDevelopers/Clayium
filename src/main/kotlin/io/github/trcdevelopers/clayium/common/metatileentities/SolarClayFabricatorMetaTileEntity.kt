package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.api.drawable.IKey
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.cleanroommc.modularui.widget.ParentWidget
import com.cleanroommc.modularui.widgets.TextWidget
import com.cleanroommc.modularui.widgets.layout.Row
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.Workable
import io.github.trcdevelopers.clayium.api.capability.impl.AbstractRecipeLogic
import io.github.trcdevelopers.clayium.api.capability.impl.ItemHandlerProxy
import io.github.trcdevelopers.clayium.api.capability.impl.NotifiableItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.recipe.RecipeLifecycleHandlerBase
import io.github.trcdevelopers.clayium.api.recipe.RecipeProcessingJob
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.recipe.SolarRecipeProcessor
import io.github.trcdevelopers.clayium.common.recipe.builder.ClayFabricatorRecipeBuilder
import io.github.trcdevelopers.clayium.common.recipe.registry.RecipeRegistry
import io.github.trcdevelopers.clayium.common.util.TransferUtils
import io.github.trcdevelopers.clayium.integration.modularui.MuiSlots
import io.github.trcdevelopers.clayium.integration.modularui.injectShowRecipesButton
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

//TODO: save data compatibility with old Workable implementation
class SolarClayFabricatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    val registry: RecipeRegistry<ClayFabricatorRecipeBuilder>
) : MetaTileEntity(metaTileEntityId, tier, validInputModes, validOutputModesLists[1], "solar_clay_fabricator") {

    override val importItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, false)
    override val exportItems: IItemHandlerModifiable = NotifiableItemStackHandler(this, 1, this, true)
    override val itemInventory: IItemHandler = ItemHandlerProxy(importItems, exportItems)
    val autoIoHandler: AutoIoHandler = AutoIoHandler.Combined(this)

    private val workable = Workable(
        this, SolarRecipeProcessor(this), SolarClayFabricatorLifecycleHandler()
    )

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
        syncManager.syncValue("clayEnergy", SyncHandlers.longNumber(
            { workable.progressTracker.clayEnergy.energy },
            { workable.progressTracker.clayEnergy = ClayEnergy(it) }
        ))

        return super.buildMainParentWidget(syncManager)
            .child(Row().widthRel(0.7f).height(26).align(Alignment.Center)
                .child(MuiSlots.itemSlotBuilder(importItems, 0).singletonSlotGroup(2).buildLarge()
                    .align(Alignment.CenterLeft))
                .child(workable.getProgressBar(syncManager).align(Alignment.Center)
                    .injectShowRecipesButton(this.registry.category.uniqueId))
                .child(MuiSlots.itemSlotBuilder(exportItems, 0).singletonSlotGroup(0).takeOnly().buildLarge()
                    .align(Alignment.CenterRight))
            )
            .child(IKey.dynamic { workable.progressTracker.clayEnergy.formatWithTrailingZeros() }.asWidget()
                .bottom(12).left(0).widthRel(0.5f))
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/solar"))
    }

    private inner class SolarClayFabricatorLifecycleHandler : RecipeLifecycleHandlerBase(this@SolarClayFabricatorMetaTileEntity) {
        override fun trySearchNewRecipe(machineTier: Int, inputs: List<ItemStack>): RecipeProcessingJob? {
            val world = world ?: return null
            val pos = pos ?: return null
            if (!world.canSeeSky(pos.up())) return null

            val recipe = registry.searchRecipe(machineTier, inputs)
                ?: return null

            val outputs = recipe.copyOutputs().take(exportItems.slots)
            if (!TransferUtils.insertToHandler(exportItems, outputs, true)) {
                this.wasOutputsFull = true
                return null
            }

            if (!recipe.matches(true, importItems, machineTier)) return null
            val (cePerTick, duration) = overclockHandler.applyOverclock(recipe.cePerTick, recipe.duration)
            this.itemOutputs = outputs
            return RecipeProcessingJob(
                requiredWork = duration,
                clayEnergyPerTick = ClayEnergy(cePerTick),
            )
        }
    }

    companion object {
        private val validInputModes = listOf(MachineIoMode.NONE, MachineIoMode.ALL)
    }
}